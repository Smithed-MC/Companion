package dev.smithed.companion.pack_provider;

import net.minecraft.resource.ResourcePackSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public interface SmithedDataPackSource extends ResourcePackSource {

    ResourcePackSource PACK_SOURCE_SMITHED = smithedText();

    static ResourcePackSource smithedText() {
        Text text = new LiteralText("smithed");
        return (text2) -> (new TranslatableText("pack.nameAndSource", text2, text)).formatted(Formatting.AQUA);
    }

}
