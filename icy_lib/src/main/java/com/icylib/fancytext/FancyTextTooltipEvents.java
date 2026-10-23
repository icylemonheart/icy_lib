package com.icylib.fancytext;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

/**
 * Tooltip 艺术字背景检测与着色。
 *
 * <ul>
 *   <li>{@link ItemTooltipEvent}：扫描 tooltip 每一行是否包含 {{styleId}} 标记，
 *       若找到第一个带 {@link TooltipBackground} 的样式，存入 {@link TooltipBackgroundHolder}；</li>
 *   <li>{@link RenderTooltipEvent.Color}：
 *     <ul>
 *       <li>COLOR/TEXTURE 模式：通过事件 setter 替换背景与边框颜色（NeoForge 原生支持）；</li>
 *       <li>STARRY_SKY 模式：将原版 4 个颜色置为全透明（原版背景不可见），
 *           随即在同一批次内自绘星空背景与边框（z=400，与原版背景同层级，
 *           先于原版透明填充写入，文字随后渲染在星空之上）。</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * 全程不使用 Mixin，避免 Forge 补丁方法（10 参数 renderTooltipBackground）无混淆映射的问题。
 *
 * 由 {@link com.icylib.IcyLib} 在客户端环境通过 {@code NeoForge.EVENT_BUS.register(...)} 显式注册，
 * 不再使用 1.20.1 的 {@code @Mod.EventBusSubscriber} 注解。
 */
public final class FancyTextTooltipEvents {

    /** 原版 tooltip 背景绘制层级（GuiGraphics 中固定为 400） */
    private static final int BACKGROUND_Z = 400;
    /** 原版背景内边距 */
    private static final int PADDING = 3;
    /** 全透明颜色（让原版背景/边框填充不可见） */
    private static final int TRANSPARENT = 0x00000000;

    private FancyTextTooltipEvents() {
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        TooltipBackground background = null;
        for (Component line : event.getToolTip()) {
            String styleId = FancyTextMarkup.extractFirstStyleId(line.getString());
            if (styleId == null) {
                continue;
            }
            FancyTextStyle style = FancyTextRegistry.get(styleId);
            if (style != null && style.tooltipBackground() != null) {
                background = style.tooltipBackground();
                break;
            }
        }
        TooltipBackgroundHolder.set(background);
    }

    @SubscribeEvent
    public static void onRenderTooltipColor(RenderTooltipEvent.Color event) {
        TooltipBackground bg = TooltipBackgroundHolder.get();
        if (bg == null) {
            return;
        }

        if (bg.mode() == TooltipBackground.Mode.STARRY_SKY) {
            // 原版背景/边框置为全透明，改由我们自绘星空
            event.setBackgroundStart(TRANSPARENT);
            event.setBackgroundEnd(TRANSPARENT);
            event.setBorderStart(TRANSPARENT);
            event.setBorderEnd(TRANSPARENT);
            renderStarryBackground(event, bg);
        } else {
            // COLOR / TEXTURE（TEXTURE 暂回退为纯色）：直接替换颜色
            event.setBackgroundStart(bg.bgTop());
            event.setBackgroundEnd(bg.bgBottom());
            event.setBorderStart(bg.borderTop());
            event.setBorderEnd(bg.borderBottom());
        }
    }

    /**
     * 自绘星空背景，布局复刻 TooltipRenderUtil.renderTooltipBackground（PADDING=3）。
     * 在原版背景填充之前写入同一绘制批次，透明的原版填充不会覆盖星空。
     */
    private static void renderStarryBackground(RenderTooltipEvent.Color event, TooltipBackground bg) {
        GuiGraphics graphics = event.getGraphics();
        Font font = event.getFont();
        List<ClientTooltipComponent> components = event.getComponents();

        // 复刻 GuiGraphics.renderTooltipInternal 的宽高计算
        int width = 0;
        int height = components.size() == 1 ? -2 : 0;
        for (ClientTooltipComponent component : components) {
            width = Math.max(width, component.getWidth(font));
            height += component.getHeight();
        }

        int x = event.getX();
        int y = event.getY();
        int z = BACKGROUND_Z;

        // 背景矩形（含 3px 内边距）
        int i = x - PADDING;
        int j = y - PADDING;
        int k = width + PADDING * 2;
        int l = height + PADDING * 2;

        // 上下扩展横线
        graphics.fill(i, j - 1, i + k, j, z, bg.bgTop());
        graphics.fill(i, j + l, i + k, j + l + 1, z, bg.bgBottom());
        // 星空主背景（底色渐变 + 星点）
        StarfieldRenderer.render(graphics, i, j, i + k, j + l, z, bg);
        // 左右扩展竖线渐变
        graphics.fillGradient(i - 1, j, i, j + l, z, bg.bgTop(), bg.bgBottom());
        graphics.fillGradient(i + k, j, i + k + 1, j + l, z, bg.bgTop(), bg.bgBottom());
        // 边框（原版 renderFrameGradient 布局）
        graphics.fillGradient(i, j + 1, i + 1, j + l - 1, z, bg.borderTop(), bg.borderBottom());
        graphics.fillGradient(i + k - 1, j + 1, i + k, j + l - 1, z, bg.borderTop(), bg.borderBottom());
        graphics.fill(i, j, i + k, j + 1, z, bg.borderTop());
        graphics.fill(i, j + l - 1, i + k, j + l, z, bg.borderBottom());
    }
}
