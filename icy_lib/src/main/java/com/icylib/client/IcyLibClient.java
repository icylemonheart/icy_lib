package com.icylib.client;

import com.icylib.client.menu.RecycleMenuScreen;
import com.icylib.client.menu.UpgradeMenuScreen;
import com.icylib.init.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = "icy_lib", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class IcyLibClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.UPGRADE_MENU.get(), UpgradeMenuScreen::new);
            MenuScreens.register(ModMenuTypes.RECYCLE_MENU.get(), RecycleMenuScreen::new);
        });
    }
}
