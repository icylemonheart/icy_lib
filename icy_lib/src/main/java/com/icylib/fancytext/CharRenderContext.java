package com.icylib.fancytext;

/** 单字符渲染上下文，效果链依次修改其字段。 */
public final class CharRenderContext {

    public final int index;
    public final int codePoint;
    public final float charX;
    public final float charWidth;

    public int color;
    public float xOffset;
    public float yOffset;
    public float alpha = 1.0F;
    public float highlight = 0.0F;

    public CharRenderContext(int index, int codePoint, float charX, float charWidth, int baseColor) {
        this.index = index;
        this.codePoint = codePoint;
        this.charX = charX;
        this.charWidth = charWidth;
        this.color = baseColor;
    }
}
