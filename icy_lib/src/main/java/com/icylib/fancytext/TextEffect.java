package com.icylib.fancytext;

/** 艺术字效果接口：按顺序逐字符修改渲染上下文。 */
@FunctionalInterface
public interface TextEffect {

    void apply(CharRenderContext ctx, FancyTextEnv env);
}
