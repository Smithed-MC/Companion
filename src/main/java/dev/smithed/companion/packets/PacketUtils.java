package dev.smithed.companion.packets;

import dev.smithed.companion.PostReloadListener;
import dev.smithed.companion.SmithedMain;
import dev.smithed.companion.item_groups.ItemGroupData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.Map;
import java.util.Objects;

public class PacketUtils {
    // everything within this map will be registered as a packet listener on the server side
    // is this the most efficient thing to do? probably not but im not making multiple classes to perform minor operations
    public static Map<Identifier, ServerPlayNetworking.PlayChannelHandler> serverPacketMap = Map.ofEntries(
            // processes and sends itemgroup info.
            Map.entry(new Identifier(SmithedMain.MODID, "itemgroup_info_channel"), (server, player, handler, buf, responseSender) -> {
                for (ItemGroupData data : PostReloadListener.unregisteredItemGroups.values()) {
                    PacketByteBuf groupBuffer = PacketByteBufs.create();
                    NbtCompound group = new NbtCompound();
                    data.toNbt(group);
                    groupBuffer.writeNbt(group);
                    SP2C(player, new Identifier(SmithedMain.MODID, "itemgroup_info_channel"), groupBuffer);
                }
            }),
            // mark down player as smithed-comp-client user. marker is removed upon logout.
            // might eventually add more subtags for enabled feature checks however idk rn.
            Map.entry(new Identifier(SmithedMain.MODID, "mark_companion_player"), (server, player, handler, buf, responseSender) -> {
                player.addScoreboardTag("smithed.client");
                SmithedMain.logger.info("marked down: " + player.getName() + " as a smithed-companion user");
            })
    );

    // everything within this map will be registered as a packet listener on the client side
    public static Map<Identifier, ClientPlayNetworking.PlayChannelHandler> clientPacketMap = Map.ofEntries(
            // decodes itemgroup info
            Map.entry(new Identifier(SmithedMain.MODID, "itemgroup_info_channel"), (client, handler, buf, responseSender) -> {

                ItemGroupData itemGroupData = ItemGroupData.fromNBT(Objects.requireNonNull(buf.readUnlimitedNbt()));
                // NOT the best way to do this
                // but screw you, Quick and dirty
                DefaultedList<ItemStack> stacks = DefaultedList.of();
                if(!SmithedMain.registeredItemGroups.containsKey(itemGroupData.getName()))
                    SmithedMain.registeredItemGroups.put(itemGroupData.getName(), itemGroupData.toItemGroup());
                else {
                    stacks.addAll(itemGroupData.getItemStacks());
                    SmithedMain.registeredItemGroups.get(itemGroupData.getName()).appendStacks(stacks);
                }
            })
    );

    // generic method for sending packet to client
    public static void SP2C(ServerPlayerEntity playerEntity, Identifier channelName, PacketByteBuf buf) {
        ServerPlayNetworking.send(playerEntity, channelName, buf);
    }

    // generic method for sending packet to server
    public static void SP2S(Identifier identifier, PacketByteBuf buf) {
        ClientPlayNetworking.send(identifier, buf);
    }

    public static void registerServerPacketListeners() {
        for (Identifier identifier : serverPacketMap.keySet())
            ServerPlayNetworking.registerGlobalReceiver(identifier, serverPacketMap.get(identifier));
    }

    public static void registerClientPacketListeners() {
        for (Identifier identifier : clientPacketMap.keySet())
            ClientPlayNetworking.registerGlobalReceiver(identifier, clientPacketMap.get(identifier));
    }
}