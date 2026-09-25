package com.github.steven23334.entity_uuid_copy.network;

import com.github.steven23334.entity_uuid_copy.client.PlayerUuidScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record PlayerListPacket(Map<String, UUID> players) implements CustomPacketPayload {
    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath("entity_uuid_copy", "player_list");

    public static final Type<PlayerListPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PlayerListPacket> CODEC =
            StreamCodec.of(
                    (buf, packet) -> {
                        buf.writeInt(packet.players.size());
                        packet.players.forEach((name, uuid) -> {
                            buf.writeUtf(name);
                            buf.writeUUID(uuid);
                        });
                    },
                    buf -> {
                        int size = buf.readInt();
                        Map<String, UUID> players = new HashMap<>();
                        for (int i = 0; i < size; i++) {
                            players.put(buf.readUtf(), buf.readUUID());
                        }
                        return new PlayerListPacket(players);
                    }
            );

    @Override
    public @NotNull Type<PlayerListPacket> type() {
        return TYPE;
    }

    /** 客户端收到后打开 GUI */
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> PlayerUuidScreen.open(players));
    }
}