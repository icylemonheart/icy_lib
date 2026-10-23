package com.icylib.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * 超算空间效果 - 进入慢动作状态（来自战双帕弥什的设定）
 */
public class HyperCalculationSpaceEffect extends MobEffect {
    public static final float SLOW_MOTION_FACTOR = 0.1F;

    public HyperCalculationSpaceEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x00FFFF);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration > 0;
    }
}
