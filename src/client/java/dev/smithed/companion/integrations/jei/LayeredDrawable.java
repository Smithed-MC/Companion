package dev.smithed.companion.integrations.jei;

import dev.smithed.companion.container.BackgroundContainer;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.DrawContext;

/**
 * Draws a layered image of the starting background texture with
 * the background container on top. Enables rendering custom UIs.
 */
public class LayeredDrawable implements IDrawable {

    private final IDrawable drawable;
    private final BackgroundContainer backgroundContainer;

    public LayeredDrawable(IDrawable drawable, BackgroundContainer backgroundContainer) {
        this.drawable = drawable;
        this.backgroundContainer = backgroundContainer;
    }

    @Override
    public int getWidth() {
        return drawable.getWidth();
    }

    @Override
    public int getHeight() {
        return drawable.getHeight();
    }

    @Override
    public void draw(DrawContext guiGraphics, int xOffset, int yOffset) {
        drawable.draw(guiGraphics, xOffset, yOffset);
        guiGraphics.drawTexture(backgroundContainer.icon(), xOffset, yOffset, backgroundContainer.u(), backgroundContainer.v(), backgroundContainer.width(), backgroundContainer.height());
    }
}
