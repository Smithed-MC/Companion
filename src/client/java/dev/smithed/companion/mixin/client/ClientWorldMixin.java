package dev.smithed.companion.mixin.client;

import dev.smithed.companion.utils.DatapackItemUtils;
import dev.smithed.companion.utils.RegistryUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Mixin(ClientWorld.class)
public class ClientWorldMixin implements DatapackItemUtils.DatapackItemHandler {

    private Map<Identifier, Optional<ItemStack>> datapackItems = new HashMap<>();

    /*
    Get item from definition
     */
    @Override
    public ItemStack smithed$getDatapackItem(Identifier identifier) {
        return datapackItems.get(identifier).orElseThrow();
    }

    /*
    Register an item by putting it into the map
     */
    @Override
    public void smithed$registerItem(DatapackItemUtils.DatapackItem item) {
        datapackItems.put(MinecraftClient.getInstance().world.getRegistryManager().get(RegistryUtils.DATAPACK_ITEM_REGISTRY).getId(item), Optional.of(item.getStack()));
    }

    /*
    Dump map contents by just making it into a new hashmap, Let java garbage collector deal with it.
     */
    @Override
    public void smithed$dumpItems() {
        datapackItems = new HashMap<>();
    }
}
