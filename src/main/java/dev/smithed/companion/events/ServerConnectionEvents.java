package dev.smithed.companion.events;

import dev.smithed.companion.SmithedMain;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class ServerConnectionEvents {
    public static void serverPlayerDisconnectEvent() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            handler.getPlayer().getScoreboardTags().remove("smithed.client");
            SmithedMain.logger.info("removed tag: smithed.client");
        });
    }
}