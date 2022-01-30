package dev.smithed.companion.events;

import dev.smithed.companion.SmithedMain;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.logging.Logger;

public class ServerConnectionEvents {

    public static void serverPlayerDisconnectEvent() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            handler.getPlayer().getScoreboardTags().remove("smithed.client");
            SmithedMain.logger.info("removed tag: smithed.client");
        });
    }

}
