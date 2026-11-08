package com.icylib.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** C2S 升级/晋升请求：action 0=升级、1=晋升。 */
public record ServerboundPlayerUpgradeActionPayload(int action) implements CustomPacketPayload {
    public static final Type<ServerboundPlayerUpgradeActionPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("icy_lib", "player_upgrade_action"));

    public static final StreamCodec<FriendlyByteBuf, ServerboundPlayerUpgradeActionPayload> STREAM_CODEC =
            StreamCodec.ofMember(ServerboundPlayerUpgradeActionPayload::write, ServerboundPlayerUpgradeActionPayload::read);

    private static ServerboundPlayerUpgradeActionPayload read(FriendlyByteBuf buf) {
        return new ServerboundPlayerUpgradeActionPayload(buf.readVarInt());
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeVarInt(action);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ServerboundPlayerUpgradeActionPayload payload, IPayloadContext context) {
        PlayerUpgradeNetworkHandler.handleServer(payload, context);
    }
}
