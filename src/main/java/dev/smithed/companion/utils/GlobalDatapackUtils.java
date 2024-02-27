package dev.smithed.companion.utils;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.*;
import net.minecraft.resource.FileResourcePackProvider.PackOpenerImpl;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import net.minecraft.util.path.SymlinkEntry;
import net.minecraft.util.path.SymlinkFinder;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
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
        private static final SymlinkFinder finder = new SymlinkFinder(path -> true);

        /**
         * The datapack provider for smithed global datapacks
         * @param packsDir directory to grab global datapacks from
         * @param type the resourcetype of smithed global datapacks
         * @param source the source of the provider
         */
        public SmithedDataPackProvider(Path packsDir, ResourceType type, ResourcePackSource source) {
            super(packsDir, ResourceType.SERVER_DATA, source, finder);
        }

        @Override
        public void register(Consumer<ResourcePackProfile> profileAdder) {
            // EVERY TOOL IS A HAMMER IF YOU TRY HARD ENOUGH
            PackOpenerImpl packOpenerImpl = new PackOpenerImpl(finder, false);
            List<File> packs = List.of(Objects.requireNonNull(smithedDataPacks.listFiles(PACK_FILTER)));

            packs.forEach(file -> {
                List<SymlinkEntry> list = new ArrayList<>();
                try {
                    var factory = packOpenerImpl.open(file.toPath(), list);

                    var packName = file.getName();
                    var profile = createSmithedProfile(packName, factory);

                    profileAdder.accept(profile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });

            super.register(profileAdder);
        }

        private static ResourcePackProfile createSmithedProfile(String packName, ResourcePackProfile.PackFactory packFactory) {
            return ResourcePackProfile.create(packName, Text.literal("Smithed Pack"), true, packFactory, ResourceType.SERVER_DATA, ResourcePackProfile.InsertionPosition.TOP, SMITHED_PACK_SOURCE);
        }

    }

}
