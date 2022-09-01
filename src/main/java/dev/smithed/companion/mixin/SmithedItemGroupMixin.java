package dev.smithed.companion.mixin;

import dev.smithed.companion.SmithedItemGroupExtensions;
import dev.smithed.companion.item_groups.ItemGroupUtils;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemGroup.class)
public abstract class SmithedItemGroupMixin implements SmithedItemGroupExtensions {

    @Shadow
    @Final
    @Mutable
    public static ItemGroup[] GROUPS;

    // We've had our fun, now it's time to remove smithed itemgroups from the mix.
    // FIRST OFF: we're completely removing every single smithedgroup only.
    // To do THIS we need to REMEMBER a list of every name and then remove all with those names.
    // Afterwards we take all that remain and compile them into a whole list.
    @Override
    public void declutterArray() {
        ItemGroup[] tempGroups = GROUPS;
        GROUPS = new ItemGroup[GROUPS.length - ItemGroupUtils.indexes.size()];

        for (int i = 0; i < tempGroups.length; i++) {
            if(!ItemGroupUtils.indexes.contains(i)) {
                GROUPS[i] = tempGroups[i];
            }
        }

        ItemGroupUtils.indexes.clear();
    }
}
