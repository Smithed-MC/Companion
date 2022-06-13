package dev.smithed.companion;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.smithed.companion.packets.PacketUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Identifier;

import java.util.logging.Logger;

public class SmithedMainClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
//
//        PacketUtils.registerClientPacketListeners();
//
//        try {
//            ClientCommandManager.getActiveDispatcher().register(ClientCommandManager.literal("reload_groups").executes(context -> {
//                PacketByteBuf buf = PacketByteBufs.create();
//
//                PacketUtils.SP2S(new Identifier(SmithedMain.MODID, "itemgroup_info_channel"), buf);
//                return 1;
//            }));
//        }
//        catch (NullPointerException e) {
//            SmithedMain.logger.info("Unable to load client reload command");
//        }
	}

}