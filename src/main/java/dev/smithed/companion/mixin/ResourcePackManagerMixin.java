package dev.smithed.companion.mixin;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import dev.smithed.companion.pack_provider.SmithedDataPackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(ResourcePackManager.class)
public abstract class ResourcePackManagerMixin {
    @Shadow
    private Map<String, ResourcePackProfile> profiles;

    @Shadow
    public Set<ResourcePackProvider> providers;

    @Shadow
    private Stream<ResourcePackProfile> streamProfilesByName(Collection<String> names) {
        throw new AssertionError();
    }

    @Inject(at=@At("HEAD"), method="buildEnabledProfiles", cancellable = true)
    private void buildEnabledProfiles(Collection<String> enabledNames, CallbackInfoReturnable<List<ResourcePackProfile>> cir) {
        // Fetch smithed pack provider
        Optional<ResourcePackProvider> smithedProvider = this.providers.stream().filter(provider -> provider instanceof SmithedDataPackProvider).findFirst();

        // List of all packs to be marked as enabled
        List<ResourcePackProfile> allEnabledPacks = this.streamProfilesByName(enabledNames).collect(Collectors.toList());

        // List of all packs loaded by smithed
        List<ResourcePackProfile> smithedPacks = new ArrayList<>();


        if (smithedProvider.isPresent() && ((SmithedDataPackProvider)smithedProvider.get()).orderedSmithedPacks.size() > 0) {
            smithedPacks = this.streamProfilesByName(((SmithedDataPackProvider)smithedProvider.get()).orderedSmithedPacks).collect(Collectors.toList());
            allEnabledPacks.removeAll(smithedPacks);
        }

        // Register all smithed packs
        for (ResourcePackProfile resourcePackProfile : smithedPacks) {
            if (resourcePackProfile.isAlwaysEnabled() && !allEnabledPacks.contains(resourcePackProfile)) {
                resourcePackProfile.getInitialPosition().insert(allEnabledPacks, resourcePackProfile, Functions.identity(), false);
            }
        }

        for (ResourcePackProfile resourcePackProfile : this.profiles.values()) {
            if (resourcePackProfile.isAlwaysEnabled() && !allEnabledPacks.contains(resourcePackProfile)) {
                resourcePackProfile.getInitialPosition().insert(allEnabledPacks, resourcePackProfile, Functions.identity(), false);
            }
        }

        cir.setReturnValue(ImmutableList.copyOf(allEnabledPacks));
    }
}
