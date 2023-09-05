package dev.smithed.companion.utils;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static net.minecraft.resource.ResourcePackSource.getSourceTextSupplier;

/**
 * Class that exists for global datapack utilities
 * @author dragoncommands
 */
public class GlobalDatapackUtils {

    /**
     * The global datapacks folder from which all smithed datapacks will be grabbed from
     */
    public static File smithedDataPacks = (Path.of(FabricLoader.getInstance().getGameDir().toString() + "/datapacks")).toFile();

    /**
     * This class exists to be our pack provider which will be used to register our global datapacks
     */
    public static class SmithedDataPackProvider extends FileResourcePackProvider {

        /**
         * Get source text supplier and initialize filters
         */
        private static final FileFilter PACK_FILTER = (file) -> (file.isFile() && file.getName().endsWith(".zip")) || (file.isDirectory() && (new File(file, "pack.mcmeta")).isFile());
        private static final ResourcePackSource SMITHED_PACK_SOURCE = ResourcePackSource.create(getSourceTextSupplier("pack.source.smithed"), true);
        public List<String> orderedSmithedPacks = new ArrayList<>();

        /**
         * The datapack provider for smithed global datapacks
         * @param packsDir directory to grab global datapacks from
         * @param type the resourcetype of smithed global datapacks
         * @param source the source of the provider
         */
        public SmithedDataPackProvider(Path packsDir, ResourceType type, ResourcePackSource source) {
            super(packsDir, ResourceType.SERVER_DATA, source);
        }

        @Override
        public void register(Consumer<ResourcePackProfile> profileAdder) {
            List<File> packs = List.of(Objects.requireNonNull(smithedDataPacks.listFiles(PACK_FILTER)));

            packs.forEach(file -> {
                String packName = file.getName();
                ResourcePackProfile profile = ResourcePackProfile.of(
                    packName,
                        Text.literal("SmithedPack"),
                        true,
                        getFactory(file.toPath(), true),
                        new ResourcePackProfile.Metadata(Text.literal("Smithed datapacks"), 20, FeatureSet.empty()),
                        ResourceType.SERVER_DATA,
                        ResourcePackProfile.InsertionPosition.TOP,
                        true,
                        SMITHED_PACK_SOURCE
                );

                profileAdder.accept(profile);
            });

            super.register(profileAdder);
        }
    }
}
