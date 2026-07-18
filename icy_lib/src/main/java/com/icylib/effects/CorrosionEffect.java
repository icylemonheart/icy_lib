package com.icylib.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import com.icylib.init.ModAttributes;

import java.util.UUID;

public class CorrosionEffect extends MobEffect {
    private static final UUID SHADOW_RESISTANCE_UUID = UUID.fromString("d1d2d3d4-e5f6-7890-abcd-ef1234567892");

    public CorrosionEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B008B);
        addAttributeModifier(ModAttributes.SHADOW_RESISTANCE.get(), SHADOW_RESISTANCE_UUID.toString(), -0.15, AttributeModifier.Operation.ADDITION);
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