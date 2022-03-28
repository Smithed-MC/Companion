package dev.smithed.companion;

import com.google.gson.JsonSyntaxException;
import dev.smithed.companion.item_groups.ItemGroupData;
import dev.smithed.companion.item_groups.SimplifiedItemGroupEntry;
import dev.smithed.companion.mixin.TitleScreenMixin;
import dev.smithed.companion.packets.PacketUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.profiler.Profiler;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static dev.smithed.companion.SmithedMain.*;
import static dev.smithed.companion.SmithedMain.logger;

public class PostReloadListener implements SimpleResourceReloadListener<Void> {

    public static Map<String,List<SimplifiedItemGroupEntry>> queuedGroupAdditions = new HashMap<>();
    // To be stored on the server, this group is then distributed to clients
    public static Map<String, ItemGroupData> unregisteredItemGroups = new HashMap<>();

    @Override
    public CompletableFuture<Void> load(ResourceManager manager, Profiler profiler, Executor executor) {

        return CompletableFuture.supplyAsync(() -> {
            SmithedMain.logger.info("Loaded custom resource manager");
            // clear the itemgroups cached by the server if they exist
            if(!unregisteredItemGroups.isEmpty())
                unregisteredItemGroups.clear();

            manager.findResources("item_groups", (str) -> str.endsWith(".json")).forEach(id -> {
                try (InputStream stream = manager.getResource(id).getInputStream()) {
                    ItemGroupData.loadItemGroupData(stream, id);
                }
                catch (IOException e) {
                    SmithedMain.logger.error("Issue importing item group file: " + id.toString(), e);
                } catch (JsonSyntaxException e) {
                    SmithedMain.logger.error("Invalid JSON file or object in: " + id.toString() + ", skipping to next file.");
                } catch (RuntimeException e) {
                    SmithedMain.logger.error("Caught an unexpected error while deserializing {}", id.toString());
                    SmithedMain.logger.error("Skipping {}", id.toString());
                    SmithedMain.logger.error("Error: ", e);
                }
            });

            logger.info("queued groups: " + queuedGroupAdditions);

            return null;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(Void data, ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> {
            logger.info("Inject reload reached");
            logger.info("current queued: " + queuedGroupAdditions);
            logger.info("unregistered groups" + unregisteredItemGroups);



            executor.execute(() -> {
                for (Map.Entry<String, List<SimplifiedItemGroupEntry>> pair: queuedGroupAdditions.entrySet()) {
                    try {
                        logger.info("Current group: " + pair.getKey());
                        List<ItemStack> stacks = DefaultedList.of();
                        for (SimplifiedItemGroupEntry entry : pair.getValue()) {
                            switch (entry.getType()) {
                                case "minecraft:item":
                                    stacks.add(ItemGroupData.tryParseEntryAsStack(entry, "Attempted addition of"));
                                    break;
                                case "minecraft:loot_table":
                                    logger.info("Registered loot table: " + entry.getID());
                                    LootTable table = getServer().getLootManager().getTable(Identifier.tryParse(entry.getID()));
                                    logger.info(table.toString());
                                    stacks.addAll(table.generateLoot(new LootContext.Builder(getServer().getOverworld()).build(LootContextType.create().build())));
                                    break;
                                case "smithed:loot_directory":
                                    logger.info("Registered loot directory: " + entry.getID());

                                    Objects.requireNonNull(getServer()).getLootManager().getTableIds().forEach(id -> {
                                        logger.info("Available paths: " + Objects.requireNonNull(id.toString()) + ", \ndesired path: " + entry.getID());
                                        if(id.toString().startsWith(entry.getID())) {
                                            logger.info("found path:" + id.getPath());
                                        }
                                    });
                                    break;
                            }
                        }

                        unregisteredItemGroups.get(pair.getKey()).addItems(stacks);
                    }
                    catch (NullPointerException e) {
                        logger.error("ItemGroup {} with intent to append doesn't exist.", pair.getKey());
                    }
                }
                logger.info("Post-Reload item group setup complete");
            });
        }, executor);
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(SmithedMain.MODID, "post_reload");
    }
}
