package com.icylib.fancytext;

/**
 * 程序化星空背景参数。
 *
 * 星点位置由 {@link #seed} 确定性生成（保证不随帧跳动），
 * 闪烁与漂移由渲染时钟驱动。
 *
 * @param seed          随机种子（决定星点布局，不同种子星空不同）
 * @param starCount     星点数量（推荐 40~120，越多越密但越耗性能）
 * @param minSize       最小星点尺寸（像素，≥1）
 * @param maxSize       最大星点尺寸（像素，≥minSize）
 * @param twinkleSpeed  闪烁速度（Hz，0 为不闪烁）
 * @param twinkleAmount 闪烁幅度 0~1（1=最暗时完全熄灭）
 * @param driftSpeed    横向漂移速度（像素/秒，0 为静止，负值反向）
 * @param starColor     星点基础颜色（RGB，不含 alpha；闪烁通过 alpha 实现）
 */
public record StarfieldConfig(long seed, int starCount, int minSize, int maxSize,
                              double twinkleSpeed, float twinkleAmount, float driftSpeed, int starColor) {

    public static final StarfieldConfig DEFAULT =
            new StarfieldConfig(0x1C3B5EL, 80, 1, 2, 1.2, 0.6F, 6.0F, 0xFFFFFF);

    /** 冷白星：偏蓝白的冷色调星空 */
    public static final StarfieldConfig COOL_WHITE =
            new StarfieldConfig(0xA1B2C3L, 90, 1, 2, 1.5, 0.55F, 5.0F, 0xDDEEFF);

    /** 暖金星：偏金黄的暖色调星空 */
    public static final StarfieldConfig WARM_GOLD =
            new StarfieldConfig(0x5E3B1CL, 70, 1, 3, 1.0, 0.65F, 4.0F, 0xFFE8AA);
}
