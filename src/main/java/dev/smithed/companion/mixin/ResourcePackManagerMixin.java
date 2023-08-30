package dev.smithed.companion.mixin;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import dev.smithed.companion.utils.GlobalDatapackUtils;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin {
    @Shadow private Map<String, ResourcePackProfile> profiles;
    @Shadow @Final Set<ResourcePackProvider> providers;

    @Shadow private Stream<ResourcePackProfile> streamProfilesByName(Collection<String> names) {
        throw new AssertionError();
    }

    /*
    Add global datapacks to the enabled profiles list
     */
    @Inject(method = "buildEnabledProfiles", at = @At("HEAD"), cancellable = true)
    private void buildEnabledProfiles(Collection<String> enabledNames, CallbackInfoReturnable<List<ResourcePackProfile>> cir) {
        // Fetch smithed pack provider
        Optional<ResourcePackProvider> smithedProvider = providers.stream().filter(provider -> provider instanceof GlobalDatapackUtils.SmithedDataPackProvider).findFirst();
        List<ResourcePackProfile> EnabledPacks = this.streamProfilesByName(enabledNames).collect(Collectors.toList()), SmithedPacks = new ArrayList<>();
        if (smithedProvider.isPresent() && ((GlobalDatapackUtils.SmithedDataPackProvider)smithedProvider.get()).orderedSmithedPacks.size() > 0) {
            SmithedPacks = this.streamProfilesByName(((GlobalDatapackUtils.SmithedDataPackProvider)smithedProvider.get()).orderedSmithedPacks).collect(Collectors.toList());
            EnabledPacks.removeAll(SmithedPacks);
        }

        // Register all smithed packs
        for (ResourcePackProfile resourcePackProfile : SmithedPacks) {
            if (resourcePackProfile.isAlwaysEnabled() && !EnabledPacks.contains(resourcePackProfile)) {
                resourcePackProfile.getInitialPosition().insert(EnabledPacks, resourcePackProfile, Functions.identity(), false);
            }
        }
        for (ResourcePackProfile resourcePackProfile : this.profiles.values()) {
            if (resourcePackProfile.isAlwaysEnabled() && !EnabledPacks.contains(resourcePackProfile)) {
                resourcePackProfile.getInitialPosition().insert(EnabledPacks, resourcePackProfile, Functions.identity(), false);
            }
        }

        cir.setReturnValue(ImmutableList.copyOf(EnabledPacks));
    }
}
