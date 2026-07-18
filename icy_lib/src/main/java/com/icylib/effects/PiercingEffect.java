package com.icylib.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import com.icylib.init.ModAttributes;

import java.util.UUID;

public class PiercingEffect extends MobEffect {
    private static final UUID PHYSICAL_RESISTANCE_UUID = UUID.fromString("c1c2c3d4-e5f6-7890-abcd-ef1234567891");

    public PiercingEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF4500);
        addAttributeModifier(ModAttributes.PHYSICAL_RESISTANCE.get(), PHYSICAL_RESISTANCE_UUID.toString(), -0.07, AttributeModifier.Operation.ADDITION);
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