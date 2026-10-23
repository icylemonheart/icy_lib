package com.icylib.fancytext;

import net.minecraft.resources.ResourceLocation;

/**
 * Tooltip 面板背景样式。
 *
 * 支持三种模式：
 * <ul>
 *   <li>{@link Mode#COLOR}：纯色/渐变背景（原版 fillGradient 替换）</li>
 *   <li>{@link Mode#STARRY_SKY}：程序化流动星空（深色渐变 + 星点闪烁 + 漂移）</li>
 *   <li>{@link Mode#TEXTURE}：PNG 贴图背景（需提供素材，预留接口）</li>
 * </ul>
 *
 * COLOR 模式由 4 个 ARGB 颜色组成（背景顶/底、边框顶/底）。
 * STARRY_SKY 模式使用 {@link StarfieldConfig} 控制星空参数。
 * TEXTURE 模式使用 {@link #texture} 指定贴图路径（目前回退为 COLOR，待接入贴图渲染）。
 */
public record TooltipBackground(Mode mode, int bgTop, int bgBottom, int borderTop, int borderBottom,
                                StarfieldConfig starfield, ResourceLocation texture) {

    public enum Mode { COLOR, STARRY_SKY, TEXTURE }

    /** 原版默认 tooltip 背景：深色半透明 + 紫蓝渐变边框 */
    public static final TooltipBackground VANILLA =
            new TooltipBackground(Mode.COLOR, 0xF0100010, 0xF0100010, 0x505000FF, 0x5028007F, null, null);

    // ==================== COLOR 工厂 ====================

    /** 纯色背景 + 渐变边框（顶色 → 底色） */
    public static TooltipBackground solid(int bg, int borderTop, int borderBottom) {
        return new TooltipBackground(Mode.COLOR, bg, bg, borderTop, borderBottom, null, null);
    }

    /** 纯色背景 + 纯色边框 */
    public static TooltipBackground solid(int bg, int border) {
        return new TooltipBackground(Mode.COLOR, bg, bg, border, border, null, null);
    }

    /** 全自定义：背景垂直渐变 + 边框垂直渐变 */
    public static TooltipBackground gradient(int bgTop, int bgBottom, int borderTop, int borderBottom) {
        return new TooltipBackground(Mode.COLOR, bgTop, bgBottom, borderTop, borderBottom, null, null);
    }

    // ==================== STARRY_SKY 工厂 ====================

    /** 程序化流动星空，使用默认星空参数 */
    public static TooltipBackground starrySky() {
        return starrySky(StarfieldConfig.DEFAULT);
    }

    /** 程序化流动星空，自定义星空参数 */
    public static TooltipBackground starrySky(StarfieldConfig config) {
        // 星空底色：深蓝紫渐变，边框：暗蓝紫
        return new TooltipBackground(Mode.STARRY_SKY, 0xF0050518, 0xF0020210, 0x503344AA, 0x50112266, config, null);
    }

    /** 程序化流动星空，自定义底色与星空参数 */
    public static TooltipBackground starrySky(int bgTop, int bgBottom, int borderTop, int borderBottom,
                                              StarfieldConfig config) {
        return new TooltipBackground(Mode.STARRY_SKY, bgTop, bgBottom, borderTop, borderBottom, config, null);
    }

    // ==================== TEXTURE 工厂（预留） ====================

    /**
     * PNG 贴图背景（预留接口）。
     * 贴图需放置于 assets/&lt;namespace&gt;/textures/&lt;path&gt;.png。
     * 当前版本回退为 COLOR 模式渲染，贴图渲染将在后续版本接入。
     */
    public static TooltipBackground texture(ResourceLocation texture) {
        return new TooltipBackground(Mode.TEXTURE, 0xF0100010, 0xF0100010, 0x505000FF, 0x5028007F, null, texture);
    }
}
