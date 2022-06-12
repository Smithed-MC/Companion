package dev.smithed.companion.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static dev.smithed.companion.SmithedUtil.hasSmithedNBT;

@Mixin(TemptGoal.class)
public abstract class TemptGoalMixin extends Goal {
    public final String NO_INTERACT_TAG = "NoInteract";

    @Shadow @Final private Ingredient food;

    @Inject(method = "isTemptedBy", at = @At("RETURN"), cancellable = true)
    public void isTemptedByInject(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        // Edge cases!!!, Check if the hands have another version of the desired items so we don't remove normal functionality

        // check if one hand has smithed NBT, if so check if the other hand has the ingredient, if it does: exit, if it doesn't: pass
        if((hasSmithedNBT(entity.getMainHandStack()) && this.food.test(entity.getOffHandStack())) || (hasSmithedNBT(entity.getOffHandStack()) && this.food.test(entity.getMainHandStack()))) return;

        // Check if mainhand has the smithed tag: if yes continue, Mainhand
        if ((hasSmithedNBT(entity.getMainHandStack()) && Objects.requireNonNull(entity.getMainHandStack().getSubNbt("smithed")).getBoolean(NO_INTERACT_TAG))) cir.setReturnValue(false);

        // Check if offhand has the smithed tag: if yes continue, Offhand
        if ((hasSmithedNBT(entity.getOffHandStack()) && Objects.requireNonNull(entity.getOffHandStack().getSubNbt("smithed")).getBoolean(NO_INTERACT_TAG))) cir.setReturnValue(false);
    }
}
