package com.icylib.fancytext;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * 艺术字渲染器。
 *
 * 原理：逐字符调用 drawString，每个字符独立计算颜色与偏移，
 * 因此支持渐变、条带、浮动、波浪等逐字符动画。
 *
 * HUD / Screen 中使用：
 * <pre>{@code
 * FancyTextRenderer.renderCentered(guiGraphics, font, "冰洁柠檬心", w / 2F, 10, FancyTextPresets.SKY_STRIPE);
 * FancyTextRenderer.render(guiGraphics, font, "帕弥什", x, y, "icy_lib:void_whisper");
 * }</pre>
 *
 * 聊天等无法自定义渲染的场景，可转换为带 § 颜色代码的静态近似文本：
 * <pre>{@code
 * player.sendSystemMessage(Component.literal(FancyTextRenderer.toLegacyString("天空", "icy_lib:sky_stripe")));
 * }</pre>
 */
public final class FancyTextRenderer {

    private FancyTextRenderer() {
    }

    /** 剥离文本中的 § 格式代码与 {{...}} 内嵌标记 */
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

    /** 文本渲染总宽度（按剥离格式代码后的内容计算） */
    public static float width(Font font, String text) {
        return font.width(stripFormatting(text));
    }

    // ==================== 渲染 ====================

    public static void render(GuiGraphics guiGraphics, Font font, String text, float x, float y, String styleId) {
        FancyTextStyle style = FancyTextRegistry.get(styleId);
        if (style == null) {
            // 未注册的样式回退为普通白色渲染，保证内容不丢失
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

        // 预计算每个字符宽度与前缀 x
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

    // ==================== 静态 § 近似（聊天/物品名等场景） ====================

    /**
     * 把动态艺术字转换为带 § 颜色代码的静态字符串。
     * 动画被"快照"为当前时刻的颜色（每字符取最近的原版16色），浮动等位移效果无法在纯文本中体现。
     *
     * @param textWidth  文本总宽度（像素）。客户端可用 {@link #width} 精确计算
     */
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

    /** 码点转字符串 */
    private static String codePointString(int codePoint) {
        return new String(Character.toChars(codePoint));
    }

    /**
     * 便捷重载：按注册 ID 转换，宽度按平均字符宽估算（服务端安全，无需 Font）。
     * 需要精确渐变时客户端请使用三参数版本。
     */
    public static String toLegacyString(String text, String styleId) {
        FancyTextStyle style = FancyTextRegistry.get(styleId);
        String clean = stripFormatting(text);
        return toLegacyString(clean, style, clean.length() * 6.0F);
    }
}
