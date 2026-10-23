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

/**
 * 全局艺术字渲染器（Mixin 专用低层渲染，客户端）。
 *
 * 由 {@link com.icylib.mixin.FontEffectMixin} 在 Font.drawInBatch 拦截后调用：
 * 1. 将文本解析为带样式的段列表（{@link FancyTextMarkup}）
 * 2. 普通段整段交还原版渲染（保留样式与性能）
 * 3. 特效段逐字符应用效果链（颜色/浮动等），再交还原版字形渲染
 *
 * 颜色通过字符 Style.withColor 生效（保留斜体等外部样式，阴影自动按原版规则压暗），
 * 浮动通过逐字符 y 偏移生效。
 */
public final class GlobalFancyTextRenderer {

    /** 重入保护：内部还原版渲染时会再次经过 Mixin 拦截点 */
    private static final ThreadLocal<Boolean> RENDERING = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private GlobalFancyTextRenderer() {
    }

    public static boolean isRendering() {
        return RENDERING.get();
    }

    // ==================== Mixin 入口 ====================

    /** drawInBatch(String) 拦截入口；返回 null 表示未接管（调用方放行原版） */
    public static Integer renderString(Font font, String text, float x, float y, int color, boolean shadow,
                                       Matrix4f matrix, MultiBufferSource buffer, Font.DisplayMode mode,
                                       int bgColor, int packedLight) {
        List<FancyTextMarkup.Segment> segments = FancyTextMarkup.parse(text, Style.EMPTY);
        return renderSegments(font, segments, x, y, color, shadow, matrix, buffer, mode, bgColor, packedLight);
    }

    /** drawInBatch(FormattedCharSequence) 拦截入口（tooltip/GUI/Component 转发都走这里） */
    public static Integer renderSequence(Font font, FormattedCharSequence sequence, float x, float y, int color,
                                         boolean shadow, Matrix4f matrix, MultiBufferSource buffer,
                                         Font.DisplayMode mode, int bgColor, int packedLight) {
        List<FancyTextMarkup.StyledChar> chars = new ArrayList<>(64);
        sequence.accept((index, style, codePoint) -> chars.add(new FancyTextMarkup.StyledChar(style, codePoint)));
        List<FancyTextMarkup.Segment> segments = FancyTextMarkup.parseStyled(chars);
        return renderSegments(font, segments, x, y, color, shadow, matrix, buffer, mode, bgColor, packedLight);
    }

    /** width(FormattedText) 辅助：剔除标记后的宽度 */
    public static int strippedWidth(Font font, String rawText) {
        return font.width(FancyTextMarkup.strip(rawText));
    }

    // ==================== 渲染核心 ====================

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
                    // 普通段：整段原版渲染（保留外部样式颜色/斜体等）
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

    /** 特效段：逐字符跑效果链，返回该段总宽度 */
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
            // 颜色通过字符样式生效（保留外部斜体等）；阴影由原版自动压暗
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

    /** 构造带样式的 FormattedCharSequence */
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
