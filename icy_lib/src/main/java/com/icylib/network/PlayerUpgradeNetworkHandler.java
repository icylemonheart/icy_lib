package com.icylib.network;

import com.icylib.upgrade.PlayerUpgradeAttributes;
import com.icylib.upgrade.PlayerUpgradeData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** 玩家升级/晋升网络包处理：C2S action 0=升级、1=晋升，并负责 S2C 同步。 */
public class PlayerUpgradeNetworkHandler {

    public static void handleServer(ServerboundPlayerUpgradeActionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                PlayerUpgradeData data = PlayerUpgradeData.get(serverPlayer);
                boolean changed = false;

                if (payload.action() == 0) {
                    if (data.canLevelUp()) {
                        data.addExp(0);
                        changed = true;
                    }
                } else if (payload.action() == 1) {
                    if (data.tryPromote()) {
                        changed = true;
                    }
                }

                if (changed) {
                    PlayerUpgradeData.set(serverPlayer, data);
                    PlayerUpgradeAttributes.apply(serverPlayer, data);
                    syncToClient(serverPlayer, data);
                }
            }
        });
    }

    public static void syncToClient(ServerPlayer player, PlayerUpgradeData data) {
        PacketDistributor.sendToPlayer(player, new ClientboundPlayerUpgradeSyncPayload(data));
    }

    public static void handleClient(ClientboundPlayerUpgradeSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientUpgradeCache.set(payload.data()));
    }

    public static void sendUpgrade() {
        PacketDistributor.sendToServer(new ServerboundPlayerUpgradeActionPayload(0));
    }

    public static void sendPromote() {
        PacketDistributor.sendToServer(new ServerboundPlayerUpgradeActionPayload(1));
    }

    public static void onPlayerLogin(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            PlayerUpgradeData data = PlayerUpgradeData.get(serverPlayer);
            PlayerUpgradeAttributes.apply(serverPlayer, data);
            syncToClient(serverPlayer, data);
        }
    }
}
