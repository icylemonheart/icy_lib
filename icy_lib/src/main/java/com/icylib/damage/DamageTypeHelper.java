package com.icylib.damage;

import com.icylib.IcyLib;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

@Mod.EventBusSubscriber(modid = IcyLib.MODID)
public class DamageTypeHelper {
    /**
     * Physical damage - Damage mainly affected by armor, including fall, suffocation, drowning, starvation, etc.
     */
    private static final Set<String> PHYSICAL_DAMAGE = Set.of(
            "player", "playerAttack", "mob", "mobAttack",
            "arrow", "trident", "thrown", "projectile",
            "fallingBlock", "anvil", "witherSkull",
            "dragonBreath", "fireball", "smallFireball",
            "enderDragon", "sting", "thorn",
            "fall", "flyIntoWall", "suffocation",
            "sweetBerryBush", "dryout", "sonic_boom",
            "generic", "mobAttackNoAggro", "mobProjectile",
            "drown", "starvation",
            "tacz.bullet",
            "icy_physical"
    );

    /**
     * Ice damage - Freeze-related damage
     */
    private static final Set<String> ICE_DAMAGE = Set.of(
            "freeze", "ice_spike",
            "ice_magic",
            "icy_ice"
    );

    /**
     * Fire damage - Fire and explosion-related damage
     */
    private static final Set<String> FIRE_DAMAGE = Set.of(
            "fire", "inFire", "onFire", "lava", "hotFloor",
            "explosion", "explosion.player", "fireball", "smallFireball",
            "fire_magic", "fire_field", "dragon_breath_pool",
            "icy_fire"
    );

    /**
     * Light damage - Lightning-related damage
     */
    private static final Set<String> LIGHT_DAMAGE = Set.of(
            "lightningBolt",
            "lightning_magic", "holy_magic",
            "icy_light"
    );

    /**
     * Shadow damage - Wither-related damage
     */
    private static final Set<String> SHADOW_DAMAGE = Set.of(
            "wither",
            "blood_magic", "eldritch_magic",
            "icy_shadow"
    );

    /**
     * Magic damage - Magic-related damage
     */
    private static final Set<String> MAGIC_DAMAGE = Set.of(
            "magic",
            "ender_magic", "evocation_magic",
            "icy_magic"
    );

    /**
     * Poison damage - Poison-related damage
     */
    private static final Set<String> POISON_DAMAGE = Set.of(
            "poison",
            "poison_cloud", "nature_magic", "blood_cauldron",
            "icy_poison"
    );

    /**
     * Void damage - Void-related damage
     */
    private static final Set<String> VOID_DAMAGE = Set.of(
            "outOfWorld",
            "icy_void"
    );
    
    

    /**
     * Check if damage is physical damage
     */
    public static boolean isPhysicalDamage(DamageSource source) {
        String msgId = source.getMsgId();
        return (PHYSICAL_DAMAGE.contains(msgId) ||
               source.is(DamageTypes.MOB_ATTACK) ||
               source.is(DamageTypes.PLAYER_ATTACK)) &&
               !isVoidDamage(source);
    }

    /**
     * Check if damage is ice damage
     */
    public static boolean isIceDamage(DamageSource source) {
        String msgId = source.getMsgId();
        return ICE_DAMAGE.contains(msgId) ||
               source.is(DamageTypes.FREEZE);
    }

    /**
     * Check if damage is fire damage
     */
    public static boolean isFireDamage(DamageSource source) {
        String msgId = source.getMsgId();
        return FIRE_DAMAGE.contains(msgId) ||
               source.is(DamageTypes.IN_FIRE) ||
               source.is(DamageTypes.ON_FIRE) ||
               source.is(DamageTypes.LAVA) ||
               source.is(DamageTypes.HOT_FLOOR) ||
               source.is(DamageTypes.EXPLOSION);
    }

    /**
     * Check if damage is light damage
     */
    public static boolean isLightDamage(DamageSource source) {
        String msgId = source.getMsgId();
        return LIGHT_DAMAGE.contains(msgId) ||
               source.is(DamageTypes.LIGHTNING_BOLT);
    }

