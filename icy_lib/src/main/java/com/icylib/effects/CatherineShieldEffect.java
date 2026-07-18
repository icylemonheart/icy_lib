package com.icylib.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class CatherineShieldEffect extends MobEffect {
    private static final UUID PHYSICAL_DAMAGE_UUID = UUID.fromString("d4e5f6a7-b8c9-0123-4567-89abcdef0123");

    public CatherineShieldEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x00BFFF);
        addAttributeModifier(Attributes.ATTACK_DAMAGE, PHYSICAL_DAMAGE_UUID.toString(), 0.15, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
    }
}