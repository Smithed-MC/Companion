package dev.smithed.companion;

import com.google.gson.Gson;
import dev.smithed.companion.packets.PacketUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
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
	private static final Gson GSON = new Gson();
	// This is used by the clients to hold a reference of registered item groups
	public static Map<String, ItemGroup> registeredItemGroups = new HashMap<>();
	// The global datapacks file loc
	public static File smithedDataPacks = (Path.of(FabricLoader.getInstance().getGameDir().toString() + "/datapacks")).toFile();
	public static SmithedConfig config;

	@Override
	public void onInitialize() {

		// Load in file containing hashcodes

		File smithedConfig = (Path.of(FabricLoader.getInstance().getConfigDir().toFile() + "/datapacks")).toFile();

		ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
			SmithedMain.server = server;

			// Fetch a hashcode from data related to the server
			// Compare the hashcode of the packs on the server

			int hashCode = server.getDataPackManager().providers.hashCode();
		});
		ServerLifecycleEvents.SERVER_STOPPED.register((server) -> SmithedMain.server = null);

		DefaultedList<ItemStack> stacks = DefaultedList.of();
		stacks.add(new ItemStack(Items.COMMAND_BLOCK).setCustomName(Text.literal("fucky wucky OWO")));
		ItemGroup.BUILDING_BLOCKS.appendStacks(stacks);

		//register smithed reload listeners
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new PostReloadListener());

		PacketUtils.registerServerPacketListeners();
		//PacketUtils.registerClientPacketListeners();

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			handler.getPlayer().getScoreboardTags().remove("smithed.client");
			logger.info("removed tag: smithed.client");
		});


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