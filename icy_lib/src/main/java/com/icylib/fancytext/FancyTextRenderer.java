package com.icylib.fancytext;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/** 艺术字渲染器：逐字符调用 drawString 渲染，支持渐变/条带/浮动/波浪等动画。 */
public final class FancyTextRenderer {

    private FancyTextRenderer() {
    }

    public static String stripFormatting(String text) {
        if (text == null) {
            return "";
        }
        text = FancyTextMarkup.strip(text);
        if (text.indexOf('§') < 0) {
            return text;
        }
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '§' && i + 1 < text.length()) {
                i++;
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static float width(Font font, String text) {
        return font.width(stripFormatting(text));
    }

    public static void render(GuiGraphics guiGraphics, Font font, String text, float x, float y, String styleId) {
        FancyTextStyle style = FancyTextRegistry.get(styleId);
        if (style == null) {
            guiGraphics.drawString(font, stripFormatting(text), (int) x, (int) y, 0xFFFFFF, true);
            return;
        }
        render(guiGraphics, font, text, x, y, style);
    }

    public static void render(GuiGraphics guiGraphics, Font font, String text, float x, float y, FancyTextStyle style) {
        String clean = stripFormatting(text);
        int[] cps = clean.codePoints().toArray();
        int len = cps.length;
        if (len == 0 || style == null) {
            return;
        }

        float[] widths = new float[len];
        float[] xs = new float[len];
        float acc = 0.0F;
        for (int i = 0; i < len; i++) {
            widths[i] = font.width(codePointString(cps[i]));
            xs[i] = acc;
            acc += widths[i];
        }

        FancyTextEnv env = new FancyTextEnv(Util.getMillis() / 1000.0, acc);

        float px = x;
        for (int i = 0; i < len; i++) {
            if (cps[i] == ' ') {
                px += widths[i];
                continue;
            }
            CharRenderContext ctx = new CharRenderContext(i, cps[i], xs[i], widths[i], style.baseColor());
            for (TextEffect effect : style.effects()) {
                effect.apply(ctx, env);
            }
            int finalColor = ColorUtil.withAlpha(ctx.color,
                    ColorUtil.alpha01Of(ctx.color) * ctx.alpha * style.alpha());
            guiGraphics.drawString(font, codePointString(cps[i]), px + ctx.xOffset, y + ctx.yOffset, finalColor, style.shadow());
            px += widths[i];
        }
    }

    public static void renderCentered(GuiGraphics guiGraphics, Font font, String text, float centerX, float y, String styleId) {
        render(guiGraphics, font, text, centerX - width(font, text) / 2.0F, y, styleId);
    }

    public static void renderCentered(GuiGraphics guiGraphics, Font font, String text, float centerX, float y, FancyTextStyle style) {
        render(guiGraphics, font, text, centerX - width(font, text) / 2.0F, y, style);
    }

    /** 把动态艺术字转换为带 § 颜色代码的静态字符串；textWidth 为文本总宽度（像素）。 */
    public static String toLegacyString(String text, FancyTextStyle style, float textWidth) {
        String clean = stripFormatting(text);
        int len = clean.length();
        if (len == 0 || style == null) {
            return clean;
        }
        FancyTextEnv env = new FancyTextEnv(Util.getMillis() / 1000.0, Math.max(textWidth, 1.0F));

        int[] cps = clean.codePoints().toArray();
        len = cps.length;
        StringBuilder sb = new StringBuilder(len * 4);
        char lastCode = 0;
        for (int i = 0; i < len; i++) {
            if (cps[i] == ' ') {
                sb.append(' ');
                continue;
            }
            float charX = len == 1 ? 0.0F : (i / (float) len) * textWidth;
            CharRenderContext ctx = new CharRenderContext(i, cps[i], charX, 6.0F, style.baseColor());
            for (TextEffect effect : style.effects()) {
                effect.apply(ctx, env);
            }
            char code = ColorUtil.nearestVanillaCode(ctx.color);
            if (code != lastCode) {
                sb.append('§').append(code);
                lastCode = code;
            }
            sb.appendCodePoint(cps[i]);
        }
        return sb.toString();
    }

    private static String codePointString(int codePoint) {
        return new String(Character.toChars(codePoint));
    }

    /** 按注册 ID 转换，宽度按平均字符宽估算（服务端安全，无需 Font）。 */
    public static String toLegacyString(String text, String styleId) {
        FancyTextStyle style = FancyTextRegistry.get(styleId);
        String clean = stripFormatting(text);
        return toLegacyString(clean, style, clean.length() * 6.0F);
    }
}
