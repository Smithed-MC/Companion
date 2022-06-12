package dev.smithed.companion.mixin;

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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static dev.smithed.companion.SmithedUtil.getSmithed;
import static dev.smithed.companion.SmithedUtil.hasSmithedNBT;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow private NbtCompound nbt;

    @Inject(method = "getItemBarColor", at = @At("RETURN"), cancellable = true)
    public void itemBarColorInject(CallbackInfoReturnable<Integer> cir) {
        NbtList colorList = nbt.getCompound("smithed").getCompound("durability").getList("colors", NbtElement.INT_TYPE);
        if(hasSmithedNBT(nbt) && colorList != null && colorList.size() > 0) {
            // yes there was other code here but it didn't do anything because it was incomplete so i cut it out.
            cir.setReturnValue(colorList.getInt(0));
        }
    }

    @Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isDamaged()Z"))
    public boolean removeDurabilityWhenCustom(ItemStack itemStack) {
        //prevent vanilla durability from showing up when the ItemStack has smithed durability
        if (itemStack.getNbt() != null && hasSmithedNBT(nbt) && getSmithed(nbt).get("durability") != null)
            return false;

        return itemStack.isDamaged();
    }

    @Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 16))
    public <E> boolean modifyIdLine(List<E> list, E element) {
        //add custom id when present, vanilla one when not
        if (((ItemStack) (Object) this).getNbt() != null && hasSmithedNBT(nbt) && getSmithed(nbt).get("identifier") != null)
            list.add((E)Text.literal(getSmithed(nbt).getString("identifier")).formatted(Formatting.DARK_GRAY));
        else
            list.add(element);

        return true;
    }
}