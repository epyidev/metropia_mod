package fr.epyi.metropiamod.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class InventorySlotChangePacket {

    public ItemStack itemStack1;
    public ItemStack itemStack2;
    public int slotId1;
    public int slotId2;

    public InventorySlotChangePacket(ItemStack itemStack1, ItemStack itemStack2, int slotId1, int slotId2) {
        this.itemStack1 = itemStack1;
        this.itemStack2 = itemStack2;
        this.slotId1 = slotId1;
        this.slotId2 = slotId2;
    }

    public static void encode(InventorySlotChangePacket inventorySlotChangePacket, PacketBuffer packetBuffer) {
        packetBuffer.writeItemStack(inventorySlotChangePacket.itemStack1);
        packetBuffer.writeItemStack(inventorySlotChangePacket.itemStack2);
        packetBuffer.writeInt(inventorySlotChangePacket.slotId1);
        packetBuffer.writeInt(inventorySlotChangePacket.slotId2);
    }

    public static InventorySlotChangePacket decode(PacketBuffer packetBuffer) {
        ItemStack itemStack1 = packetBuffer.readItemStack();
        ItemStack itemStack2 = packetBuffer.readItemStack();
        int slotId1 = packetBuffer.readInt();
        int slotId2 = packetBuffer.readInt();
        return new InventorySlotChangePacket(itemStack1, itemStack2, slotId1, slotId2);
    }

    public static void handle(InventorySlotChangePacket packet, Supplier<NetworkEvent.Context> ctx) {
        // Check if the packet is Cl->SV
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {

            ServerPlayerEntity player = ctx.get().getSender();

            // If itemStacks isn't air, check if player has it
            assert player != null;
            if (!Objects.requireNonNull(packet.itemStack1.getItem().getRegistryName()).toString().equals("minecraft:air")) {
                if (!player.inventory.hasItemStack(packet.itemStack1)) {
                    player.connection.disconnect(new StringTextComponent("Anticheat: Ejecté pour tentative de triche (Invalid packet usage)"));
                    return;
                }
            }

            if (!Objects.requireNonNull(packet.itemStack2.getItem().getRegistryName()).toString().equals("minecraft:air")) {
                if (!player.inventory.hasItemStack(packet.itemStack2)) {
                    player.connection.disconnect(new StringTextComponent("Anticheat: Ejecté pour tentative de triche (Invalid packet usage)"));
                    return;
                }
            }

            ItemStack movedItem = packet.itemStack1.copy();
            movedItem.setCount(packet.itemStack1.getCount());

            ItemStack destItem = packet.itemStack2.copy();
            destItem.setCount(packet.itemStack2.getCount());

            // Switch itemStacks inventory locations
            player.inventory.setInventorySlotContents(packet.slotId1, movedItem);
            player.inventory.setInventorySlotContents(packet.slotId2, destItem);
        }
    }
}
