package com.icylib.client;

import com.icylib.client.menu.RecycleMenuScreen;
import com.icylib.client.menu.UpgradeMenuScreen;
import com.icylib.init.ModMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * 客户端侧初始化类。
 *
 * 仅在客户端被 {@link com.icylib.IcyLib} 通过 {@code FMLEnvironment.dist.isClient()} 守卫加载，
 * 因此本类可自由引用客户端专属 API（如 Screen 子类）。
 *
 * NeoForge 1.21.1 中 {@code MenuScreens.register} 已标记为 @Deprecated 且 private，
 * 改用 {@link RegisterMenuScreensEvent}（MOD 总线事件）注册屏幕工厂。
 */
@OnlyIn(Dist.CLIENT)
public class IcyLibClient {

    /**
     * 由 IcyLib 主类在客户端环境调用，注册 MOD 总线事件监听器。
     */
    public static void registerClient(IEventBus modBus) {
        modBus.addListener(IcyLibClient::onRegisterMenuScreens);
    }

    @SubscribeEvent
    private static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.UPGRADE_MENU.get(), UpgradeMenuScreen::new);
        event.register(ModMenuTypes.RECYCLE_MENU.get(), RecycleMenuScreen::new);
    }
}
