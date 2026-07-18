package com.icylib.init;

import com.icylib.IcyLib;
import com.icylib.menu.RecycleMenu;
import com.icylib.menu.UpgradeMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, IcyLib.MODID);

    public static final RegistryObject<MenuType<UpgradeMenu>> UPGRADE_MENU = MENU_TYPES.register("upgrade",
            () -> IForgeMenuType.create((windowId, inv, data) -> new UpgradeMenu(windowId, inv)));

    public static final RegistryObject<MenuType<RecycleMenu>> RECYCLE_MENU = MENU_TYPES.register("recycle",
            () -> IForgeMenuType.create((windowId, inv, data) -> new RecycleMenu(windowId, inv)));
}
