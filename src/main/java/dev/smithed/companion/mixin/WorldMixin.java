package dev.smithed.companion.mixin;

import dev.smithed.companion.registry.DatapackItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(World.class)
public abstract class WorldMixin implements DatapackItem.DatapackItemHandler {

    @Override
    public ItemStack smithed$getDatapackItem(Identifier identifier) {
        return Items.STONE.getDefaultStack();
    }
}
