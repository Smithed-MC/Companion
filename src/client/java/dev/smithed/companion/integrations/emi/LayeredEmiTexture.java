package dev.smithed.companion.integrations.emi;

import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.render.EmiTexture;
import dev.smithed.companion.container.BackgroundContainer;
import net.minecraft.client.gui.DrawContext;

public class LayeredEmiTexture implements EmiRenderable {

    private final EmiRenderable back;
    private final EmiRenderable front;

    public LayeredEmiTexture(EmiRenderable front, EmiRenderable back) {
        this.back = back;
        this.front = front;
    }

    public LayeredEmiTexture(EmiRenderable front, BackgroundContainer back) {
        this.back = convertBackgroundContainer(back);
        this.front = front;
    }

    public LayeredEmiTexture(BackgroundContainer front, EmiRenderable back) {
        this.back = back;
        this.front = convertBackgroundContainer(front);
    }

    public LayeredEmiTexture(BackgroundContainer front, BackgroundContainer back) {
        this.back = convertBackgroundContainer(back);
        this.front = convertBackgroundContainer(front);
    }

    private static EmiTexture convertBackgroundContainer(BackgroundContainer background) {
        return new EmiTexture(background.icon(), background.u(), background.v(), background.width(), background.height());
    }

    @Override
    public void render(DrawContext draw, int x, int y, float delta) {
        front.render(draw, x, y, delta);
        back.render(draw, x, y, delta);
    }

    public EmiRenderable getFront() {
        return front;
    }

    public EmiRenderable getBack() {
        return back;
    }
}
