package com.icylib.event;

import com.icylib.config.IcyLibConfig;
import com.icylib.init.ModAttributes;
import com.icylib.damage.DamageTypeHelper;
import com.icylib.damage.DamageTypeHelper.DamageCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * 防御事件处理器 - 处理所有抗性和防御属性
 *
 * 伤害计算公式：
 * - 帕弥什伤害：不受任何抗性和防御影响
 * - 物理伤害：实际伤害 = 原伤害 ÷ 物理抗性 ÷ 通用抗性 - 防御
 * - 元素伤害：实际伤害 = 原伤害 ÷ (对应元素抗性 × 元素抗性) ÷ 通用抗性 - 防御
 * - 虚空伤害：实际伤害 = 原伤害 ÷ 虚空抗性 ÷ 通用抗性 - 防御
 */
public class DefenseEventHandler {

    @SubscribeEvent
    public void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();
        DamageContainer container = event.getContainer();
        float damage = container.getNewDamage();

        DamageCategory category = DamageTypeHelper.getDamageCategory(container.getSource());

        if (category == DamageCategory.PUNISHING) {
            return;
        }

        double universalResistance = getAttributeValue(target, ModAttributes.UNIVERSAL_RESISTANCE);
        double defense = getAttributeValue(target, ModAttributes.DEFENSE);

        if (!(target instanceof Player)) {
            double maxHealth = target.getMaxHealth();
            double defenseMultiplier = IcyLibConfig.MONSTER_DEFENSE_MULTIPLIER.get();
            defense += maxHealth * defenseMultiplier;
        }

        switch (category) {
            case PHYSICAL:
                double physicalResistance = getAttributeValue(target, ModAttributes.PHYSICAL_RESISTANCE);
                damage = (float) (damage / physicalResistance / universalResistance);
                break;

            case ICE:
                double iceResistance = getAttributeValue(target, ModAttributes.ICE_RESISTANCE);
                double elementalResistance = getAttributeValue(target, ModAttributes.ELEMENTAL_RESISTANCE);
                damage = (float) (damage / (iceResistance * elementalResistance) / universalResistance);
                break;

            case FIRE:
                double fireResistance = getAttributeValue(target, ModAttributes.FIRE_RESISTANCE);
                elementalResistance = getAttributeValue(target, ModAttributes.ELEMENTAL_RESISTANCE);
                damage = (float) (damage / (fireResistance * elementalResistance) / universalResistance);
                break;

            case LIGHT:
                double lightResistance = getAttributeValue(target, ModAttributes.LIGHT_RESISTANCE);
                elementalResistance = getAttributeValue(target, ModAttributes.ELEMENTAL_RESISTANCE);
                damage = (float) (damage / (lightResistance * elementalResistance) / universalResistance);
                break;

            case SHADOW:
                double shadowResistance = getAttributeValue(target, ModAttributes.SHADOW_RESISTANCE);
                elementalResistance = getAttributeValue(target, ModAttributes.ELEMENTAL_RESISTANCE);
                damage = (float) (damage / (shadowResistance * elementalResistance) / universalResistance);
                break;

            case MAGIC:
                double magicResistance = getAttributeValue(target, ModAttributes.MAGIC_RESISTANCE);
                elementalResistance = getAttributeValue(target, ModAttributes.ELEMENTAL_RESISTANCE);
                damage = (float) (damage / (magicResistance * elementalResistance) / universalResistance);
                break;

            case POISON:
                double poisonResistance = getAttributeValue(target, ModAttributes.POISON_RESISTANCE);
                elementalResistance = getAttributeValue(target, ModAttributes.ELEMENTAL_RESISTANCE);
                damage = (float) (damage / (poisonResistance * elementalResistance) / universalResistance);
                break;

            case VOID:
                double voidResistance = getAttributeValue(target, ModAttributes.VOID_RESISTANCE);
                damage = (float) (damage / voidResistance / universalResistance);
                break;

            case ELEMENTAL_GENERIC:
                elementalResistance = getAttributeValue(target, ModAttributes.ELEMENTAL_RESISTANCE);
                damage = (float) (damage / elementalResistance / universalResistance);
                break;
        }

        damage = (float) (damage - defense);

        if (damage <= 0.0F) {
            if (IcyLibConfig.CANCEL_HIT_ANIMATION_WHEN_NO_DAMAGE.get()) {
                event.setCanceled(true);
                return;
            }
            damage = 0.0F;
        }

        container.setNewDamage(damage);
    }

    private double getAttributeValue(LivingEntity entity, net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute) {
        AttributeInstance attr = entity.getAttribute(attribute);
        return attr != null ? attr.getValue() : 1.0D;
    }
}
