package com.icylib;

import com.icylib.armor.ArmorSystem;
import com.icylib.client.IcyLibClient;
import com.icylib.config.IcyLibConfig;
import com.icylib.config.MultiplyMigrationConfig;
import com.icylib.defense.DefenseSystemManager;
import com.icylib.dodge.DodgeEventHandler;
import com.icylib.event.CriticalStrikeEventHandler;
import com.icylib.event.DefenseEventHandler;
import com.icylib.event.MultiplyTotalMigrationHandler;
import com.icylib.event.PlayerUpgradeEventHandler;
import com.icylib.init.ModAttributes;
import com.icylib.init.ModEffects;
import com.icylib.init.ModMenuTypes;
import com.icylib.init.ModUpgradeItems;
import com.icylib.network.PlayerUpgradeNetworkHandler;
import com.icylib.shield.ShieldSystem;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(IcyLib.MODID)
public class IcyLib {

    public static final String MODID = "icy_lib";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static List<com.icylib.upgrade.ExpItem> EXP_ITEMS = new ArrayList<>();

    public IcyLib(IEventBus bus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, IcyLibConfig.SPEC, "icy/icy_lib-common.toml");
        modContainer.registerConfig(ModConfig.Type.COMMON, MultiplyMigrationConfig.SPEC, "icy/multiply_migration.toml");

        ModAttributes.ATTRIBUTES.register(bus);
        ModEffects.EFFECTS.register(bus);
        ModMenuTypes.MENU_TYPES.register(bus);
        ModUpgradeItems.UPGRADE_ITEMS.register(bus);

        bus.addListener(this::setup);
        bus.addListener(this::onEntityAttributeModification);

        NeoForge.EVENT_BUS.register(new CriticalStrikeEventHandler());
        NeoForge.EVENT_BUS.register(new DefenseEventHandler());
        NeoForge.EVENT_BUS.register(new MultiplyTotalMigrationHandler());
        NeoForge.EVENT_BUS.register(new DodgeEventHandler());
        NeoForge.EVENT_BUS.register(new PlayerUpgradeEventHandler());

        // 注册静态类以触发类加载，从而注册默认防御层
        NeoForge.EVENT_BUS.register(DefenseSystemManager.class);
        NeoForge.EVENT_BUS.register(ShieldSystem.class);
        NeoForge.EVENT_BUS.register(ArmorSystem.class);

        if (FMLEnvironment.dist.isClient()) {
            IcyLibClient.registerClient(bus);
            NeoForge.EVENT_BUS.register(com.icylib.fancytext.FancyTextTooltipEvents.class);
            NeoForge.EVENT_BUS.register(com.icylib.quality.QualityTooltipEvents.class);
            NeoForge.EVENT_BUS.register(com.icylib.client.HyperSpaceOverlay.class);
        }
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("Icy Lib v1.1.1 (NeoForge 1.21.1) loaded successfully!");
            LOGGER.info("Damage types registered: physical, ice, fire, light, shadow, magic, poison, void, punishing");
            LOGGER.info("Effects registered: fortitude, hyper_calculation_space, rage, scorching, electrocution, catherine_shield, piercing, corrosion");
            LOGGER.info("Upgrade system loaded: stages=10, exp_items, boost_items, upgrade_menu, recycle_menu");
            LOGGER.info("Defense system loaded: layers=[SHIELD(priority=0), ARMOR(priority=1)], manager=DefenseSystemManager, extensible via DefenseApplyEvent");
            LOGGER.info("Multiply migration loaded: independent-zone (ADD_MULTIPLIED_TOTAL) → multiply-base (ADD_MULTIPLIED_BASE), per-tick scan, UUID→ResourceLocation id whitelist");
            LOGGER.info("FancyText loaded: 50+ presets, tooltip background modes=[COLOR, STARRY_SKY, TEXTURE], FontEffectMixin intercepts drawInBatch/width");

            EXP_ITEMS.add(ModUpgradeItems.EXP_ITEM_1.get());
            EXP_ITEMS.add(ModUpgradeItems.EXP_ITEM_2.get());
            EXP_ITEMS.add(ModUpgradeItems.EXP_ITEM_3.get());
            EXP_ITEMS.add(ModUpgradeItems.EXP_ITEM_4.get());
            EXP_ITEMS.add(ModUpgradeItems.EXP_ITEM_5.get());
        });
    }

    private void onEntityAttributeModification(final EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
            event.add(entityType, ModAttributes.CRITICAL_STRIKE_CHANCE);
            event.add(entityType, ModAttributes.DEFENSE);
            event.add(entityType, ModAttributes.CRITICAL_STRIKE_DEFENSE);
            event.add(entityType, ModAttributes.CRITICAL_STRIKE_MULTIPLIER_ADD);
            event.add(entityType, ModAttributes.CRITICAL_STRIKE_MULTIPLIER_MULTIPLY);
            event.add(entityType, ModAttributes.PHYSICAL_RESISTANCE);
            event.add(entityType, ModAttributes.ELEMENTAL_RESISTANCE);
            event.add(entityType, ModAttributes.UNIVERSAL_RESISTANCE);
            event.add(entityType, ModAttributes.ICE_RESISTANCE);
            event.add(entityType, ModAttributes.FIRE_RESISTANCE);
            event.add(entityType, ModAttributes.LIGHT_RESISTANCE);
            event.add(entityType, ModAttributes.SHADOW_RESISTANCE);
            event.add(entityType, ModAttributes.MAGIC_RESISTANCE);
            event.add(entityType, ModAttributes.POISON_RESISTANCE);
            event.add(entityType, ModAttributes.VOID_RESISTANCE);
        }
    }
}
