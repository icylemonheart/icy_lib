package com.icylib.quality;

import net.minecraft.client.gui.GuiGraphics;

public final class QualityRenderHelper {

    public static final int SLOT_SIZE = 16;
    private static final int THICK_WIDTH = 2;
    private static final int THIN_WIDTH = 1;
    private static final int LINE_GAP = 1;
    private static final int TOOLTIP_BAND = 6;
    private static final int TOOLTIP_Z = 400;

    private QualityRenderHelper() {
    }

    public static void renderSlotBackground(GuiGraphics graphics, int x, int y, QualityTheme theme) {
        graphics.fillGradient(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0,
                withAlpha(theme.primary(), 0x46), withAlpha(theme.primary(), 0x10));

        int thinStart = x + SLOT_SIZE - THIN_WIDTH - LINE_GAP - THICK_WIDTH;
        graphics.fill(thinStart, y, thinStart + THIN_WIDTH, y + SLOT_SIZE, 0, withAlpha(theme.light(), 0xC8));

        int thickStart = thinStart + THIN_WIDTH + LINE_GAP;
        graphics.fill(thickStart, y, thickStart + THICK_WIDTH, y + SLOT_SIZE, 0, withAlpha(theme.primary(), 0xF2));
    }

    public static void renderTooltipBackground(GuiGraphics graphics, int x, int y, int width, int height,
                                               QualityTheme theme) {
        int left = x - 3;
        int top = y - 3;
        int bandStart = left + width + 6;
        int right = bandStart + TOOLTIP_BAND;
        int bottom = top + height + 6;

        int backgroundTop = withAlpha(tint(theme.primary(), 0.30F), 0xF2);
        int backgroundBottom = withAlpha(tint(theme.primary(), 0.14F), 0xF2);
        int borderTop = withAlpha(theme.primary(), 0x9A);
        int borderBottom = withAlpha(theme.primary(), 0x66);

        graphics.fillGradient(left, top, right, bottom, TOOLTIP_Z, backgroundTop, backgroundBottom);
        graphics.fill(left, top, right, top + 1, TOOLTIP_Z, borderTop);
        graphics.fill(left, bottom - 1, right, bottom, TOOLTIP_Z, borderBottom);
        graphics.fillGradient(left, top + 1, left + 1, bottom - 1, TOOLTIP_Z, borderTop, borderBottom);

        graphics.fill(bandStart, top + 1, bandStart + 1, bottom - 1, TOOLTIP_Z, withAlpha(theme.primary(), 0x70));
        graphics.fill(bandStart + 2, top + 1, bandStart + 4, bottom - 1, TOOLTIP_Z, withAlpha(theme.primary(), 0xF2));
        graphics.fill(bandStart + 5, top + 1, bandStart + 6, bottom - 1, TOOLTIP_Z, withAlpha(theme.light(), 0xE6));
    }

    public static int withAlpha(int rgb, int alpha) {
        return (alpha << 24) | (rgb & 0xFFFFFF);
    }

    private static int tint(int rgb, float factor) {
        int red = Math.min(255, Math.max(0, (int) (((rgb >> 16) & 0xFF) * factor)));
        int green = Math.min(255, Math.max(0, (int) (((rgb >> 8) & 0xFF) * factor)));
        int blue = Math.min(255, Math.max(0, (int) ((rgb & 0xFF) * factor)));
        return (red << 16) | (green << 8) | blue;
    }
}
