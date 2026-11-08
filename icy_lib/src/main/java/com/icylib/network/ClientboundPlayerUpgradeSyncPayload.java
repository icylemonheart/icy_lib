package com.icylib.network;

import com.icylib.upgrade.PlayerUpgradeData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** S2C 同步玩家升级数据。 */
public record ClientboundPlayerUpgradeSyncPayload(PlayerUpgradeData data) implements CustomPacketPayload {
    public static final Type<ClientboundPlayerUpgradeSyncPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("icy_lib", "player_upgrade_sync"));

    public static final StreamCodec<FriendlyByteBuf, ClientboundPlayerUpgradeSyncPayload> STREAM_CODEC =
            StreamCodec.ofMember(ClientboundPlayerUpgradeSyncPayload::write, ClientboundPlayerUpgradeSyncPayload::read);

    private static ClientboundPlayerUpgradeSyncPayload read(FriendlyByteBuf buf) {
        return new ClientboundPlayerUpgradeSyncPayload(PlayerUpgradeData.fromTag(buf.readNbt()));
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeNbt(data.toTag());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientboundPlayerUpgradeSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientUpgradeCache.set(payload.data()));
    }
}
