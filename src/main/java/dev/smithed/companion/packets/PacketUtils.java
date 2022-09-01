package dev.smithed.companion.packets;

import dev.smithed.companion.SmithedMain;
import dev.smithed.companion.item_groups.ItemGroupUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Map;

import static dev.smithed.companion.SmithedMain.logger;

public class PacketUtils {
    // everything within this map will be registered as a packet listener on the server side
    // is this the most efficient thing to do? probably not but im not making multiple classes to perform minor operations
    public static Map<Identifier, ServerPlayNetworking.PlayChannelHandler> serverPacketMap = Map.ofEntries(
            // processes and sends itemgroup info.
            Map.entry(new Identifier(SmithedMain.MODID, "itemgroup_info_channel"), (server, player, handler, buf, responseSender) -> {
                if(ItemGroupUtils.groupEntries != null) {

                    PacketByteBuf groupBuffer = PacketByteBufs.create();
                    ItemGroupUtils.parseEntries(server);
                    ItemGroupUtils.packItemGroups();

                    logger.info("packed groups: {}",ItemGroupUtils.itemGroups);

                    for (NbtCompound data : ItemGroupUtils.itemGroups) {
                        logger.info(data);
                        groupBuffer.writeNbt(data);
                        SP2C(player, new Identifier(SmithedMain.MODID, "itemgroup_data_channel"), groupBuffer);
                    }
                }
            }),

            // Mark down player as smithed-comp-client user. marker is removed upon logout.
            // Might eventually add more subtags for enabled feature checks however idk rn.
            Map.entry(new Identifier(SmithedMain.MODID, "mark_companion_player"), (server, player, handler, buf, responseSender) -> {
                player.addScoreboardTag("smithed.client");
                logger.info("marked down: " + player.getName().getString() + " as a smithed-companion user");
            })
    );

    // generic method for sending packet to client
    public static void SP2C(ServerPlayerEntity playerEntity, Identifier channelName, PacketByteBuf buf) {
        ServerPlayNetworking.send(playerEntity, channelName, buf);
    }

    public static void registerServerPacketListeners() {
        for (Identifier identifier : serverPacketMap.keySet())
            ServerPlayNetworking.registerGlobalReceiver(identifier, serverPacketMap.get(identifier));
    }

}