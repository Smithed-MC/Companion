package dev.smithed.companion.mixin;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

import static dev.smithed.companion.SmithedUtil.hasSmithedNBT;

@Mixin(TemptGoal.class)
public abstract class TemptGoalMixin extends Goal {
    public final String NO_INTERACT_TAG = "NoInteract";

    @Redirect(method = "isTemptedBy", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/Ingredient;test(Lnet/minecraft/item/ItemStack;)Z"))
    public boolean preventTempting(Ingredient ingredient, ItemStack itemStack) {
        //return false if the no interact tag is present
        if (hasSmithedNBT(itemStack) && Objects.requireNonNull(itemStack.getSubNbt("smithed")).getBoolean(NO_INTERACT_TAG))
            return false;
        return ingredient.test(itemStack);
    }
}