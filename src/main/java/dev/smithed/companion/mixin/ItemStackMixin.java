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

    @Shadow public abstract int getDamage();

    @Shadow public abstract int getMaxDamage();

    // used to make sure we don't calculate a color more than once, feel free to replace with something better
    // if possible - dragon

    @Inject(method = "getItemBarColor", at = @At("RETURN"), cancellable = true)
    public void itemBarColorInject(CallbackInfoReturnable<Integer> cir) {
        NbtList colorList = nbt.getCompound("smithed").getCompound("durability").getList("colors", NbtElement.INT_TYPE);
        if(hasSmithedNBT(nbt) && colorList != null && colorList.size() > 0) {
            int itemBarColor = colorList.getInt(0);
            if(colorList.size() > 1) {
                // split into sections and then find which section this bar is currently in
                // this can be calculated with this
                int trueDamage = this.getDamage();
                int curSection = (trueDamage / (this.getMaxDamage() / colorList.size()));
                float lerpScale = (float) ((trueDamage / (this.getMaxDamage() / colorList.size())) - (curSection > 1 ? curSection : 0));

                //logger.info(curSection);
                //logger.info(lerpScale);

                int colorA = colorList.getInt(curSection), colorB = colorList.getInt(curSection + 1);
                int bitShiftA = (int)(256 * lerpScale),bitShiftB = 256 - bitShiftA;

                // final bar color after being bitshifted
                itemBarColor = (((((colorA & 0x00ff00ff) * bitShiftB) + ((colorB & 0x00ff00ff) * bitShiftA)) >> 8) & 0x00ff00ff) | (((((colorA & 0xff00ff00) * bitShiftB) + ((colorB & 0xff00ff00) * bitShiftA)) >> 8) & 0xff00ff00);
            }
            cir.setReturnValue(itemBarColor);
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