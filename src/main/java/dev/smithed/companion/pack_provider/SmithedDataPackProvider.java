package dev.smithed.companion.pack_provider;

import net.minecraft.resource.*;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SmithedDataPackProvider extends FileResourcePackProvider {
    private static final FileFilter FILTERED_PACK = (file) -> (file.isFile() && file.getName().endsWith(".zip")) || (file.isDirectory() && (new File(file, "pack.mcmeta")).isFile());

    public List<String> orderedSmithedPacks = new ArrayList<>();

    public SmithedDataPackProvider(File packsFolder) {
        super(packsFolder, SmithedDataPackSource.PACK_SOURCE_SMITHED);
    }

    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder, ResourcePackProfile.Factory factory) {
        // Initialize directories
        if (!this.packsFolder.isDirectory()) {
            this.packsFolder.mkdirs();
        }

        File[] packs = loadPacksFromFiles();

        if (packs != null) {
            for (File file : packs) {
                String packName = file.getName();
                ResourcePackProfile resourcePackProfile = ResourcePackProfile.of(
                        packName,
                        true,
                        this.createResourcepack(file),
                        factory,
                        ResourcePackProfile.InsertionPosition.TOP,
                        SmithedDataPackSource.PACK_SOURCE_SMITHED
                );

                if (resourcePackProfile != null) {
                    profileAdder.accept(resourcePackProfile);
                }
            }
        }
    }

    private File[] loadPacksFromFiles() {
        //load files in any order
        return this.packsFolder.listFiles(FILTERED_PACK);
    }

    private Supplier<ResourcePack> createResourcepack(File file) {
        return file.isDirectory() ? () -> new DirectoryResourcePack(file) : () -> new ZipResourcePack(file);
    }
}