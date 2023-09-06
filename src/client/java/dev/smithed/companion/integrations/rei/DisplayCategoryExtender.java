package dev.smithed.companion.integrations.rei;

import com.google.common.collect.Lists;
import dev.smithed.companion.container.BackgroundContainer;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public abstract class DisplayCategoryExtender<T extends Display> implements DisplayCategory<T> {

    protected final CategoryIdentifier<T> id;
    protected final Text title;
    protected final Renderer icon;
    protected final BackgroundContainer background;

    public DisplayCategoryExtender(Identifier id, Text title, ItemStack icon) {
        this(id, title, icon, null);
    }

    public DisplayCategoryExtender(Identifier id, Text title, ItemStack icon, BackgroundContainer background) {
        this.id = CategoryIdentifier.of(id);
        this.title = title;
        this.icon = EntryStack.of(VanillaEntryTypes.ITEM, icon);
        this.background = background;
    }

    @Override
    public CategoryIdentifier<T> getCategoryIdentifier() {
        return id;
    }

    @Override
    public Text getTitle() {
        return title;
    }

    @Override
    public Renderer getIcon() {
        return icon;
    }

    @Override
    public int getDisplayWidth(T display) {
        return 174;
    }

    @Override
    public int getDisplayHeight() {
        return 66;
    }

}
