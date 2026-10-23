package com.icylib.fancytext;

/**
 * 单个字符的渲染上下文。
 *
 * 效果链在渲染时逐字符构建此上下文并依次修改字段，
 * 最终渲染器根据 color / xOffset / yOffset / alpha 输出字符。
 */
public final class CharRenderContext {

    /** 可见字符索引（已剥离格式代码与标记） */
    public final int index;
    /** 字符码点（支持增补平面字符） */
    public final int codePoint;
    /** 字符在文本中的起始 x 位置（像素，相对文本起点） */
    public final float charX;
    /** 字符渲染宽度（像素） */
    public final float charWidth;

    /** 当前颜色 ARGB（效果链逐步修改；RGB 被效果修改，alpha 通常保留） */
    public int color;
    /** 渲染 x 偏移（像素） */
    public float xOffset;
    /** 渲染 y 偏移（像素，负值向上） */
    public float yOffset;
    /** 附加透明度系数 0~1（与样式全局透明度相乘） */
    public float alpha = 1.0F;
    /** 高亮权重 0~1（供效果之间参考，如条带扫过设置后被浮动效果读取） */
    public float highlight = 0.0F;

    public CharRenderContext(int index, int codePoint, float charX, float charWidth, int baseColor) {
        this.index = index;
        this.codePoint = codePoint;
        this.charX = charX;
        this.charWidth = charWidth;
        this.color = baseColor;
    }
}
