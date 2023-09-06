package dev.smithed.companion.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.smithed.companion.container.BackgroundContainer;
import dev.smithed.companion.container.ItemContainer;
import net.minecraft.text.Text;

import java.util.Optional;

/**
 * Custom category for grouping smithed recipes into.
 * @param display Name of category
 * @param icon Item to render as the category, usually the workbench (ie. Crafting Table)
 * @param inventoryType Type of inventory to use for recipes, must be:
 *                      * chest, barrel, shulker_box (3x9)
 *                      * dispenser, dropper (3x3)
 *                      * hopper (1x5)
 * @param background Optional background overlay, if your workstation has a custom GUI.
 */
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
