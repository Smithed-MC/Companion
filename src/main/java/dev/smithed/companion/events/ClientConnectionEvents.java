package dev.smithed.companion.events;

import dev.smithed.companion.SmithedMain;
import dev.smithed.companion.packets.PacketUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.util.Identifier;

public class ClientConnectionEvents {

    public static void ClientJoinEvent() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            PacketUtils.SP2S(new Identifier(SmithedMain.MODID, "mark_companion_player"), PacketByteBufs.create());
            PacketUtils.SP2S(new Identifier(SmithedMain.MODID, "itemgroup_info_channel"), PacketByteBufs.create());
        });
    }
}
