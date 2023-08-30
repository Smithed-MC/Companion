package dev.smithed.companion.mixin;

import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

import static dev.smithed.companion.utils.NBTUtils.hasSmithedNBT;

@Mixin(TemptGoal.class)
public class TemptGoalMixin {

    public final String NO_INTERACT_TAG = "NoInteract";

    /*
    This inject reaches into the `isTemptedBy` method to add an additional check to see if it's an item with a smithed tag
    This exists to fix issues with thing like WFOAS or COAS
     */
    @Redirect(method = "isTemptedBy", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/Ingredient;test(Lnet/minecraft/item/ItemStack;)Z"))
    public boolean preventTempting(Ingredient ingredient, ItemStack itemStack) {
        //return false if the no interact tag is present
        if (hasSmithedNBT(itemStack) && Objects.requireNonNull(itemStack.getSubNbt("smithed")).getBoolean(NO_INTERACT_TAG))
            return false;
        return ingredient.test(itemStack);
    }
}
