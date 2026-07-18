package com.icylib.damage;

import com.icylib.IcyLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class IcyDamageTypes {
    /**
     * Physical damage - Damage affected by armor, including fall, suffocation, etc.
     */
    public static final ResourceKey<DamageType> PHYSICAL = register("icy_physical");

    /**
     * Ice damage - Freeze-related damage
     */
    public static final ResourceKey<DamageType> ICE = register("icy_ice");

    /**
     * Fire damage - Fire and explosion-related damage
     */
    public static final ResourceKey<DamageType> FIRE = register("icy_fire");

    /**
     * Light damage - Lightning-related damage
     */
    public static final ResourceKey<DamageType> LIGHT = register("icy_light");

    /**
     * Shadow damage - Wither-related damage
     */
    public static final ResourceKey<DamageType> SHADOW = register("icy_shadow");

    /**
     * Magic damage - Magic-related damage
     */
    public static final ResourceKey<DamageType> MAGIC = register("icy_magic");

    /**
     * Poison damage - Poison-related damage
     */
    public static final ResourceKey<DamageType> POISON = register("icy_poison");

    /**
     * Void damage - Void-related damage
     */
    public static final ResourceKey<DamageType> VOID = register("icy_void");

    /**
     * Punishing damage - True damage that cannot be resisted by armor, potion effects, or enchantments
     */
    public static final ResourceKey<DamageType> PUNISHING = register("icy_punishing");

    private static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(IcyLib.MODID, name));
    }
}