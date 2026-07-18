package com.icylib.event;

import com.icylib.config.IcyLibConfig;
import com.icylib.init.ModAttributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.lang.reflect.Field;
import java.util.Random;

/**
 * Critical strike event handler - Handles critical strike and critical defense
 * 
 * Critical strike formula:
 * - Actual critical chance = (Attacker critical - Target critical defense) %
 * - Non-player entities have default critical defense = 1/10 of armor value
 */
public class CriticalStrikeEventHandler {

    private static final Random RANDOM = new Random();
    private static Field isDamageCriticalField = null;

    static {
        try {
            isDamageCriticalField = DamageSource.class.getDeclaredField("isDamageCritical");
            isDamageCriticalField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            try {
                isDamageCriticalField = DamageSource.class.getDeclaredField("f_141927_");
                isDamageCriticalField.setAccessible(true);
            } catch (NoSuchFieldException e2) {
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (source.getEntity() instanceof LivingEntity attacker) {
            net.minecraft.world.entity.ai.attributes.AttributeInstance attackerAttr = attacker.getAttribute(ModAttributes.CRITICAL_STRIKE_CHANCE.get());
            double attackerCritical = attackerAttr != null ? attackerAttr.getValue() : 0.0D;
            
            LivingEntity target = event.getEntity();
            net.minecraft.world.entity.ai.attributes.AttributeInstance targetAttr = target.getAttribute(ModAttributes.CRITICAL_STRIKE_DEFENSE.get());
            double targetDefense = targetAttr != null ? targetAttr.getValue() : 0.0D;
            
            if (!(target instanceof Player)) {
            double armorValue = target.getArmorValue();
            double criticalDefenseMultiplier = IcyLibConfig.MONSTER_CRITICAL_DEFENSE_MULTIPLIER.get();
            targetDefense += armorValue * criticalDefenseMultiplier;
        }
            
            double finalCriticalChance = Math.max(0.0D, attackerCritical - targetDefense) / 100.0D;
            
            if (finalCriticalChance > 0.0D && RANDOM.nextDouble() < finalCriticalChance) {
                float multiplier = (float) getCriticalMultiplier(attacker);
                float newDamage = event.getAmount() * multiplier;
                event.setAmount(newDamage);
                setDamageCritical(source, true);
            }
        }
    }

    private double getCriticalMultiplier(LivingEntity attacker) {
        double baseMultiplier = ModAttributes.CRITICAL_STRIKE_MULTIPLIER;
        
        net.minecraft.world.entity.ai.attributes.AttributeInstance addAttr = attacker.getAttribute(ModAttributes.CRITICAL_STRIKE_MULTIPLIER_ADD.get());
        double addMultiplier = addAttr != null ? addAttr.getValue() : 0.0D;
        
        net.minecraft.world.entity.ai.attributes.AttributeInstance multAttr = attacker.getAttribute(ModAttributes.CRITICAL_STRIKE_MULTIPLIER_MULTIPLY.get());
        double multiplyMultiplier = multAttr != null ? multAttr.getValue() : 1.0D;
        
        return (baseMultiplier + addMultiplier) * multiplyMultiplier;
    }

    private static void setDamageCritical(DamageSource source, boolean critical) {
        if (isDamageCriticalField != null) {
            try {
                isDamageCriticalField.set(source, critical);
            } catch (IllegalAccessException e) {
            }
        }
    }
}
