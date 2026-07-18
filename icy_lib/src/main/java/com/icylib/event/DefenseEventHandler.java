package com.icylib.event;

import com.icylib.config.IcyLibConfig;
import com.icylib.init.ModAttributes;
import com.icylib.damage.DamageTypeHelper;
import com.icylib.damage.DamageTypeHelper.DamageCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Defense event handler - Handles all resistance and defense attributes
 * 
 * Damage calculation formula:
 * - Punishing damage: Unaffected by any resistance or defense
 * - Physical damage: Actual damage = Base damage ÷ Physical resistance ÷ Universal resistance - Defense
 * - Elemental damage: Actual damage = Base damage ÷ (Element resistance × Elemental resistance) ÷ Universal resistance - Defense
 * - Void damage: Actual damage = Base damage ÷ Void resistance ÷ Universal resistance - Defense
 * 
 * Monster default attributes:
 * - Non-player entities have default defense = 1/10 of max health
 * - Non-player entities have default critical defense = 1/10 of armor value
 */
public class DefenseEventHandler {

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        float damage = event.getAmount();
        
        DamageCategory category = DamageTypeHelper.getDamageCategory(event.getSource());
        
        if (category == DamageCategory.PUNISHING) {
            return;
        }
        
        double universalResistance = getAttributeValue(target, ModAttributes.UNIVERSAL_RESISTANCE.get());
        double defense = getAttributeValue(target, ModAttributes.DEFENSE.get());
        
        if (!(target instanceof Player)) {
            double maxHealth = target.getMaxHealth();
            double defenseMultiplier = IcyLibConfig.MONSTER_DEFENSE_MULTIPLIER.get();
            defense += maxHealth * defenseMultiplier;
        }
        
        switch (category) {
            case PHYSICAL:
                double physicalResistance = getAttributeValue(target, ModAttributes.PHYSICAL_RESISTANCE.get());
                damage = (float) (damage / physicalResistance / universalResistance);
                break;
                
            case ICE:
                double iceResistance = getAttributeValue(target, ModAttributes.ICE_RESISTANCE.get());
                double elementalResistance = getAttributeValue(target, ModAttributes.ELEMENTAL_RESISTANCE.get());
                damage = (float) (damage / (iceResistance * elementalResistance) / universalResistance);
                break;
                
            case FIRE:
                double fireResistance = getAttributeValue(target, ModAttributes.FIRE_RESISTANCE.get());
                elementalResistance = getAttributeValue(target, ModAttributes.ELEMENTAL_RESISTANCE.get());
                damage = (float) (damage / (fireResistance * elementalResistance) / universalResistance);
                break;
                
            case LIGHT:
                double lightResistance = getAttributeValue(target, ModAttributes.LIGHT_RESISTANCE.get());
                elementalResistance = getAttributeValue(target, ModAttributes.ELEMENTAL_RESISTANCE.get());
                damage = (float) (damage / (lightResistance * elementalResistance) / universalResistance);
                break;
                
            case SHADOW:
                double shadowResistance = getAttributeValue(target, ModAttributes.SHADOW_RESISTANCE.get());
                elementalResistance = getAttributeValue(target, ModAttributes.ELEMENTAL_RESISTANCE.get());
                damage = (float) (damage / (shadowResistance * elementalResistance) / universalResistance);
                break;
                
            case MAGIC:
                double magicResistance = getAttributeValue(target, ModAttributes.MAGIC_RESISTANCE.get());
                elementalResistance = getAttributeValue(target, ModAttributes.ELEMENTAL_RESISTANCE.get());
                damage = (float) (damage / (magicResistance * elementalResistance) / universalResistance);
                break;
                
            case POISON:
                double poisonResistance = getAttributeValue(target, ModAttributes.POISON_RESISTANCE.get());
                elementalResistance = getAttributeValue(target, ModAttributes.ELEMENTAL_RESISTANCE.get());
                damage = (float) (damage / (poisonResistance * elementalResistance) / universalResistance);
                break;
                
            case VOID:
                double voidResistance = getAttributeValue(target, ModAttributes.VOID_RESISTANCE.get());
                damage = (float) (damage / voidResistance / universalResistance);
                break;
                
            case ELEMENTAL_GENERIC:
                elementalResistance = getAttributeValue(target, ModAttributes.ELEMENTAL_RESISTANCE.get());
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
        
        event.setAmount(damage);
    }
    
    private double getAttributeValue(LivingEntity entity, net.minecraft.world.entity.ai.attributes.Attribute attribute) {
        AttributeInstance attr = entity.getAttribute(attribute);
        return attr != null ? attr.getValue() : 1.0D;
    }
}