package com.icylib.init;

import com.icylib.IcyLib;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModAttributes {

    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, IcyLib.MODID);

    public static final double ATTRIBUTE_MAX_VALUE = Integer.MAX_VALUE;

    public static final DeferredHolder<Attribute, Attribute> CRITICAL_STRIKE_CHANCE =
            ATTRIBUTES.register("critical_strike_chance", () -> new RangedAttribute("icy_lib:critical_strike_chance", 0.0D, 0.0D, ATTRIBUTE_MAX_VALUE).setSyncable(true));

    public static final DeferredHolder<Attribute, Attribute> DEFENSE =
            ATTRIBUTES.register("defense", () -> new RangedAttribute("icy_lib:defense", 0.0D, 0.0D, ATTRIBUTE_MAX_VALUE).setSyncable(true));

    public static final DeferredHolder<Attribute, Attribute> CRITICAL_STRIKE_DEFENSE =
            ATTRIBUTES.register("critical_strike_defense", () -> new RangedAttribute("icy_lib:critical_strike_defense", 0.0D, 0.0D, ATTRIBUTE_MAX_VALUE).setSyncable(true));

    public static final DeferredHolder<Attribute, Attribute> CRITICAL_STRIKE_MULTIPLIER_ADD =
            ATTRIBUTES.register("critical_strike_multiplier_add", () -> new RangedAttribute("icy_lib:critical_strike_multiplier_add", 0.0D, 0.0D, ATTRIBUTE_MAX_VALUE).setSyncable(true));

    public static final DeferredHolder<Attribute, Attribute> CRITICAL_STRIKE_MULTIPLIER_MULTIPLY =
            ATTRIBUTES.register("critical_strike_multiplier_multiply", () -> new RangedAttribute("icy_lib:critical_strike_multiplier_multiply", 1.0D, 0.01D, ATTRIBUTE_MAX_VALUE).setSyncable(true));

    public static final DeferredHolder<Attribute, Attribute> PHYSICAL_RESISTANCE =
            ATTRIBUTES.register("physical_resistance", () -> new RangedAttribute("icy_lib:physical_resistance", 1.0D, 0.01D, ATTRIBUTE_MAX_VALUE).setSyncable(true));

    public static final DeferredHolder<Attribute, Attribute> ELEMENTAL_RESISTANCE =
            ATTRIBUTES.register("elemental_resistance", () -> new RangedAttribute("icy_lib:elemental_resistance", 1.0D, 0.01D, ATTRIBUTE_MAX_VALUE).setSyncable(true));

    public static final DeferredHolder<Attribute, Attribute> UNIVERSAL_RESISTANCE =
            ATTRIBUTES.register("universal_resistance", () -> new RangedAttribute("icy_lib:universal_resistance", 1.0D, 0.01D, ATTRIBUTE_MAX_VALUE).setSyncable(true));

    public static final DeferredHolder<Attribute, Attribute> ICE_RESISTANCE =
            ATTRIBUTES.register("ice_resistance", () -> new RangedAttribute("icy_lib:ice_resistance", 1.0D, 0.01D, ATTRIBUTE_MAX_VALUE).setSyncable(true));

    public static final DeferredHolder<Attribute, Attribute> FIRE_RESISTANCE =
            ATTRIBUTES.register("fire_resistance", () -> new RangedAttribute("icy_lib:fire_resistance", 1.0D, 0.01D, ATTRIBUTE_MAX_VALUE).setSyncable(true));

    public static final DeferredHolder<Attribute, Attribute> LIGHT_RESISTANCE =
            ATTRIBUTES.register("light_resistance", () -> new RangedAttribute("icy_lib:light_resistance", 1.0D, 0.01D, ATTRIBUTE_MAX_VALUE).setSyncable(true));

    public static final DeferredHolder<Attribute, Attribute> SHADOW_RESISTANCE =
            ATTRIBUTES.register("shadow_resistance", () -> new RangedAttribute("icy_lib:shadow_resistance", 1.0D, 0.01D, ATTRIBUTE_MAX_VALUE).setSyncable(true));

    public static final DeferredHolder<Attribute, Attribute> MAGIC_RESISTANCE =
            ATTRIBUTES.register("magic_resistance", () -> new RangedAttribute("icy_lib:magic_resistance", 1.0D, 0.01D, ATTRIBUTE_MAX_VALUE).setSyncable(true));

    public static final DeferredHolder<Attribute, Attribute> POISON_RESISTANCE =
            ATTRIBUTES.register("poison_resistance", () -> new RangedAttribute("icy_lib:poison_resistance", 1.0D, 0.01D, ATTRIBUTE_MAX_VALUE).setSyncable(true));

    public static final DeferredHolder<Attribute, Attribute> VOID_RESISTANCE =
            ATTRIBUTES.register("void_resistance", () -> new RangedAttribute("icy_lib:void_resistance", 1.0D, 0.01D, ATTRIBUTE_MAX_VALUE).setSyncable(true));

    public static final double CRITICAL_STRIKE_MULTIPLIER = 1.5D;
}
