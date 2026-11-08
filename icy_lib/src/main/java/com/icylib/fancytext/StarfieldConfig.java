package com.icylib.fancytext;

/** 程序化星空背景参数；seed 决定星点布局，闪烁与漂移由渲染时钟驱动。 */
public record StarfieldConfig(long seed, int starCount, int minSize, int maxSize,
                              double twinkleSpeed, float twinkleAmount, float driftSpeed, int starColor) {

    public static final StarfieldConfig DEFAULT =
            new StarfieldConfig(0x1C3B5EL, 80, 1, 2, 1.2, 0.6F, 6.0F, 0xFFFFFF);

    public static final StarfieldConfig COOL_WHITE =
            new StarfieldConfig(0xA1B2C3L, 90, 1, 2, 1.5, 0.55F, 5.0F, 0xDDEEFF);

    public static final StarfieldConfig WARM_GOLD =
            new StarfieldConfig(0x5E3B1CL, 70, 1, 3, 1.0, 0.65F, 4.0F, 0xFFE8AA);
}
