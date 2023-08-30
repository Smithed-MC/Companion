package dev.smithed.companion.utils;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public class ServerEventUtils {

    public static void registerAll() {
        ServerPlayConnectionEvents.JOIN.register(ServerEventUtils::joinListener);
        ServerLifecycleEvents.SERVER_STARTED.register(ServerEventUtils::startListener);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(ServerEventUtils::reloadListener);
    }

    /*
    Reload server listener for functionality that needs it
    Essentially a central dispatch point for numerous other classes
     */
    private static void reloadListener(MinecraftServer server, LifecycledResourceManager lifecycledResourceManager, boolean b) {
        ShortcutUtils.updateShortcuts(server);
        server.getPlayerManager().getPlayerList().forEach(player -> ServerPlayNetworking.send(player, PacketUtilsCommon.RELOAD_PACKET_IDENTIFER, PacketByteBufs.empty()));
    }

    /*
    Start server listener for functionality that needs it
    Essentially a central dispatch point for numerous other classes
     */
    private static void startListener(MinecraftServer server) {
        ShortcutUtils.enableShortcuts(server);
    }

    /*
    Server join listener for functionality that needs it
    Essentially a central dispatch point for numerous other classes
     */
    private static void joinListener(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {

    }
}
