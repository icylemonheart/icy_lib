package com.icylib.init;

import com.icylib.IcyLib;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModAttributes {

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, IcyLib.MODID);

    /**
     * Maximum attribute value - 2.1 billion
     */
    public static final double ATTRIBUTE_MAX_VALUE = Integer.MAX_VALUE;

    /**
     * Critical strike chance - Chance to deal critical strike on attack
     * 1 critical strike = 1% chance
     * Actual critical chance = (Critical chance - Critical defense) %
     */
    public static final RegistryObject<Attribute> CRITICAL_STRIKE_CHANCE = ATTRIBUTES.register("critical_strike_chance",
            () -> new RangedAttribute("icy_lib:critical_strike_chance", 0.0D, 0.0D, ATTRIBUTE_MAX_VALUE)
                    .setSyncable(true));

    /**
     * Defense - Reduces damage taken
     * Actual damage = Base damage - Defense value
     * If result is <= 0, damage taken is 0
     */
    public static final RegistryObject<Attribute> DEFENSE = ATTRIBUTES.register("defense",
            () -> new RangedAttribute("icy_lib:defense", 0.0D, 0.0D, ATTRIBUTE_MAX_VALUE)
                    .setSyncable(true));

    /**
     * Critical strike defense - Reduces chance of being critically hit
     * Attacker's actual critical chance = (Attacker critical - Target critical defense) %
     */
    public static final RegistryObject<Attribute> CRITICAL_STRIKE_DEFENSE = ATTRIBUTES.register("critical_strike_defense",
            () -> new RangedAttribute("icy_lib:critical_strike_defense", 0.0D, 0.0D, ATTRIBUTE_MAX_VALUE)
                    .setSyncable(true));

    /**
     * Critical multiplier addition - Additive critical damage multiplier (Base 1.5 + Addition)
     */
    public static final RegistryObject<Attribute> CRITICAL_STRIKE_MULTIPLIER_ADD = ATTRIBUTES.register("critical_strike_multiplier_add",
            () -> new RangedAttribute("icy_lib:critical_strike_multiplier_add", 0.0D, 0.0D, ATTRIBUTE_MAX_VALUE)
                    .setSyncable(true));

    /**
     * Critical multiplier multiply - Multiplicative critical damage multiplier ((Base 1.5 + Addition) × Multiplier)
     */
    public static final RegistryObject<Attribute> CRITICAL_STRIKE_MULTIPLIER_MULTIPLY = ATTRIBUTES.register("critical_strike_multiplier_multiply",
            () -> new RangedAttribute("icy_lib:critical_strike_multiplier_multiply", 1.0D, 1.0D, ATTRIBUTE_MAX_VALUE)
                    .setSyncable(true));

    /**
     * Physical resistance - Reduces physical damage taken
     * Actual physical damage = Base physical damage ÷ Physical resistance
     */
    public static final RegistryObject<Attribute> PHYSICAL_RESISTANCE = ATTRIBUTES.register("physical_resistance",
            () -> new RangedAttribute("icy_lib:physical_resistance", 1.0D, 1.0D, ATTRIBUTE_MAX_VALUE)
                    .setSyncable(true));

    /**
     * Elemental resistance - Reduces elemental damage (general multiplier for elemental damage)
     */
    public static final RegistryObject<Attribute> ELEMENTAL_RESISTANCE = ATTRIBUTES.register("elemental_resistance",
            () -> new RangedAttribute("icy_lib:elemental_resistance", 1.0D, 1.0D, ATTRIBUTE_MAX_VALUE)
                    .setSyncable(true));

    /**
     * Universal resistance - Reduces all damage taken (except Punishing damage)
     */
    public static final RegistryObject<Attribute> UNIVERSAL_RESISTANCE = ATTRIBUTES.register("universal_resistance",
            () -> new RangedAttribute("icy_lib:universal_resistance", 1.0D, 1.0D, ATTRIBUTE_MAX_VALUE)
                    .setSyncable(true));

    /**
     * Ice resistance - Reduces ice damage taken
     */
    public static final RegistryObject<Attribute> ICE_RESISTANCE = ATTRIBUTES.register("ice_resistance",
            () -> new RangedAttribute("icy_lib:ice_resistance", 1.0D, 1.0D, ATTRIBUTE_MAX_VALUE)
                    .setSyncable(true));

    /**
     * Fire resistance - Reduces fire damage taken
     */
    public static final RegistryObject<Attribute> FIRE_RESISTANCE = ATTRIBUTES.register("fire_resistance",
            () -> new RangedAttribute("icy_lib:fire_resistance", 1.0D, 1.0D, ATTRIBUTE_MAX_VALUE)
                    .setSyncable(true));

    /**
     * Light resistance - Reduces light damage taken
     */
    public static final RegistryObject<Attribute> LIGHT_RESISTANCE = ATTRIBUTES.register("light_resistance",
            () -> new RangedAttribute("icy_lib:light_resistance", 1.0D, 1.0D, ATTRIBUTE_MAX_VALUE)
                    .setSyncable(true));

    /**
     * Shadow resistance - Reduces shadow damage taken
     */
    public static final RegistryObject<Attribute> SHADOW_RESISTANCE = ATTRIBUTES.register("shadow_resistance",
            () -> new RangedAttribute("icy_lib:shadow_resistance", 1.0D, 1.0D, ATTRIBUTE_MAX_VALUE)
                    .setSyncable(true));

    /**
     * Magic resistance - Reduces magic damage taken
     */
    public static final RegistryObject<Attribute> MAGIC_RESISTANCE = ATTRIBUTES.register("magic_resistance",
            () -> new RangedAttribute("icy_lib:magic_resistance", 1.0D, 1.0D, ATTRIBUTE_MAX_VALUE)
                    .setSyncable(true));

    /**
     * Poison resistance - Reduces poison damage taken
     */
    public static final RegistryObject<Attribute> POISON_RESISTANCE = ATTRIBUTES.register("poison_resistance",
            () -> new RangedAttribute("icy_lib:poison_resistance", 1.0D, 1.0D, ATTRIBUTE_MAX_VALUE)
                    .setSyncable(true));

    /**
     * Void resistance - Reduces void damage taken
     */
    public static final RegistryObject<Attribute> VOID_RESISTANCE = ATTRIBUTES.register("void_resistance",
            () -> new RangedAttribute("icy_lib:void_resistance", 1.0D, 1.0D, ATTRIBUTE_MAX_VALUE)
                    .setSyncable(true));

    public static final double CRITICAL_STRIKE_MULTIPLIER = 1.5D;
}
