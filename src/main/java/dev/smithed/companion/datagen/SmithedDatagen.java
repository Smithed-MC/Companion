package dev.smithed.companion.datagen;

import com.mojang.serialization.Lifecycle;
import dev.smithed.companion.utils.RegistryUtils;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;

public class SmithedDatagen implements DataGeneratorEntrypoint {

    /*
    Initialized data generator for smithed content
    Datagen mainly exists in smithed-companion for debugging purposes with no intents to use it for content.
     */
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();
        pack.addProvider(ShortcutProvider::new);
        pack.addProvider(ItemgroupProvider::new);
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        registryBuilder.addRegistry(RegistryUtils.SHORTCUT_REGISTRY, foo -> {

        });

        registryBuilder.addRegistry(RegistryUtils.ITEMGROUP_REGISTRY, foo -> {

        });
        DataGeneratorEntrypoint.super.buildRegistry(registryBuilder);
    }
}
