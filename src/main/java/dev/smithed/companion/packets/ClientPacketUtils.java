package dev.smithed.companion.packets;

import dev.smithed.companion.SmithedMain;
import dev.smithed.companion.item_groups.ItemGroupUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Objects;

public class ClientPacketUtils {
    // everything within this map will be registered as a packet listener on the client side
    public static Map<Identifier, ClientPlayNetworking.PlayChannelHandler> clientPacketMap = Map.ofEntries(
            // decodes itemgroup info
            Map.entry(new Identifier(SmithedMain.MODID, "itemgroup_data_channel"), (client, handler, buf, responseSender) -> {
                ItemGroupUtils.destroyGroups();
                ItemGroupUtils.buildGroup(Objects.requireNonNull(buf.readUnlimitedNbt()));
            })
    );

    // generic method for sending packet to server
    public static void SP2S(Identifier identifier, PacketByteBuf buf) {
        ClientPlayNetworking.send(identifier, buf);
    }

    public static void registerClientPacketListeners() {
        for (Identifier identifier : clientPacketMap.keySet())
            ClientPlayNetworking.registerGlobalReceiver(identifier, clientPacketMap.get(identifier));
    }
}
