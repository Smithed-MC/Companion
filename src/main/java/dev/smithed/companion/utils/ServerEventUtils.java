package dev.smithed.companion.utils;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.jetbrains.annotations.Nullable;

/**
 * A series of utilities for event listener registration, helpful for keeping event declaration and dispatch clean
 * @author dragoncommands, ImCoolYeah105
 */
public class ServerEventUtils {

    private static MinecraftServer currentServer = null;

    /**
     * Simple registration command, bundles registration of listeners into one catch-all
     */
    public static void registerAll() {
        ServerPlayConnectionEvents.JOIN.register(ServerEventUtils::joinListener);
        ServerLifecycleEvents.SERVER_STARTED.register(ServerEventUtils::startListener);
        ServerLifecycleEvents.SERVER_STOPPED.register(ServerEventUtils::stopListener);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(ServerEventUtils::reloadListener);
    }

    /**
     * Reload server listener for functionality that needs it
     * Essentially a central dispatch point for numerous other classes
     * @param server the server instance these events are registered on
     * @param lifecycledResourceManager resource manager for fetching resources from server files
     * @param success if reload was successful or not
     */
    private static void reloadListener(MinecraftServer server, LifecycledResourceManager lifecycledResourceManager, boolean success) {
        ShortcutUtils.updateShortcuts(server);
        server.getPlayerManager().getPlayerList().forEach(player -> ServerPlayNetworking.send(player, PacketUtilsCommon.RELOAD_PACKET_IDENTIFER, PacketByteBufs.empty()));
    }

    /**
     *  Start server listener for functionality that needs it
     *  Essentially a central dispatch point for numerous other classes
     * @param server the server that is starting up
     */
    private static void startListener(MinecraftServer server) {
        currentServer = server;
        ShortcutUtils.enableShortcuts(server);
    }

    /**
     *  Stop server listener for functionality that needs it
     *  Essentially a central dispatch point for numerous other classes
     * @param server the server that is stopping up
     */
    private static void stopListener(MinecraftServer server) {
        currentServer = null;
    }

    /**
     * Server join listener for functionality that needs it
     * Essentially a central dispatch point for numerous other classes
     * @param handler handler of network packet events
     * @param sender the sender of the packet to the server
     * @param server this server instance
     */
    private static void joinListener(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {

    }

    /**
     * Tracks current server through start/stop event handlers.
     * @return current loaded server instance, may be null
     */
    @Nullable
    public static MinecraftServer getCurrentServer() {
        return currentServer;
    }
}
