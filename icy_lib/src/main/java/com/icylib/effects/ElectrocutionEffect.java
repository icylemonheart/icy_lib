package com.icylib.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import com.icylib.init.ModAttributes;

import java.util.UUID;

public class ElectrocutionEffect extends MobEffect {
    private static final UUID LIGHT_RESISTANCE_UUID = UUID.fromString("c1b2c3d4-e5f6-7890-abcd-ef1234567890");

    public ElectrocutionEffect() {
        super(MobEffectCategory.HARMFUL, 0x00FFFF);
        addAttributeModifier(ModAttributes.LIGHT_RESISTANCE.get(), LIGHT_RESISTANCE_UUID.toString(), -0.15, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        super.applyEffectTick(entity, amplifier);
    }
}