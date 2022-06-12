package dev.smithed.companion.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Shadow @Final private boolean doBackgroundFade;

    @Shadow private long backgroundFadeStart;

    @Inject(method = "render", at = @At("RETURN"))
    private void injectMethod(MatrixStack matrices, int mouseX, int mouseY, float delta,CallbackInfo info) {
        float s = this.doBackgroundFade ? (float)(Util.getMeasuringTimeMs() - this.backgroundFadeStart) / 1000.0F : 1.0F;
        float p = this.doBackgroundFade ? MathHelper.clamp(s - 1.0F, 0.0F, 1.0F) : 1.0F;
        int d = MathHelper.ceil(p * 255.0F) << 24;

        if ((d & -67108864) != 0) {
            drawStringWithShadow(matrices, this.textRenderer, "This client is running the smithed companion mod", 2, this.height - 20, 16777215 | d);
        }
    }
}