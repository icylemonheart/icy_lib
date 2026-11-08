package com.icylib.fancytext;

import net.minecraft.network.chat.Style;

import java.util.ArrayList;
import java.util.List;

/** 内嵌标记协议：{{styleId}} 开启样式段、{{/}} 结束；标记在渲染与测量时剔除。 */
public final class FancyTextMarkup {

    private static final String CLOSE_MARK = "{{/}}";
    private static final int MAX_STYLE_ID_LENGTH = 64;

    private FancyTextMarkup() {
    }

    public record StyledChar(Style style, int codePoint) {
    }

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
        return id.isEmpty() || id.equals("/") ? null : id;
    }

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

    public static List<Segment> parse(String text, Style baseStyle) {
        List<StyledChar> chars = new ArrayList<>(text.length());
        text.codePoints().forEach(cp -> chars.add(new StyledChar(baseStyle, cp)));
        return parseStyled(chars);
    }

    public static List<Segment> parseStyled(List<StyledChar> chars) {
        List<Segment> out = new ArrayList<>();
        Style firstStyle = chars.isEmpty() ? Style.EMPTY : chars.get(0).style();
        Segment current = new Segment(null, firstStyle);
        int n = chars.size();
        int i = 0;
        while (i < n) {
            StyledChar sc = chars.get(i);
            if (sc.codePoint() == '{' && i + 1 < n && chars.get(i + 1).codePoint() == '{') {
                if (i + 4 < n && chars.get(i + 2).codePoint() == '/' && chars.get(i + 3).codePoint() == '}'
                        && chars.get(i + 4).codePoint() == '}') {
                    out.add(current);
                    current = new Segment(null, sc.style());
                    i += 5;
                    continue;
                }
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
