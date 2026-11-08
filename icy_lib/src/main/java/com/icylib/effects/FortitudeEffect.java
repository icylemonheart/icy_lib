package com.icylib.effects;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.ArrayList;
import java.util.List;

/**
 * 坚韧：免疫击退，每秒移除所有负面效果。
 */
public class FortitudeEffect extends MobEffect {

    public FortitudeEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFD700);
        addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE,
                ResourceLocation.fromNamespaceAndPath("icy_lib", "fortitude_knockback_resistance"),
                1.0, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        List<Holder<MobEffect>> effectsToRemove = new ArrayList<>();
        for (MobEffectInstance instance : entity.getActiveEffects()) {
            Holder<MobEffect> effect = instance.getEffect();
            if (!effect.value().isBeneficial()) {
                effectsToRemove.add(effect);
            }
        }
        for (Holder<MobEffect> effect : effectsToRemove) {
            entity.removeEffect(effect);
        }
        return true;
    }
}
