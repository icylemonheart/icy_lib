package com.icylib.event;

import com.icylib.network.PlayerUpgradeNetworkHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * 玩家登录/重生时同步升级数据并应用属性。
 */
public class PlayerUpgradeEventHandler {

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerUpgradeNetworkHandler.onPlayerLogin(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        PlayerUpgradeNetworkHandler.onPlayerLogin(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            PlayerUpgradeNetworkHandler.onPlayerLogin(event.getEntity());
        }
    }
}
