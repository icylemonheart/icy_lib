package com.icylib.init;

import com.icylib.IcyLib;
import com.icylib.menu.RecycleMenu;
import com.icylib.menu.UpgradeMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(BuiltInRegistries.MENU, IcyLib.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<UpgradeMenu>> UPGRADE_MENU =
            MENU_TYPES.register("upgrade", () -> new MenuType<>(UpgradeMenu::new, FeatureFlags.VANILLA_SET));

    public static final DeferredHolder<MenuType<?>, MenuType<RecycleMenu>> RECYCLE_MENU =
            MENU_TYPES.register("recycle", () -> new MenuType<>(RecycleMenu::new, FeatureFlags.VANILLA_SET));
}
