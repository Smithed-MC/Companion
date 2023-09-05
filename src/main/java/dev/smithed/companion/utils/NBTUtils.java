package dev.smithed.companion.utils;

import net.minecraft.item.ItemStack;

/**
 * A class with useful methods for checking NBT
 * @author dragoncommands
 * @apiNote more to come soon
 */
public class NBTUtils {

    /**
     * Check to see if the item has an existing NBT tag
     * @param stack the stack being checked
     * @param key the key being checked for
     */
    public static boolean hasNBT(ItemStack stack, String key) {
        return stack.getSubNbt(key) != null;
    }

    /**
     * Will return true if this is a "datapack item". Made for special features that
     * @param stack The stack to check for NBT
     */
    public static boolean hasSmithedNBT(ItemStack stack) {
        return hasNBT(stack, "smithed");
    }
}
