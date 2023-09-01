package dev.smithed.companion.utils;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import static dev.smithed.companion.utils.RegistryUtils.DATAPACK_ITEM_REGISTRY;

public class DatapackItemHandler {

    public static void loadDatapackItems(ClientPlayNetworkHandler networkHandler, PacketSender packetSender, MinecraftClient client) {
        if(client.world != null && client.world instanceof DatapackItemUtils.DatapackItemHandler handler) {
            handler.smithed$dumpItems();
            client.world.getRegistryManager().get(DATAPACK_ITEM_REGISTRY).forEach(handler::smithed$registerItem);
        }
    }
}
