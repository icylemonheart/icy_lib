package com.icylib.fancytext;

import net.minecraft.network.chat.Style;

import java.util.ArrayList;
import java.util.List;

/**
 * 艺术字内嵌标记协议（文本内嵌样式标记，供全局 Mixin 渲染识别）。
 *
 * 语法：
 * <pre>
 *   {{icy_lib:life_weave}}生命之织缕的契约{{/}}      —— 样式段
 *   {{a}}段1{{/}}普通文本{{b}}段2{{/}}               —— 多段混排
 * </pre>
 * - 开始标记：{@code {{样式ID}}}，样式 ID 建议带 modid 前缀
 * - 结束标记：{@code {{/}}}；省略时特效延伸到文本末尾
 * - 标记在渲染/测量时被剔除，不占宽度、不显示
 * - 样式 ID 未注册时该段按普通文本渲染（标记仍被剔除）
 * - 与原版 § 格式代码互不干扰，可与特效文本模组的 ${...} 标记共存
 *
 * 服务端安全：本类与解析逻辑仅依赖 Style，可在两端使用。
 * 物品名示例：{@code Component.literal("{{icy_lib:life_weave}}生命之织缕的契约{{/}}")}
 */
public final class FancyTextMarkup {

    private static final String CLOSE_MARK = "{{/}}";
    /** 样式 ID 最大长度，防止误吞普通文本 */
    private static final int MAX_STYLE_ID_LENGTH = 64;

    private FancyTextMarkup() {
    }

    /** 带样式的单个字符（样式字符流解析的中间表示） */
    public record StyledChar(Style style, int codePoint) {
    }

    /** 解析结果段：styleId=null 表示普通文本；baseStyle 保留外部样式（斜体等），特效段颜色由效果链接管 */
    public static final class Segment {
        public final String styleId;
        private final StringBuilder text = new StringBuilder();
        private Style baseStyle;

        Segment(String styleId, Style baseStyle) {
            this.styleId = styleId;
            this.baseStyle = baseStyle;
        }

        void append(StyledChar sc) {
            if (text.length() == 0) {
                // 段基础样式取段内第一个字符的样式（保留斜体等，颜色由效果链决定）
                this.baseStyle = sc.style();
            }
            this.text.appendCodePoint(sc.codePoint());
        }

        public String text() {
            return text.toString();
        }

        public Style baseStyle() {
            return baseStyle == null ? Style.EMPTY : baseStyle;
        }
    }

    // ==================== 快速检测 ====================

    /** 快速检测文本中是否存在完整标记（开始与结束符号均出现） */
    public static boolean hasMarkup(String text) {
        if (text == null) {
            return false;
        }
        int open = text.indexOf("{{");
        if (open < 0) {
            return false;
        }
        return text.indexOf("}}", open + 2) > 0 || text.indexOf(CLOSE_MARK, open + 2) > 0;
    }

    /**
     * 提取文本中第一个 {{styleId}} 标记的样式 ID；无标记返回 null。
     * 用于 tooltip 背景检测（不需要完整解析，只需找到第一个样式 ID）。
     */
    public static String extractFirstStyleId(String text) {
        if (text == null) {
            return null;
        }
        int open = text.indexOf("{{");
        if (open < 0) {
            return null;
        }
        int close = text.indexOf("}}", open + 2);
        if (close < 0 || close - open - 2 > MAX_STYLE_ID_LENGTH) {
            return null;
        }
        String id = text.substring(open + 2, close).trim();
        // 排除结束标记 {{/}}
        return id.isEmpty() || id.equals("/") ? null : id;
    }

    // ==================== 剔除标记（测量用） ====================

    /** 剔除所有标记字符，返回实际显示的纯文本（用于宽度计算） */
    public static String strip(String text) {
        if (text == null || !hasMarkup(text)) {
            return text == null ? "" : text;
        }
        StringBuilder out = new StringBuilder(text.length());
        int i = 0;
        int n = text.length();
        while (i < n) {
            char c = text.charAt(i);
            if (c == '{' && i + 1 < n && text.charAt(i + 1) == '{') {
                if (i + 4 < n && text.charAt(i + 2) == '/' && text.charAt(i + 3) == '}' && text.charAt(i + 4) == '}') {
                    i += 5;
                    continue;
                }
                int end = findClose(text, i + 2);
                if (end > 0) {
                    i = end + 2;
                    continue;
                }
            }
            out.append(c);
            i++;
        }
        return out.toString();
    }

    // ==================== 解析 ====================

    /** 解析纯文本（单一样式上下文） */
    public static List<Segment> parse(String text, Style baseStyle) {
        List<StyledChar> chars = new ArrayList<>(text.length());
        text.codePoints().forEach(cp -> chars.add(new StyledChar(baseStyle, cp)));
        return parseStyled(chars);
    }

    /**
     * 在带样式字符流上解析标记（供 Mixin 渲染器使用）。
     * 输入应来自 FormattedCharSequence / FormattedText.visit 展开的结果。
     */
    public static List<Segment> parseStyled(List<StyledChar> chars) {
        List<Segment> out = new ArrayList<>();
        Style firstStyle = chars.isEmpty() ? Style.EMPTY : chars.get(0).style();
        Segment current = new Segment(null, firstStyle);
        int n = chars.size();
        int i = 0;
        while (i < n) {
            StyledChar sc = chars.get(i);
            if (sc.codePoint() == '{' && i + 1 < n && chars.get(i + 1).codePoint() == '{') {
                // 优先匹配结束标记 {{/}}
                if (i + 4 < n && chars.get(i + 2).codePoint() == '/' && chars.get(i + 3).codePoint() == '}'
                        && chars.get(i + 4).codePoint() == '}') {
                    out.add(current);
                    current = new Segment(null, sc.style());
                    i += 5;
                    continue;
                }
                // 匹配开始标记 {{styleId}}
                int end = findCloseStyled(chars, i + 2);
                if (end > 0) {
                    StringBuilder id = new StringBuilder();
                    for (int k = i + 2; k < end; k++) {
                        id.appendCodePoint(chars.get(k).codePoint());
                    }
                    String styleId = id.toString().trim();
                    if (!styleId.isEmpty() && styleId.length() <= MAX_STYLE_ID_LENGTH) {
                        out.add(current);
                        current = new Segment(styleId, sc.style());
                        i = end + 2;
                        continue;
                    }
                }
            }
            current.append(sc);
            i++;
        }
        out.add(current);
        return out;
    }

    // ==================== 内部工具 ====================

    /** String 版:从 from 开始找 "}}"，返回 '}' 首个索引；未找到返回 -1 */
    private static int findClose(String text, int from) {
        int n = text.length();
        int j = from;
        while (j + 1 < n && j - from <= MAX_STYLE_ID_LENGTH) {
            if (text.charAt(j) == '}' && text.charAt(j + 1) == '}') {
                return j;
            }
            j++;
        }
        return -1;
    }

    /** StyledChar 流版:同上 */
    private static int findCloseStyled(List<StyledChar> chars, int from) {
        int n = chars.size();
        int j = from;
        while (j + 1 < n && j - from <= MAX_STYLE_ID_LENGTH) {
            if (chars.get(j).codePoint() == '}' && chars.get(j + 1).codePoint() == '}') {
                return j;
            }
            j++;
        }
        return -1;
    }
}
