package dev.smithed.companion.mixin.client;

import dev.smithed.companion.utils.DatapackItemUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;

import java.util.HashMap;
import java.util.Map;

@Mixin(ClientWorld.class)
public class ClientWorldMixin implements DatapackItemUtils.DatapackItemHandler {

    private final Map<Identifier, ItemStack> datapackItems = new HashMap<>();

    @Override
    public ItemStack getDatapackItem(Identifier identifier) {
        return datapackItems.get(identifier);
    }

    @Override
    public void registerItem(DatapackItemUtils.DatapackItem item) {
        datapackItems.put(item.identifier(), item.itemStack());
    }
}
