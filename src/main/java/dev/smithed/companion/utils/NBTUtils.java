package dev.smithed.companion.utils;

import net.minecraft.item.ItemStack;

public class NBTUtils {

    /*
    Check to see if the item has an existing NBT tag
     */
    public static boolean hasNBT(ItemStack stack, String key) {
        return stack.getSubNbt(key) != null;
    }

    /*
    Will return true if this is a "datapack item". Made for special features that
    This method has one parameter:
    - Itemstack stack: The stack to check for NBT
     */
    public static boolean hasSmithedNBT(ItemStack stack) {
        return hasNBT(stack, "smithed");
    }
}
