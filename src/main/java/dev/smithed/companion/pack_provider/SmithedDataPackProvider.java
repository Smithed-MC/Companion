package dev.smithed.companion.pack_provider;

import net.minecraft.resource.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SmithedDataPackProvider extends FileResourcePackProvider {
    private static final FileFilter FILTERED_PACK = (file) -> (file.isFile() && file.getName().endsWith(".zip")) || (file.isDirectory() && (new File(file, "pack.mcmeta")).isFile());

    private static final ResourcePackSource SMITHED_PACK_SOURCE = (text) -> (Text.translatable("pack.nameAndSource", text, Text.literal("smithed"))).formatted(Formatting.AQUA);

    //this is read but never written to
    public List<String> orderedSmithedPacks = new ArrayList<>();

    public SmithedDataPackProvider(File packsFolder) {
        super(packsFolder, SMITHED_PACK_SOURCE);
    }

    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder, ResourcePackProfile.Factory factory) {
        // Initialize directories
        if (!this.packsFolder.isDirectory()) {
            this.packsFolder.mkdirs();
        }

        File[] packs = this.packsFolder.listFiles(FILTERED_PACK);

        if (packs != null) {
            for (File file : packs) {
                String packName = file.getName();
                ResourcePackProfile resourcePackProfile = ResourcePackProfile.of(
                    packName,
                    true,
                    this.createResourcepack(file),
                    factory,
                    ResourcePackProfile.InsertionPosition.TOP,
                    SMITHED_PACK_SOURCE
                );

                if (resourcePackProfile != null) {
                    profileAdder.accept(resourcePackProfile);
                }
            }
        }
    }

    private Supplier<ResourcePack> createResourcepack(File file) {
        return file.isDirectory() ? () -> new DirectoryResourcePack(file) : () -> new ZipResourcePack(file);
    }
}