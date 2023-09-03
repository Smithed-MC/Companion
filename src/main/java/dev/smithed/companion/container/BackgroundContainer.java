package dev.smithed.companion.container;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

public record BackgroundContainer(Identifier icon, int u, int v, int width, int height) {

    public static final Codec<BackgroundContainer> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("icon").forGetter(BackgroundContainer::icon),
                    Codec.INT.fieldOf("width_offset").forGetter(BackgroundContainer::u),
                    Codec.INT.fieldOf("height_offset").forGetter(BackgroundContainer::v),
                    Codec.INT.fieldOf("width").forGetter(BackgroundContainer::width),
                    Codec.INT.fieldOf("height").forGetter(BackgroundContainer::height)
            ).apply(instance, BackgroundContainer::new)
    );
}
