package com.icylib.fancytext;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/** 全局艺术字渲染器：由 FontEffectMixin 拦截 Font.drawInBatch 后调用。 */
public final class GlobalFancyTextRenderer {

    /** 重入保护：内部还原版渲染时会再次经过 Mixin 拦截点 */
    private static final ThreadLocal<Boolean> RENDERING = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private GlobalFancyTextRenderer() {
    }

    public static boolean isRendering() {
        return RENDERING.get();
    }

    public static Integer renderString(Font font, String text, float x, float y, int color, boolean shadow,
                                       Matrix4f matrix, MultiBufferSource buffer, Font.DisplayMode mode,
                                       int bgColor, int packedLight) {
        List<FancyTextMarkup.Segment> segments = FancyTextMarkup.parse(text, Style.EMPTY);
        return renderSegments(font, segments, x, y, color, shadow, matrix, buffer, mode, bgColor, packedLight);
    }

    public static Integer renderSequence(Font font, FormattedCharSequence sequence, float x, float y, int color,
                                         boolean shadow, Matrix4f matrix, MultiBufferSource buffer,
                                         Font.DisplayMode mode, int bgColor, int packedLight) {
        List<FancyTextMarkup.StyledChar> chars = new ArrayList<>(64);
        sequence.accept((index, style, codePoint) -> chars.add(new FancyTextMarkup.StyledChar(style, codePoint)));
        List<FancyTextMarkup.Segment> segments = FancyTextMarkup.parseStyled(chars);
        return renderSegments(font, segments, x, y, color, shadow, matrix, buffer, mode, bgColor, packedLight);
    }

    public static int strippedWidth(Font font, String rawText) {
        return font.width(FancyTextMarkup.strip(rawText));
    }

    private static Integer renderSegments(Font font, List<FancyTextMarkup.Segment> segments, float x, float y,
                                          int color, boolean shadow, Matrix4f matrix, MultiBufferSource buffer,
                                          Font.DisplayMode mode, int bgColor, int packedLight) {
        RENDERING.set(true);
        try {
            float cursor = x;
            for (FancyTextMarkup.Segment seg : segments) {
                String segText = seg.text();
                if (segText.isEmpty()) {
                    continue;
                }
                FancyTextStyle style = seg.styleId != null ? FancyTextRegistry.get(seg.styleId) : null;
                if (style == null) {
                    FormattedCharSequence seq = styledSequence(segText, seg.baseStyle());
                    font.drawInBatch(seq, cursor, y, color, shadow, matrix, buffer, mode, bgColor, packedLight);
                    cursor += font.width(segText);
                } else {
                    cursor += renderStyledSegment(font, seg, style, cursor, y, color, shadow, matrix, buffer, mode, bgColor, packedLight);
                }
            }
            return Math.round(cursor);
        } finally {
            RENDERING.set(false);
        }
    }

    private static float renderStyledSegment(Font font, FancyTextMarkup.Segment seg, FancyTextStyle style,
                                             float x, float y, int color, boolean shadow, Matrix4f matrix,
                                             MultiBufferSource buffer, Font.DisplayMode mode, int bgColor,
                                             int packedLight) {
        int[] cps = seg.text().codePoints().toArray();
        int len = cps.length;
        if (len == 0) {
            return 0.0F;
        }
        float[] widths = new float[len];
        float total = 0.0F;
        for (int i = 0; i < len; i++) {
            widths[i] = font.width(codePointString(cps[i]));
            total += widths[i];
        }

        FancyTextEnv env = new FancyTextEnv(Util.getMillis() / 1000.0, total);
        float cursor = x;
        for (int i = 0; i < len; i++) {
            if (cps[i] == ' ') {
                cursor += widths[i];
                continue;
            }
            CharRenderContext ctx = new CharRenderContext(i, cps[i], cursor - x, widths[i], style.baseColor());
            for (TextEffect effect : style.effects()) {
                effect.apply(ctx, env);
            }
            Style glyphStyle = seg.baseStyle().withColor(TextColor.fromRgb(ctx.color & 0xFFFFFF));
            FormattedCharSequence seq = styledSequence(codePointString(cps[i]), glyphStyle);
            font.drawInBatch(seq, cursor + ctx.xOffset, y + ctx.yOffset, color, shadow, matrix, buffer, mode, bgColor, packedLight);
            cursor += widths[i];
        }
        return total;
    }

    private static String codePointString(int codePoint) {
        return new String(Character.toChars(codePoint));
    }

    private static FormattedCharSequence styledSequence(String text, Style style) {
        int[] cps = text.codePoints().toArray();
        return consumer -> {
            for (int i = 0; i < cps.length; i++) {
                if (!consumer.accept(i, style, cps[i])) {
                    return false;
                }
            }
            return true;
        };
    }
}
