package dev.smithed.companion;

import dev.smithed.companion.utils.EventUtils;
import net.fabricmc.api.ClientModInitializer;

public class SmithedModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EventUtils.registerAll();
	}
}