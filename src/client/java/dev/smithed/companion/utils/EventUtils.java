package dev.smithed.companion.utils;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;

public class EventUtils {

    /*
    Registers all client-only events such as a client joining a server
     */
    public static void registerAll() {
        ClientPlayConnectionEvents.JOIN.register(EventUtils::joinListener);
        ClientPlayConnectionEvents.DISCONNECT.register(EventUtils::disconnectListener);
        ClientPlayNetworking.registerGlobalReceiver(PacketUtilsCommon.RELOAD_PACKET_IDENTIFER, (client, handler, buf, responseSender) -> {
            ItemGroupHandler.reloadGroups(client);
        });
    }

    /*
    Client join server listener for functionality that needs it
    Essentially a central dispatch point for numerous other classes
     */
    private static void joinListener(ClientPlayNetworkHandler networkHandler, PacketSender packetSender, MinecraftClient client) {
        ItemGroupHandler.loadGroups(networkHandler,packetSender,client);
    }

    /*
    Client disconnect server listener for functionality that needs it
    Essentially a central dispatch point for numerous other classes
     */
    private static void disconnectListener(ClientPlayNetworkHandler networkHandler, MinecraftClient client) {
        ItemGroupHandler.discardGroups(networkHandler, client);
    }

}
