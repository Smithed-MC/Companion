package dev.smithed.companion.mixin;

import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldListWidget.WorldEntry.class)
public abstract class WorldListWidgetMixin extends WorldListWidget.Entry implements AutoCloseable {

    @Shadow @Final private LevelSummary level;

    @Inject(method = "play", at = @At("HEAD"))
    public void playInject(CallbackInfo ci) {
        if(!this.level.isUnavailable()) {

        }
    }

}
