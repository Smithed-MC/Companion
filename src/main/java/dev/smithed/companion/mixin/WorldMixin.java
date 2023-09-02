package dev.smithed.companion.mixin;

import dev.smithed.companion.utils.DatapackItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(World.class)
public abstract class WorldMixin implements DatapackItemUtils.DatapackItemHandler {

    @Override
    public ItemStack smithed$getDatapackItem(Identifier identifier) {
        return Items.STONE.getDefaultStack();
    }
}
