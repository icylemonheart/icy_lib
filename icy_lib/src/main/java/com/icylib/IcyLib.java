package com.icylib;

import com.icylib.config.IcyLibConfig;
import com.icylib.event.CriticalStrikeEventHandler;
import com.icylib.event.DefenseEventHandler;
import com.icylib.init.ModAttributes;
import com.icylib.init.ModEffects;
import com.icylib.init.ModMenuTypes;
import com.icylib.init.ModUpgradeItems;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(IcyLib.MODID)
public class IcyLib {

    public static final String MODID = "icy_lib";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static List<com.icylib.upgrade.ExpItem> EXP_ITEMS = new ArrayList<>();

    public IcyLib() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, IcyLibConfig.SPEC, "icy_lib-common.toml");
        
        ModAttributes.ATTRIBUTES.register(bus);
        ModEffects.EFFECTS.register(bus);
        ModMenuTypes.MENU_TYPES.register(bus);
        ModUpgradeItems.UPGRADE_ITEMS.register(bus);
        
        bus.addListener(this::setup);
        bus.addListener(this::onEntityAttributeModification);
        
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new CriticalStrikeEventHandler());
        MinecraftForge.EVENT_BUS.register(new DefenseEventHandler());
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("Icy Lib loaded successfully!");
            LOGGER.info("Damage types registered: physical, ice, fire, light, shadow, magic, poison, void, punishing");
            LOGGER.info("Iron Spells compatibility: fire_magic, ice_magic, lightning_magic, holy_magic, nature_magic, ender_magic, evocation_magic, blood_magic, eldritch_magic, poison_cloud, heartstop, fire_field, dragon_breath_pool, blood_cauldron");
            LOGGER.info("TACZ compatibility: bullet, bullet_ignore_armor, bullet_void, bullet_void_ignore_armor (all mapped to physical damage)");
            LOGGER.info("Effects registered: fortitude, hyper_calculation_space");
            LOGGER.info("Upgrade system loaded: stages=10, exp_items, boost_items, upgrade_menu, recycle_menu");
            
            EXP_ITEMS.add(ModUpgradeItems.EXP_ITEM_1.get());
            EXP_ITEMS.add(ModUpgradeItems.EXP_ITEM_2.get());
            EXP_ITEMS.add(ModUpgradeItems.EXP_ITEM_3.get());
            EXP_ITEMS.add(ModUpgradeItems.EXP_ITEM_4.get());
            EXP_ITEMS.add(ModUpgradeItems.EXP_ITEM_5.get());
        });
    }

    private void onEntityAttributeModification(final EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
            event.add(entityType, ModAttributes.CRITICAL_STRIKE_CHANCE.get());
            event.add(entityType, ModAttributes.DEFENSE.get());
            event.add(entityType, ModAttributes.CRITICAL_STRIKE_DEFENSE.get());
            event.add(entityType, ModAttributes.CRITICAL_STRIKE_MULTIPLIER_ADD.get());
            event.add(entityType, ModAttributes.CRITICAL_STRIKE_MULTIPLIER_MULTIPLY.get());
            event.add(entityType, ModAttributes.PHYSICAL_RESISTANCE.get());
            event.add(entityType, ModAttributes.ELEMENTAL_RESISTANCE.get());
            event.add(entityType, ModAttributes.UNIVERSAL_RESISTANCE.get());
            event.add(entityType, ModAttributes.ICE_RESISTANCE.get());
            event.add(entityType, ModAttributes.FIRE_RESISTANCE.get());
            event.add(entityType, ModAttributes.LIGHT_RESISTANCE.get());
            event.add(entityType, ModAttributes.SHADOW_RESISTANCE.get());
            event.add(entityType, ModAttributes.MAGIC_RESISTANCE.get());
            event.add(entityType, ModAttributes.POISON_RESISTANCE.get());
            event.add(entityType, ModAttributes.VOID_RESISTANCE.get());
        }
    }
}
