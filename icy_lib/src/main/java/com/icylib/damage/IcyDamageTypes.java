package com.icylib.damage;

import com.icylib.IcyLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class IcyDamageTypes {
    /** 物理伤害 - 受盔甲影响的伤害，包括摔落、窒息等 */
    public static final ResourceKey<DamageType> PHYSICAL = register("icy_physical");
    /** 寒冰伤害 - 冰冻相关伤害 */
    public static final ResourceKey<DamageType> ICE = register("icy_ice");
    /** 火焰伤害 - 火焰与爆炸相关伤害 */
    public static final ResourceKey<DamageType> FIRE = register("icy_fire");
    /** 光明伤害 - 雷击相关伤害 */
    public static final ResourceKey<DamageType> LIGHT = register("icy_light");
    /** 暗影伤害 - 凋零相关伤害 */
    public static final ResourceKey<DamageType> SHADOW = register("icy_shadow");
    /** 魔法伤害 - 魔法相关伤害 */
    public static final ResourceKey<DamageType> MAGIC = register("icy_magic");
    /** 毒素伤害 - 中毒相关伤害 */
    public static final ResourceKey<DamageType> POISON = register("icy_poison");
    /** 虚空伤害 - 虚空相关伤害 */
    public static final ResourceKey<DamageType> VOID = register("icy_void");
    /** 帕弥什伤害 - 真实伤害，无法被护甲、药水效果、附魔等途径抵御 */
    public static final ResourceKey<DamageType> PUNISHING = register("icy_punishing");

    private static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(IcyLib.MODID, name));
    }
}
