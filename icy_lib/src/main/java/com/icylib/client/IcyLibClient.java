package com.icylib.client;

import com.icylib.client.menu.RecycleMenuScreen;
import com.icylib.client.menu.UpgradeMenuScreen;
import com.icylib.init.ModMenuTypes;
import com.icylib.menu.UpgradeMenu;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;

// 客户端初始化：菜单屏幕须经 RegisterMenuScreensEvent 注册；升级界面为全屏布局
@OnlyIn(Dist.CLIENT)
public class IcyLibClient {

    public static void registerClient(IEventBus modBus) {
        modBus.addListener(IcyLibClient::onRegisterMenuScreens);
        NeoForge.EVENT_BUS.register(PlayerUpgradeSubmitHandler.class);
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> {
            var window = Minecraft.getInstance().getWindow();
            UpgradeMenu.setClientScreenSize(window.getGuiScaledWidth(), window.getGuiScaledHeight());
        });
    }

    @SubscribeEvent
    private static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.UPGRADE_MENU.get(), UpgradeMenuScreen::new);
        event.register(ModMenuTypes.RECYCLE_MENU.get(), RecycleMenuScreen::new);
    }
}
