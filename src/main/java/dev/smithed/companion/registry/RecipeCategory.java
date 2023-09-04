package dev.smithed.companion.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.smithed.companion.container.BackgroundContainer;
import dev.smithed.companion.container.ItemContainer;
import net.minecraft.text.Text;

import java.util.Optional;

public record RecipeCategory(String display, ItemContainer icon, String inventoryType, Optional<BackgroundContainer> background) {

    public static final Codec<RecipeCategory> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("display").forGetter(RecipeCategory::display),
                    ItemContainer.CODEC.fieldOf("item_icon").forGetter(RecipeCategory::icon),
                    Codec.STRING.fieldOf("inventory").forGetter(RecipeCategory::inventoryType),
                    BackgroundContainer.CODEC.optionalFieldOf("background").forGetter(RecipeCategory::background)
            ).apply(instance, RecipeCategory::new)
    );

    public Text getDisplayText() {
        return Text.of(display);
    }

}
