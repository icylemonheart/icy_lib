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

import java.util.Set;

/**
 * 伤害类型判定：基于 source.type().msgId()（1.21.1 已移除 getMsgId()）。
 */
public class DamageTypeHelper {

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

    private static final Set<String> ICE_DAMAGE = Set.of(
            "freeze", "ice_spike",
            "ice_magic",
            "icy_ice"
    );

    private static final Set<String> FIRE_DAMAGE = Set.of(
            "fire", "inFire", "onFire", "lava", "hotFloor",
            "explosion", "explosion.player", "fireball", "smallFireball",
            "fire_magic", "fire_field", "dragon_breath_pool",
            "icy_fire"
    );

    private static final Set<String> LIGHT_DAMAGE = Set.of(
            "lightningBolt",
            "lightning_magic", "holy_magic",
            "icy_light"
    );

    private static final Set<String> SHADOW_DAMAGE = Set.of(
            "wither",
            "blood_magic", "eldritch_magic",
            "icy_shadow"
    );

    private static final Set<String> MAGIC_DAMAGE = Set.of(
            "magic",
            "ender_magic", "evocation_magic",
            "icy_magic"
    );

    private static final Set<String> POISON_DAMAGE = Set.of(
            "poison",
            "poison_cloud", "nature_magic", "blood_cauldron",
            "icy_poison"
    );

    private static final Set<String> VOID_DAMAGE = Set.of(
            "outOfWorld",
            "icy_void"
    );

    private static String getMsgId(DamageSource source) {
        return source.type().msgId();
    }

    public static boolean isPhysicalDamage(DamageSource source) {
        String msgId = getMsgId(source);
        return (PHYSICAL_DAMAGE.contains(msgId) ||
               source.is(DamageTypes.MOB_ATTACK) ||
               source.is(DamageTypes.PLAYER_ATTACK)) &&
               !isVoidDamage(source);
    }

    public static boolean isIceDamage(DamageSource source) {
        String msgId = getMsgId(source);
        return ICE_DAMAGE.contains(msgId) ||
               source.is(DamageTypes.FREEZE);
    }

    public static boolean isFireDamage(DamageSource source) {
        String msgId = getMsgId(source);
        return FIRE_DAMAGE.contains(msgId) ||
               source.is(DamageTypes.IN_FIRE) ||
               source.is(DamageTypes.ON_FIRE) ||
               source.is(DamageTypes.LAVA) ||
               source.is(DamageTypes.HOT_FLOOR) ||
               source.is(DamageTypes.EXPLOSION);
    }

    public static boolean isLightDamage(DamageSource source) {
        String msgId = getMsgId(source);
        return LIGHT_DAMAGE.contains(msgId) ||
               source.is(DamageTypes.LIGHTNING_BOLT);
    }

    public static boolean isShadowDamage(DamageSource source) {
        String msgId = getMsgId(source);
        return SHADOW_DAMAGE.contains(msgId) ||
               source.is(DamageTypes.WITHER);
    }

    public static boolean isMagicDamage(DamageSource source) {
        String msgId = getMsgId(source);
        return MAGIC_DAMAGE.contains(msgId) ||
               source.is(DamageTypes.MAGIC);
    }

    public static boolean isPoisonDamage(DamageSource source) {
        String msgId = getMsgId(source);
        return POISON_DAMAGE.contains(msgId) || msgId.equals("poison");
    }

    public static boolean isVoidDamage(DamageSource source) {
        String msgId = getMsgId(source);

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

    public static boolean isPunishingDamage(DamageSource source) {
        String msgId = getMsgId(source);
        return source.typeHolder().is(IcyDamageTypes.PUNISHING) || msgId.equals("heartstop");
    }

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

    public enum DamageCategory {
        PHYSICAL("物理伤害"),
        ICE("寒冰伤害"),
        FIRE("火焰伤害"),
        LIGHT("光明伤害"),
        SHADOW("暗影伤害"),
        MAGIC("魔法伤害"),
        POISON("毒素伤害"),
        VOID("虚空伤害"),
        ELEMENTAL_GENERIC("通用元素伤害"),
        PUNISHING("帕弥什伤害");

        private final String displayName;

        DamageCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

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
