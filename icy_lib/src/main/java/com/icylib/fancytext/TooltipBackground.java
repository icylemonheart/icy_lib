package com.icylib.fancytext;

import net.minecraft.resources.ResourceLocation;

/** Tooltip 面板背景样式：COLOR / STARRY_SKY / TEXTURE 三种模式（TEXTURE 暂回退为 COLOR）。 */
public record TooltipBackground(Mode mode, int bgTop, int bgBottom, int borderTop, int borderBottom,
                                StarfieldConfig starfield, ResourceLocation texture) {

    public enum Mode { COLOR, STARRY_SKY, TEXTURE }

    public static final TooltipBackground VANILLA =
            new TooltipBackground(Mode.COLOR, 0xF0100010, 0xF0100010, 0x505000FF, 0x5028007F, null, null);

    public static TooltipBackground solid(int bg, int borderTop, int borderBottom) {
        return new TooltipBackground(Mode.COLOR, bg, bg, borderTop, borderBottom, null, null);
    }

    public static TooltipBackground solid(int bg, int border) {
        return new TooltipBackground(Mode.COLOR, bg, bg, border, border, null, null);
    }

    public static TooltipBackground gradient(int bgTop, int bgBottom, int borderTop, int borderBottom) {
        return new TooltipBackground(Mode.COLOR, bgTop, bgBottom, borderTop, borderBottom, null, null);
    }

    public static TooltipBackground starrySky() {
        return starrySky(StarfieldConfig.DEFAULT);
    }

    public static TooltipBackground starrySky(StarfieldConfig config) {
        return new TooltipBackground(Mode.STARRY_SKY, 0xF0050518, 0xF0020210, 0x503344AA, 0x50112266, config, null);
    }

    public static TooltipBackground starrySky(int bgTop, int bgBottom, int borderTop, int borderBottom,
                                              StarfieldConfig config) {
        return new TooltipBackground(Mode.STARRY_SKY, bgTop, bgBottom, borderTop, borderBottom, config, null);
    }

    public static TooltipBackground texture(ResourceLocation texture) {
        return new TooltipBackground(Mode.TEXTURE, 0xF0100010, 0xF0100010, 0x505000FF, 0x5028007F, null, texture);
    }
}
