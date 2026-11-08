package com.icylib.fancytext;

import net.minecraft.util.Mth;

/** 艺术字颜色工具：RGB 插值、亮度缩放、HSV、最近原版色映射等。 */
public final class ColorUtil {

    private static final int[] VANILLA_COLORS = {
            0x000000, 0x0000AA, 0x00AA00, 0x00AAAA,
            0xAA0000, 0xAA00AA, 0xFFAA00, 0xAAAAAA,
            0x555555, 0x5555FF, 0x55FF55, 0x55FFFF,
            0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF
    };
    private static final char[] VANILLA_CODES = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    private ColorUtil() {
    }

    public static float clamp01(float t) {
        return t < 0.0F ? 0.0F : Math.min(t, 1.0F);
    }

    public static float smoothstep(float t) {
        t = clamp01(t);
        return t * t * (3.0F - 2.0F * t);
    }

    public static int lerpColor(int from, int to, float t) {
        t = clamp01(t);
        int r = (int) (((from >>> 16) & 0xFF) + (((to >>> 16) & 0xFF) - ((from >>> 16) & 0xFF)) * t);
        int g = (int) (((from >>> 8) & 0xFF) + (((to >>> 8) & 0xFF) - ((from >>> 8) & 0xFF)) * t);
        int b = (int) ((from & 0xFF) + ((to & 0xFF) - (from & 0xFF)) * t);
        return (from & 0xFF000000) | (r << 16) | (g << 8) | b;
    }

    public static int multiplyRGB(int color, float factor) {
        int r = clamp255((int) (((color >>> 16) & 0xFF) * factor));
        int g = clamp255((int) (((color >>> 8) & 0xFF) * factor));
        int b = clamp255((int) ((color & 0xFF) * factor));
        return (color & 0xFF000000) | (r << 16) | (g << 8) | b;
    }

    public static int withAlpha(int rgbColor, float alpha01) {
        int a = clamp255((int) (clamp01(alpha01) * 255.0F));
        return (a << 24) | (rgbColor & 0xFFFFFF);
    }

    public static float alpha01Of(int argb) {
        return ((argb >>> 24) & 0xFF) / 255.0F;
    }

    public static int hsv(float hue, float saturation, float brightness) {
        return 0xFF000000 | Mth.hsvToRgb(hue, saturation, brightness);
    }

    public static int opaque(int color) {
        return (color & 0xFF000000) == 0 ? 0xFF000000 | color : color;
    }

    public static char nearestVanillaCode(int rgb) {
        rgb &= 0xFFFFFF;
        int best = 0;
        int bestDist = Integer.MAX_VALUE;
        for (int i = 0; i < VANILLA_COLORS.length; i++) {
            int dr = ((rgb >>> 16) & 0xFF) - ((VANILLA_COLORS[i] >>> 16) & 0xFF);
            int dg = ((rgb >>> 8) & 0xFF) - ((VANILLA_COLORS[i] >>> 8) & 0xFF);
            int db = (rgb & 0xFF) - (VANILLA_COLORS[i] & 0xFF);
            int dist = dr * dr + dg * dg + db * db;
            if (dist < bestDist) {
                bestDist = dist;
                best = i;
            }
        }
        return VANILLA_CODES[best];
    }

    private static int clamp255(int v) {
        return v < 0 ? 0 : Math.min(v, 255);
    }
}
