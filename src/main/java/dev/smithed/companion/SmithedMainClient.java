package dev.smithed.companion;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.smithed.companion.item_groups.ItemGroupUtils;
import dev.smithed.companion.packets.PacketUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Identifier;

import java.util.logging.Logger;

public class SmithedMainClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		PacketUtils.registerClientPacketListeners();

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			PacketUtils.SP2S(new Identifier(SmithedMain.MODID, "mark_companion_player"), PacketByteBufs.create());
			PacketUtils.SP2S(new Identifier(SmithedMain.MODID, "itemgroup_info_channel"), PacketByteBufs.create());
		});

		ClientPlayConnectionEvents.DISCONNECT.register((phase, listener) -> {
			ItemGroupUtils.destroyGroups();
		});
	}

}