    /**
     * Check if damage is shadow damage
     */
    public static boolean isShadowDamage(DamageSource source) {
        String msgId = source.getMsgId();
        return SHADOW_DAMAGE.contains(msgId) ||
               source.is(DamageTypes.WITHER);
    }

    /**
     * Check if damage is magic damage
     */
    public static boolean isMagicDamage(DamageSource source) {
        String msgId = source.getMsgId();
        return MAGIC_DAMAGE.contains(msgId) ||
               source.is(DamageTypes.MAGIC);
    }

    /**
     * Check if damage is poison damage
     */
    public static boolean isPoisonDamage(DamageSource source) {
        String msgId = source.getMsgId();
        return POISON_DAMAGE.contains(msgId) || msgId.equals("poison");
    }

    /**
     * Check if damage is void damage
     */
    public static boolean isVoidDamage(DamageSource source) {
        String msgId = source.getMsgId();
        
        if (VOID_DAMAGE.contains(msgId) || msgId.equals("outOfWorld")) {
            return true;
        }
        
        if (msgId.equals("tacz.bullet")) {
            return source.typeHolder().unwrapKey().map(key -> {
                ResourceLocation location = key.location();
                return "tacz:bullet_void".equals(location.toString()) || 
                       "tacz:bullet_void_ignore_armor".equals(location.toString());
            }).orElse(false);
        }
        
        return false;
    }

    /**
     * Check if damage is elemental damage (Ice, Fire, Light, Shadow, Magic, Poison, Generic Elemental)
     */
    public static boolean isElementalDamage(DamageSource source) {
        DamageCategory category = getDamageCategory(source);
        return category == DamageCategory.ICE ||
               category == DamageCategory.FIRE ||
               category == DamageCategory.LIGHT ||
               category == DamageCategory.SHADOW ||
               category == DamageCategory.MAGIC ||
               category == DamageCategory.POISON ||
               category == DamageCategory.ELEMENTAL_GENERIC;
    }

    /**
     * Check if damage is punishing damage (true damage)
     */
    public static boolean isPunishingDamage(DamageSource source) {
        String msgId = source.getMsgId();
        return source.is(IcyDamageTypes.PUNISHING) || msgId.equals("heartstop");
    }

    /**
     * Get damage category
     */
    public static DamageCategory getDamageCategory(DamageSource source) {
        if (isPunishingDamage(source)) return DamageCategory.PUNISHING;
        if (isIceDamage(source)) return DamageCategory.ICE;
        if (isFireDamage(source)) return DamageCategory.FIRE;
        if (isLightDamage(source)) return DamageCategory.LIGHT;
        if (isShadowDamage(source)) return DamageCategory.SHADOW;
        if (isMagicDamage(source)) return DamageCategory.MAGIC;
        if (isPoisonDamage(source)) return DamageCategory.POISON;
        if (isVoidDamage(source)) return DamageCategory.VOID;
        if (isPhysicalDamage(source)) return DamageCategory.PHYSICAL;
        return DamageCategory.ELEMENTAL_GENERIC;
    }

    /**
     * Damage category enum
     */
    public enum DamageCategory {
        PHYSICAL("Physical"),
        ICE("Ice"),
        FIRE("Fire"),
        LIGHT("Light"),
        SHADOW("Shadow"),
        MAGIC("Magic"),
        POISON("Poison"),
        VOID("Void"),
        ELEMENTAL_GENERIC("Elemental"),
        PUNISHING("Punishing");

        private final String displayName;

        DamageCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Create damage source from damage type key
     *
     * @param attacker      The attacking entity
     * @param damageTypeKey The ResourceKey of the damage type
     * @return Created DamageSource, or null if creation fails
     */
    public static DamageSource createDamageSource(LivingEntity attacker, ResourceKey<DamageType> damageTypeKey) {
        if (attacker == null || attacker.level().isClientSide()) {
            return null;
        }
        Holder<DamageType> damageTypeHolder = attacker.level().registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolder(damageTypeKey)
                .orElse(null);
        if (damageTypeHolder != null) {
            return new DamageSource(damageTypeHolder, attacker);
        }
        return null;
    }
}