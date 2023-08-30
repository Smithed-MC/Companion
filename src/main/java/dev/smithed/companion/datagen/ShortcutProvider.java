package dev.smithed.companion.datagen;

import dev.smithed.companion.utils.RegistryUtils;
import dev.smithed.companion.utils.ShortcutUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

import static dev.smithed.companion.SmithedMain.modID;

public class ShortcutProvider extends FabricDynamicRegistryProvider {
    public ShortcutProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        var key = RegistryKey.of(RegistryUtils.SHORTCUT_REGISTRY, modID("testing"));
        entries.add(key, new ShortcutUtils.ShortcutData(0, modID("based"), "hell"));
    }

    @Override
    public String getName() {
        return "Shortcut datagenerator";
    }
}
