package dev.smithed.companion.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.smithed.companion.container.BackgroundContainer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.swing.text.html.Option;
import java.util.Optional;

public record RecipeCategory(String display, Identifier icon, String inventoryType, Optional<BackgroundContainer> background) {

    public static final Codec<RecipeCategory> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("display").forGetter(RecipeCategory::display),
                    Identifier.CODEC.fieldOf("item_icon").forGetter(RecipeCategory::icon),
                    Codec.STRING.fieldOf("inventory").forGetter(RecipeCategory::inventoryType),
                    BackgroundContainer.CODEC.optionalFieldOf("background").forGetter(RecipeCategory::background)
            ).apply(instance, RecipeCategory::new)
    );

    public Text getDisplayText() {
        return Text.of(display);
    }

}
