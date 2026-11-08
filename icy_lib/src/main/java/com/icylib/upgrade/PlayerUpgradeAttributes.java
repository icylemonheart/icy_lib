package com.icylib.upgrade;

import com.icylib.init.ModAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

/** 将玩家晋升/升级属性以 AttributeModifier 应用到玩家身上。 */
public class PlayerUpgradeAttributes {
    private static final ResourceLocation HEALTH_ID = ResourceLocation.fromNamespaceAndPath("icy_lib", "player_upgrade_health");
    private static final ResourceLocation ATTACK_ID = ResourceLocation.fromNamespaceAndPath("icy_lib", "player_upgrade_attack");
    private static final ResourceLocation DEFENSE_ID = ResourceLocation.fromNamespaceAndPath("icy_lib", "player_upgrade_defense");
    private static final ResourceLocation CRITICAL_ID = ResourceLocation.fromNamespaceAndPath("icy_lib", "player_upgrade_critical");

    public static void apply(Player player, PlayerUpgradeData data) {
        if (player == null) return;

        var healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) {
            healthAttr.removeModifier(HEALTH_ID);
            if (data.getMaxHealthBonus() > 0) {
                healthAttr.addPermanentModifier(new AttributeModifier(
                        HEALTH_ID,
                        data.getMaxHealthBonus(),
                        AttributeModifier.Operation.ADD_VALUE
                ));
            }
        }

        var attackAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttr != null) {
            attackAttr.removeModifier(ATTACK_ID);
            if (data.getAttackDamageBonus() > 0) {
                attackAttr.addPermanentModifier(new AttributeModifier(
                        ATTACK_ID,
                        data.getAttackDamageBonus(),
                        AttributeModifier.Operation.ADD_VALUE
                ));
            }
        }

        var defenseAttr = player.getAttribute(ModAttributes.DEFENSE);
        if (defenseAttr != null) {
            defenseAttr.removeModifier(DEFENSE_ID);
            if (data.getDefenseBonus() > 0) {
                defenseAttr.addPermanentModifier(new AttributeModifier(
                        DEFENSE_ID,
                        data.getDefenseBonus(),
                        AttributeModifier.Operation.ADD_VALUE
                ));
            }
        }

        var criticalAttr = player.getAttribute(ModAttributes.CRITICAL_STRIKE_CHANCE);
        if (criticalAttr != null) {
            criticalAttr.removeModifier(CRITICAL_ID);
            if (data.getCriticalBonus() > 0) {
                criticalAttr.addPermanentModifier(new AttributeModifier(
                        CRITICAL_ID,
                        data.getCriticalBonus(),
                        AttributeModifier.Operation.ADD_VALUE
                ));
            }
        }
    }

    public static void clear(Player player) {
        if (player == null) return;

        var healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) healthAttr.removeModifier(HEALTH_ID);

        var attackAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttr != null) attackAttr.removeModifier(ATTACK_ID);

        var defenseAttr = player.getAttribute(ModAttributes.DEFENSE);
        if (defenseAttr != null) defenseAttr.removeModifier(DEFENSE_ID);

        var criticalAttr = player.getAttribute(ModAttributes.CRITICAL_STRIKE_CHANCE);
        if (criticalAttr != null) criticalAttr.removeModifier(CRITICAL_ID);
    }
}
