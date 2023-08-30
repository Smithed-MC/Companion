package dev.smithed.companion.reload_listeners;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dev.smithed.companion.SmithedMain;
import dev.smithed.companion.utils.ShortcutUtils;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ShortcutReloadListener implements SimpleResourceReloadListener<Void> {

    private final Logger logger = SmithedMain.createLogger("shortcuts");

    @Override
    public CompletableFuture<Void> load(ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("Cleared shortcut cache");
            return null;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(Void data, ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> {
            executor.execute(() -> {

                manager.findResources("shortcuts", (str) -> str.getPath().endsWith(".json")).forEach((id, res) -> {
                    try (InputStream stream = res.getInputStream()) {

                        ShortcutUtils.ShortcutData shortcutData = new Gson().fromJson(new InputStreamReader(stream), ShortcutUtils.ShortcutData.class);
                        //ShortcutUtils.registerShortcut(shortcutData);

                    } catch (IOException e) {
                        logger.error("Issue importing shortcut file: " + id.toString(), e);
                    } catch (JsonSyntaxException e) {
                        logger.error("Invalid JSON file or object in: " + id.toString() + ", skipping to next file.");
                    } catch (RuntimeException e) {
                        logger.error("Caught an unexpected error while deserializing {}", id.toString());
                        logger.error("Skipping {}", id.toString());
                        logger.error("Error: ", e);
                    }
                });
                logger.info("Shortcut setup complete");
            });
        }, executor);
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(SmithedMain.MODID, "shortcut_reload");
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return List.of(ResourceReloadListenerKeys.FUNCTIONS);
    }
}
