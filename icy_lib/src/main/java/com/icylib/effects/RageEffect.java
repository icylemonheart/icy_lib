package com.icylib.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class RageEffect extends MobEffect {
    public RageEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF4500);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}