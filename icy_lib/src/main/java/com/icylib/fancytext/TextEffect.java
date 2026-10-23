package com.icylib.fancytext;

/**
 * 艺术字效果接口。
 *
 * 所有动态效果（条带扫过、浮动、波浪、脉冲等）都实现此接口，
 * 在渲染时按注册顺序依次应用于每个字符的渲染上下文，
 * 效果可以修改字符的颜色、偏移、透明度和高亮权重。
 *
 * 通过组合多个效果可以叠加出复杂动画（例如：条带扫过 + 高亮浮动）。
 */
@FunctionalInterface
public interface TextEffect {

    /**
     * 对单个字符应用效果。
     *
     * @param ctx 字符渲染上下文（可直接修改）
     * @param env 渲染环境（全局时间、文本总宽度等只读信息）
     */
    void apply(CharRenderContext ctx, FancyTextEnv env);
}
