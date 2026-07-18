package com.icylib.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Hyper Calculation Space effect - Enter slow-motion state (from Punishing: Gray Raven)
 */
public class HyperCalculationSpaceEffect extends MobEffect {
    public static final float SLOW_MOTION_FACTOR = 0.1F;

    public HyperCalculationSpaceEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x00FFFF);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration > 0;
    }
}