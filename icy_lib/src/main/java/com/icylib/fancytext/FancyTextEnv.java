package com.icylib.fancytext;

/** 单次渲染共享的只读环境：动画时间（秒）与文本总宽度（像素）。 */
public record FancyTextEnv(double time, float textWidth) {
}
