package dev.smithed.companion.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.smithed.companion.container.ItemContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.Codecs;

import java.util.List;

public record ItemGroupData(ItemContainer icon, Text displayName, String texture, List<ItemContainer> entries) {

    public static Codec<ItemGroupData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ItemContainer.CODEC.fieldOf("icon").forGetter(ItemGroupData::icon),
                    Codecs.TEXT.fieldOf("display_name").forGetter(ItemGroupData::displayName),
                    Codecs.NON_EMPTY_STRING.optionalFieldOf("texture","items.png").forGetter(ItemGroupData::texture),
                    ItemContainer.CODEC.listOf().fieldOf("entries").forGetter(ItemGroupData::entries)
            ).apply(instance, ItemGroupData::new)
    );

}

