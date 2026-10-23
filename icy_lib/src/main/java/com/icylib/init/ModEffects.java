package com.icylib.init;

import com.icylib.IcyLib;
import com.icylib.effects.CatherineShieldEffect;
import com.icylib.effects.CorrosionEffect;
import com.icylib.effects.ElectrocutionEffect;
import com.icylib.effects.FortitudeEffect;
import com.icylib.effects.HyperCalculationSpaceEffect;
import com.icylib.effects.PiercingEffect;
import com.icylib.effects.RageEffect;
import com.icylib.effects.ScorchingEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, IcyLib.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> FORTITUDE =
            EFFECTS.register("fortitude", FortitudeEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> HYPER_CALCULATION_SPACE =
            EFFECTS.register("hyper_calculation_space", HyperCalculationSpaceEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> RAGE =
            EFFECTS.register("rage", RageEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> SCORCHING =
            EFFECTS.register("scorching", ScorchingEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> ELECTROCUTION =
            EFFECTS.register("electrocution", ElectrocutionEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> CATHERINE_SHIELD =
            EFFECTS.register("catherine_shield", CatherineShieldEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> PIERCING =
            EFFECTS.register("piercing", PiercingEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> CORROSION =
            EFFECTS.register("corrosion", CorrosionEffect::new);

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}
