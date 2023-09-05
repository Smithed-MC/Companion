package dev.smithed.companion;

import dev.smithed.companion.utils.RegistryUtils;
import dev.smithed.companion.utils.ServerEventUtils;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmithedMain implements ModInitializer {
	public static final String MODID = "smithed";
    private static final Logger LOGGER = LoggerFactory.getLogger("smithed-companion");

	@Override
	public void onInitialize() {
		LOGGER.info("Smithed companion mod has started!");

		RegistryUtils.registerAll();
		ServerEventUtils.registerAll();
	}

	/**
	 * Creates a Logger for the given name. Exists to split off different utilities into their own thing.
	 */
	public static Logger createLogger(String logger) {
		return LoggerFactory.getLogger(MODID + "-" + logger);
	}
	/**
	 * Returns a Smithed Identifier, merely a shortcut for things requiring it.
	 */
	public static Identifier modID(String path) {
		return new Identifier(MODID, path);
	}
}