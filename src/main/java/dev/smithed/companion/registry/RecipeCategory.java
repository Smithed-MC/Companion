package dev.smithed.companion.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public record RecipeCategory(String display, Identifier icon, String inventoryType) {

    public static final Codec<RecipeCategory> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("display").forGetter(RecipeCategory::display),
                    Identifier.CODEC.fieldOf("item_icon").forGetter(RecipeCategory::icon),
                    Codec.STRING.fieldOf("inventory").forGetter(RecipeCategory::inventoryType)
            ).apply(instance, RecipeCategory::new)
    );

    public Text getDisplayText() {
        return Text.of(display);
    }

}
