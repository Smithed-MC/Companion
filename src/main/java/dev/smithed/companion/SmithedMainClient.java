package dev.smithed.companion;

import dev.smithed.companion.item_groups.ItemGroupUtils;
import dev.smithed.companion.packets.ClientPacketUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.util.Identifier;

public class SmithedMainClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientPacketUtils.registerClientPacketListeners();

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			ClientPacketUtils.SP2S(new Identifier(SmithedMain.MODID, "mark_companion_player"), PacketByteBufs.create());
			ClientPacketUtils.SP2S(new Identifier(SmithedMain.MODID, "itemgroup_info_channel"), PacketByteBufs.create());
		});

		ClientPlayConnectionEvents.DISCONNECT.register((phase, listener) -> {
			ItemGroupUtils.destroyGroups();
		});
	}

}