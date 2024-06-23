package fr.epyi.metropiamod.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class InventorySplitPacket {

    public ItemStack previousItemStack;
    public int splitSlotId;
    public int hoveredSlotId;
    public int splitCount;

    public InventorySplitPacket(ItemStack previousItemStack, int splitSlotId, int hoveredSlotId, int splitCount) {
        this.previousItemStack = previousItemStack;
        this.splitSlotId = splitSlotId;
        this.hoveredSlotId = hoveredSlotId;
        this.splitCount = splitCount;
    }

    public static void encode(InventorySplitPacket inventorySplitPacket, PacketBuffer packetBuffer) {
        packetBuffer.writeItemStack(inventorySplitPacket.previousItemStack);
        packetBuffer.writeInt(inventorySplitPacket.splitSlotId);
        packetBuffer.writeInt(inventorySplitPacket.hoveredSlotId);
        packetBuffer.writeInt(inventorySplitPacket.splitCount);
    }

    public static InventorySplitPacket decode(PacketBuffer packetBuffer) {
        ItemStack previousItemStack = packetBuffer.readItemStack();
        int splitSlotId = packetBuffer.readInt();
        int hoveredSlotId = packetBuffer.readInt();
        int splitCount = packetBuffer.readInt();
        return new InventorySplitPacket(previousItemStack, splitSlotId, hoveredSlotId, splitCount);
    }

    public static void handle(InventorySplitPacket packet, Supplier<NetworkEvent.Context> ctx) {

        // Check if the packet is Cl->SV
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            // Get the player source
            ServerPlayerEntity player = ctx.get().getSender();

            // Check if player has the item stack to split
            assert player != null;
            if(!player.inventory.hasItemStack(packet.previousItemStack)) {
                player.connection.disconnect(new StringTextComponent("Anticheat: Ejecté pour tentative de triche (Invalid packet usage)"));
                return;
            }

            ItemStack newPreviousItemStack = player.inventory.getStackInSlot(packet.splitSlotId).copy();
            newPreviousItemStack.setCount(player.inventory.getStackInSlot(packet.splitSlotId).getCount() - packet.splitCount);

            ItemStack newItemStack = player.inventory.getStackInSlot(packet.splitSlotId).copy();
            newItemStack.setCount(packet.splitCount);

            player.inventory.setInventorySlotContents(packet.splitSlotId, newPreviousItemStack);
            player.inventory.setInventorySlotContents(packet.hoveredSlotId, newItemStack);
        }
    }

}
