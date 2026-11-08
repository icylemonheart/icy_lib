package com.icylib.damage;

import com.icylib.IcyLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class IcyDamageTypes {
    public static final ResourceKey<DamageType> PHYSICAL = register("icy_physical");
    public static final ResourceKey<DamageType> ICE = register("icy_ice");
    public static final ResourceKey<DamageType> FIRE = register("icy_fire");
    public static final ResourceKey<DamageType> LIGHT = register("icy_light");
    public static final ResourceKey<DamageType> SHADOW = register("icy_shadow");
    public static final ResourceKey<DamageType> MAGIC = register("icy_magic");
    public static final ResourceKey<DamageType> POISON = register("icy_poison");
    public static final ResourceKey<DamageType> VOID = register("icy_void");
    /** 帕弥什伤害：真实伤害，无法被护甲、药水效果、附魔等抵御 */
    public static final ResourceKey<DamageType> PUNISHING = register("icy_punishing");

    private static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(IcyLib.MODID, name));
    }
}
