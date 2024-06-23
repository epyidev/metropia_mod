package fr.epyi.metropiamod.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class InventoryDropPacket {

    public ItemStack droppedItem;
    public int SlotId;

    public InventoryDropPacket(ItemStack itemStack1, int slotId1) {
        this.droppedItem = itemStack1;
        this.SlotId = slotId1;
    }

    public static void encode(InventoryDropPacket inventoryDropPacket, PacketBuffer packetBuffer) {
        packetBuffer.writeItemStack(inventoryDropPacket.droppedItem);
        packetBuffer.writeInt(inventoryDropPacket.SlotId);
    }

    public static InventoryDropPacket decode(PacketBuffer packetBuffer) {
        ItemStack itemStack1 = packetBuffer.readItemStack();
        int slotId1 = packetBuffer.readInt();
        return new InventoryDropPacket(itemStack1, slotId1);
    }

    public static void handle(InventoryDropPacket packet, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayerEntity player = ctx.get().getSender();

            // Check to avoid player cheating
            assert player != null;
            if (!player.inventory.hasItemStack(packet.droppedItem)) {
                player.connection.disconnect(new StringTextComponent("Anticheat: Ejecté pour tentative de triche (Invalid packet usage)"));
                return;
            }

            // Drop an item from the player*
            player.dropItem(packet.droppedItem, false, true);

            // Remove item from player's inventory
            player.inventory.setInventorySlotContents(packet.SlotId, new ItemStack(Items.AIR));
        }
    }

}
