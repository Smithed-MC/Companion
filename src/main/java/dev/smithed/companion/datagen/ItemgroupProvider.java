package dev.smithed.companion.datagen;

import dev.smithed.companion.utils.RegistryUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

import static dev.smithed.companion.SmithedMain.modID;

public class ItemgroupProvider extends FabricDynamicRegistryProvider {
    public ItemgroupProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        var key = RegistryKey.of(RegistryUtils.ITEMGROUP_REGISTRY, modID("testing"));
    }

    @Override
    public String getName() {
        return "Itemgroup datagenerator";
    }
}
