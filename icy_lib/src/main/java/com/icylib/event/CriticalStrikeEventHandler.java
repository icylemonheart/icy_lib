package com.icylib.event;

import com.icylib.config.IcyLibConfig;
import com.icylib.init.ModAttributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.lang.reflect.Field;
import java.util.Random;

/**
 * 会心事件处理器 - 处理会心攻击和会心防御
 *
 * 会心计算公式：
 * - 实际会心概率 = (攻击者会心 - 目标会心防御) %
 * - 非玩家实体默认拥有十分之一护甲值的会心防御属性
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
    public void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        DamageContainer container = event.getContainer();
        DamageSource source = container.getSource();
        if (source.getEntity() instanceof LivingEntity attacker) {
            net.minecraft.world.entity.ai.attributes.AttributeInstance attackerAttr =
                    attacker.getAttribute(ModAttributes.CRITICAL_STRIKE_CHANCE);
            double attackerCritical = attackerAttr != null ? attackerAttr.getValue() : 0.0D;

            LivingEntity target = event.getEntity();
            net.minecraft.world.entity.ai.attributes.AttributeInstance targetAttr =
                    target.getAttribute(ModAttributes.CRITICAL_STRIKE_DEFENSE);
            double targetDefense = targetAttr != null ? targetAttr.getValue() : 0.0D;

            if (!(target instanceof Player)) {
                double armorValue = target.getArmorValue();
                double criticalDefenseMultiplier = IcyLibConfig.MONSTER_CRITICAL_DEFENSE_MULTIPLIER.get();
                targetDefense += armorValue * criticalDefenseMultiplier;
            }

            double finalCriticalChance = Math.max(0.0D, attackerCritical - targetDefense) / 100.0D;

            if (finalCriticalChance > 0.0D && RANDOM.nextDouble() < finalCriticalChance) {
                float multiplier = (float) getCriticalMultiplier(attacker);
                float newDamage = container.getNewDamage() * multiplier;
                container.setNewDamage(newDamage);
                setDamageCritical(source, true);
            }
        }
    }

    private double getCriticalMultiplier(LivingEntity attacker) {
        double baseMultiplier = ModAttributes.CRITICAL_STRIKE_MULTIPLIER;

        net.minecraft.world.entity.ai.attributes.AttributeInstance addAttr =
                attacker.getAttribute(ModAttributes.CRITICAL_STRIKE_MULTIPLIER_ADD);
        double addMultiplier = addAttr != null ? addAttr.getValue() : 0.0D;

        net.minecraft.world.entity.ai.attributes.AttributeInstance multAttr =
                attacker.getAttribute(ModAttributes.CRITICAL_STRIKE_MULTIPLIER_MULTIPLY);
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
