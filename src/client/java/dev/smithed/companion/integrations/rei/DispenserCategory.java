package dev.smithed.companion.integrations.rei;

import com.google.common.collect.Lists;
import dev.smithed.companion.container.BackgroundContainer;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class DispenserCategory<T extends Display> extends DisplayCategoryExtender<T> {

    public DispenserCategory(Identifier id, Text title, ItemStack icon, BackgroundContainer background) {
        super(id, title, icon, background);
    }

    @Override
    public List<Widget> setupDisplay(T display, Rectangle bounds) {
        final Point startPoint = new Point(bounds.getX()+56, bounds.getY()+7);
        final List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));

        final EntryIngredient output = display.getOutputEntries().get(0);
        widgets.add(Widgets.createSlot(new Point(Short.MIN_VALUE, 0)).entries(output).markOutput());

        int i = 0;
        for (EntryIngredient ingredient: display.getInputEntries()) {
            widgets.add(Widgets.createSlot(new Point(startPoint.x + (18 * (i%3)), startPoint.y + (18 * (i/3))))
                    .entries(ingredient)
                    .markInput()
            );
            i += 1;
        }

        if(this.background != null)
            widgets.add(Widgets.createTexturedWidget(background.icon(), bounds.getX()+5, bounds.getY()+5, background.u(), background.v(), background.width(), background.height()));

        return widgets;
    }
}
