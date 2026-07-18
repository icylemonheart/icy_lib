package com.icylib.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import com.icylib.init.ModAttributes;

import java.util.UUID;

public class ScorchingEffect extends MobEffect {
    private static final UUID FIRE_RESISTANCE_UUID = UUID.fromString("b1b2c3d4-e5f6-7890-abcd-ef1234567890");

    public ScorchingEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF6347);
        addAttributeModifier(ModAttributes.FIRE_RESISTANCE.get(), FIRE_RESISTANCE_UUID.toString(), -0.15, AttributeModifier.Operation.ADDITION);
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