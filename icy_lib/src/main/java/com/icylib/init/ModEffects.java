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
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, IcyLib.MODID);

    public static final RegistryObject<MobEffect> FORTITUDE = EFFECTS.register("fortitude", FortitudeEffect::new);

    public static final RegistryObject<MobEffect> HYPER_CALCULATION_SPACE = EFFECTS.register("hyper_calculation_space", HyperCalculationSpaceEffect::new);

    public static final RegistryObject<MobEffect> RAGE = EFFECTS.register("rage", RageEffect::new);

    public static final RegistryObject<MobEffect> SCORCHING = EFFECTS.register("scorching", ScorchingEffect::new);

    public static final RegistryObject<MobEffect> ELECTROCUTION = EFFECTS.register("electrocution", ElectrocutionEffect::new);

    public static final RegistryObject<MobEffect> CATHERINE_SHIELD = EFFECTS.register("catherine_shield", CatherineShieldEffect::new);

    public static final RegistryObject<MobEffect> PIERCING = EFFECTS.register("piercing", PiercingEffect::new);

    public static final RegistryObject<MobEffect> CORROSION = EFFECTS.register("corrosion", CorrosionEffect::new);

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}