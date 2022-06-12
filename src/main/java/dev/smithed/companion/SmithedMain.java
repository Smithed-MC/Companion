package dev.smithed.companion;

import dev.smithed.companion.packets.PacketUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SmithedMain implements ModInitializer {
	public static Logger logger = LogManager.getLogger("Smithed");
	public static String MODID = "smithed";
	private static MinecraftServer server;

	// This is used by the clients to hold a reference of registered item groups
	public static Map<String, ItemGroup> registeredItemGroups = new HashMap<>();

	// The global datapacks file loc
	public static File smithedDataPacks = (Path.of(FabricLoader.getInstance().getGameDir().toString() + "/datapacks")).toFile();

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTING.register((server) -> SmithedMain.server = server);
		ServerLifecycleEvents.SERVER_STOPPED.register((server) -> SmithedMain.server = null);

		PacketUtils.registerServerPacketListeners();

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			handler.getPlayer().getScoreboardTags().remove("smithed.client");
			SmithedMain.logger.info("removed tag: smithed.client");
		});

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			PacketUtils.SP2S(new Identifier(SmithedMain.MODID, "mark_companion_player"), PacketByteBufs.create());
			PacketUtils.SP2S(new Identifier(SmithedMain.MODID, "itemgroup_info_channel"), PacketByteBufs.create());
		});

		//register smithed reload listeners
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new PostReloadListener());

		logger.info("Initialized");
	}

	// why didn't i put these in the smithed util class? idk bcuz fuck you?
	@NotNull
	public static MinecraftServer getServer() {
		if (server != null) {
			return server;
		}
		throw new UnsupportedOperationException("Accessed server too early!");
	}
}