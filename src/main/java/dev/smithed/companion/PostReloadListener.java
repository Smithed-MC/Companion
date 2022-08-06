package dev.smithed.companion;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dev.smithed.companion.item_groups.ItemGroupUtils;
import dev.smithed.companion.item_groups.SimplifiedItemGroup;
import dev.smithed.companion.item_groups.SimplifiedItemGroupEntry;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static dev.smithed.companion.SmithedMain.*;

public class PostReloadListener implements SimpleResourceReloadListener<Void> {

	@Override
	public CompletableFuture<Void> load(ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {

			ItemGroupUtils.cleanUp();
			logger.info("Cleared itemgroup cache");

			return null;
		}, executor);
	}

	@Override
	public CompletableFuture<Void> apply(Void data, ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {

			executor.execute(() -> {

				manager.findResources("item_groups", (str) -> str.getPath().endsWith(".json")).forEach((id, res) -> {
					try (InputStream stream = res.getInputStream()) {
						ItemGroupUtils.cleanUp();
						SimplifiedItemGroup groupData = new Gson().fromJson(new InputStreamReader(stream), SimplifiedItemGroup.class);

						ItemGroupUtils.groupEntries.putIfAbsent(groupData.getID(), new ArrayList<>());
						ItemGroupUtils.groupEntries.get(groupData.getID()).addAll(Arrays.stream(groupData.getEntries()).toList());

						ItemGroupUtils.postGroupDisplay(groupData.getID(), groupData.getTexture(), groupData.getIcon());
						ItemGroupUtils.markDirty();

					} catch (IOException e) {
						logger.error("Issue importing item group file: " + id.toString(), e);
					} catch (JsonSyntaxException e) {
						logger.error("Invalid JSON file or object in: " + id.toString() + ", skipping to next file.");
					} catch (RuntimeException e) {
						logger.error("Caught an unexpected error while deserializing {}", id.toString());
						logger.error("Skipping {}", id.toString());
						logger.error("Error: ", e);
					}
				});

				logger.info("Post-Reload item group setup complete");
			});
		}, executor);
	}

	@Override
	public Identifier getFabricId() {
		return new Identifier(SmithedMain.MODID, "post_reload");
	}

	@Override
	public Collection<Identifier> getFabricDependencies() {
		return List.of(ResourceReloadListenerKeys.LOOT_TABLES, ResourceReloadListenerKeys.FUNCTIONS);
	}
}
