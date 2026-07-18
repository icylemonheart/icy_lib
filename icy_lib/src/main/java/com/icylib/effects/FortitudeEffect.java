package com.icylib.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Fortitude effect - Immunity to knockback, removes all negative effects every second
 */
public class FortitudeEffect extends MobEffect {
    private static final UUID KNOCKBACK_RESISTANCE_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");

    public FortitudeEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFD700);
        addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE_UUID.toString(), 1.0, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        List<MobEffect> effectsToRemove = new ArrayList<>();
        for (MobEffectInstance instance : entity.getActiveEffects()) {
            MobEffect effect = instance.getEffect();
            if (!effect.isBeneficial()) {
                effectsToRemove.add(effect);
            }
        }
        for (MobEffect effect : effectsToRemove) {
            entity.removeEffect(effect);
        }
    }
}