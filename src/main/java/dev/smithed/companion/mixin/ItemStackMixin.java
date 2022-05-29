package dev.smithed.companion.mixin;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static dev.smithed.companion.SmithedUtil.getSmithed;
import static dev.smithed.companion.SmithedUtil.hasSmithedNBT;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow private NbtCompound nbt;

    @Shadow public abstract boolean isDamageable();

    @Inject(method = "getItemBarColor", at = @At("RETURN"), cancellable = true)
    public void ItemBarColorInject(CallbackInfoReturnable<Integer> cir) {
        NbtList colorList = nbt.getCompound("smithed").getCompound("durability").getList("colors", NbtElement.INT_TYPE);
        if(hasSmithedNBT(nbt) && colorList != null && colorList.size() > 0) {
            // yes there was other code here but it didn't do anything because it was incomplete so i cut it out.
            cir.setReturnValue(colorList.getInt(0));
        }
    }




    // Very Jank Solution(because i can't inject into the middle of an IF statement) but... It Just Works
    // hope people aren't using this with other mods that modify tooltip code especially at the end!!!!
    @Inject(method = "getTooltip", at = @At(value = "RETURN"), cancellable = true)
    public void passAdvancedTest(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        List<Text> tooltips = cir.getReturnValue();
        // edit: slashed the size and made some reusable methods because im lazy, screw you, me
        if(nbt != null && hasSmithedNBT(nbt) && context.isAdvanced() && tooltips != null) {
            if (getSmithed(nbt).get("durability") != null && isDamageable())
                tooltips.remove(tooltips.size() - 3);

            if (getSmithed(nbt).get("identifier") != null)
                tooltips.set(tooltips.size() -2, Text.literal(getSmithed(nbt).getString("identifier")).formatted(Formatting.DARK_GRAY));

        }

        cir.setReturnValue(tooltips);
    }



}
