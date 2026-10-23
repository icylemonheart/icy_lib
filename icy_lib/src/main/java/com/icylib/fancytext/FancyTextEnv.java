package com.icylib.fancytext;

/**
 * 艺术字渲染环境（单次渲染调用共享的只读信息）。
 *
 * @param time      动画时钟（秒，来自 Util.getMillis，两端可用）
 * @param textWidth 文本总渲染宽度（像素），用于条带位置、渐变比例等计算
 */
public record FancyTextEnv(double time, float textWidth) {
}
