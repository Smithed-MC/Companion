package dev.smithed.companion.mixin;

import dev.smithed.companion.pack_provider.SmithedDataPackProvider;
import net.minecraft.loot.LootManager;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;

import static dev.smithed.companion.SmithedMain.*;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(method = "loadDataPacks", at = @At("HEAD"))
    private static void injectLoadDatapacks(ResourcePackManager resourcePackManager, DataPackSettings dataPackSettings, boolean safeMode, CallbackInfoReturnable<DataPackSettings> cir) {
        if (safeMode) return; // exit if in safemode

        // Check if provider exists or not if Yes: Discard, if No: continue
        for (ResourcePackProvider provider : resourcePackManager.providers) {
            if (provider instanceof FileResourcePackProvider && ((FileResourcePackProvider) provider).packsFolder.getAbsolutePath().equals(smithedDataPacks.getAbsolutePath())) {
                return; // exit because custom provider already exists within context
            }
        }

        resourcePackManager.providers.add(new SmithedDataPackProvider(smithedDataPacks));
    }
}