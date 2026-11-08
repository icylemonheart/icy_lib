package com.icylib.network;

import com.icylib.IcyLib;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = IcyLib.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModNetworking {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(ClientboundPlayerUpgradeSyncPayload.TYPE, ClientboundPlayerUpgradeSyncPayload.STREAM_CODEC,
                ClientboundPlayerUpgradeSyncPayload::handle);
        registrar.playToServer(ServerboundPlayerUpgradeActionPayload.TYPE, ServerboundPlayerUpgradeActionPayload.STREAM_CODEC,
                ServerboundPlayerUpgradeActionPayload::handle);
    }
}
