package dev.smithed.companion;

import com.google.gson.Gson;
import dev.smithed.companion.events.EventUtils;
import dev.smithed.companion.item_groups.ItemGroupData;
import dev.smithed.companion.item_groups.SimplifiedItemGroupEntry;
import dev.smithed.companion.packets.PacketUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
// getString(ctx, "string")
import java.io.File;
import java.nio.file.Path;
import java.util.*;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

// word()
// literal("foo")
// argument("bar", word())
// Import everything


public class SmithedMain implements ModInitializer {

	public static Logger logger = LogManager.getLogger("Smithed");
	public static String MODID = "smithed";
	public static Gson gson = new Gson();
	private static MinecraftServer server;

	// This is used by the clients to hold a reference of registered item groups
	public static Map<String, ItemGroup> registeredItemGroups = new HashMap<>();

	// The global datapacks file loc
	@Environment(EnvType.CLIENT)
	public static File SmithedDataPacks = (Path.of(FabricLoader.getInstance().getGameDir().toString() + "/datapacks")).toFile();


	@Override
	public void onInitialize() {

		ServerLifecycleEvents.SERVER_STARTING.register(SmithedMain::setServer);
		ServerLifecycleEvents.SERVER_STOPPED.register(SmithedMain::clearServer);

		PacketUtils.registerServerPacketListeners();
		EventUtils.RegisterEvents();

		registerSmithedReloadListeners();
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

	public static void setServer(MinecraftServer server) {
		SmithedMain.server = server;
	}

	public static void clearServer(MinecraftServer server) {
		SmithedMain.server = null;
	}

	public void registerSmithedReloadListeners() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new PostReloadListener());
	}

}
