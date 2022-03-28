package dev.smithed.companion;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.smithed.companion.packets.PacketUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class SmithedMainClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        PacketUtils.registerClientPacketListeners();

        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("reload_groups").executes(context -> {
            System.out.println("Called foo with no arguments");
            PacketByteBuf buf = PacketByteBufs.create();

            PacketUtils.SP2S(new Identifier(SmithedMain.MODID, "itemgroup_info_channel"), buf);
            return 1;
        }));

    }

}
