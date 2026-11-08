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

/** Tooltip 艺术字背景检测与着色；星空背景自绘，z=400 与原版背景同层级。 */
public final class FancyTextTooltipEvents {

    private static final int BACKGROUND_Z = 400;
    private static final int PADDING = 3;
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
            event.setBackgroundStart(TRANSPARENT);
            event.setBackgroundEnd(TRANSPARENT);
            event.setBorderStart(TRANSPARENT);
            event.setBorderEnd(TRANSPARENT);
            renderStarryBackground(event, bg);
        } else {
            event.setBackgroundStart(bg.bgTop());
            event.setBackgroundEnd(bg.bgBottom());
            event.setBorderStart(bg.borderTop());
            event.setBorderEnd(bg.borderBottom());
        }
    }

    /** 自绘星空背景（布局复刻原版 PADDING=3），须在原版背景填充前写入。 */
    private static void renderStarryBackground(RenderTooltipEvent.Color event, TooltipBackground bg) {
        GuiGraphics graphics = event.getGraphics();
        Font font = event.getFont();
        List<ClientTooltipComponent> components = event.getComponents();

        int width = 0;
        int height = components.size() == 1 ? -2 : 0;
        for (ClientTooltipComponent component : components) {
            width = Math.max(width, component.getWidth(font));
            height += component.getHeight();
        }

        int x = event.getX();
        int y = event.getY();
        int z = BACKGROUND_Z;

        int i = x - PADDING;
        int j = y - PADDING;
        int k = width + PADDING * 2;
        int l = height + PADDING * 2;

        graphics.fill(i, j - 1, i + k, j, z, bg.bgTop());
        graphics.fill(i, j + l, i + k, j + l + 1, z, bg.bgBottom());
        StarfieldRenderer.render(graphics, i, j, i + k, j + l, z, bg);
        graphics.fillGradient(i - 1, j, i, j + l, z, bg.bgTop(), bg.bgBottom());
        graphics.fillGradient(i + k, j, i + k + 1, j + l, z, bg.bgTop(), bg.bgBottom());
        graphics.fillGradient(i, j + 1, i + 1, j + l - 1, z, bg.borderTop(), bg.borderBottom());
        graphics.fillGradient(i + k - 1, j + 1, i + k, j + l - 1, z, bg.borderTop(), bg.borderBottom());
        graphics.fill(i, j, i + k, j + 1, z, bg.borderTop());
        graphics.fill(i, j + l - 1, i + k, j + l, z, bg.borderBottom());
    }
}
