package com.icylib.effects;

import com.icylib.init.ModAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class PiercingEffect extends MobEffect {

    public PiercingEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF4500);
        addAttributeModifier(ModAttributes.PHYSICAL_RESISTANCE,
                ResourceLocation.fromNamespaceAndPath("icy_lib", "piercing_physical_resistance"),
                -0.07, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        return super.applyEffectTick(entity, amplifier);
    }
}
