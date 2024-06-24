package fr.epyi.metropiamod.network

import net.minecraft.item.ItemStack
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

class InventoryMergePacket(var itemStack1: ItemStack, var itemStack2: ItemStack, var slotId1: Int, var slotId2: Int) {
    companion object {
        fun encode(inventoryMergePacket: InventoryMergePacket, packetBuffer: PacketBuffer) {
            packetBuffer.writeItemStack(inventoryMergePacket.itemStack1)
            packetBuffer.writeItemStack(inventoryMergePacket.itemStack2)
            packetBuffer.writeInt(inventoryMergePacket.slotId1)
            packetBuffer.writeInt(inventoryMergePacket.slotId2)
        }

        fun decode(packetBuffer: PacketBuffer): InventoryMergePacket {
            val itemStack1 = packetBuffer.readItemStack()
            val itemStack2 = packetBuffer.readItemStack()
            val slotId1 = packetBuffer.readInt()
            val slotId2 = packetBuffer.readInt()
            return InventoryMergePacket(itemStack1, itemStack2, slotId1, slotId2)
        }

        fun handle(packet: InventoryMergePacket, ctx: Supplier<NetworkEvent.Context?>?) {
            // Check if the packet is Cl->SV
            if (ctx!!.get()!!.direction == NetworkDirection.PLAY_TO_SERVER) {
                // Get the player server entity

                val player = checkNotNull(ctx.get()!!.sender)

                if (Objects.requireNonNull(packet.itemStack1.item.registryName).toString() != "minecraft:air") {
                    if (!player.inventory.hasItemStack(packet.itemStack1)) {
                        player.connection.disconnect(StringTextComponent("Anticheat: Ejecté pour tentative de triche (Invalid packet usage)"))
                        return
                    }
                }
                if (Objects.requireNonNull<ResourceLocation?>(packet.itemStack2.item.registryName)
                        .toString() != "minecraft:air"
                ) {
                    if (!player.inventory.hasItemStack(packet.itemStack2)) {
                        player.connection.disconnect(StringTextComponent("Anticheat: Ejecté pour tentative de triche (Invalid packet usage)"))
                        return
                    }
                }

                // Calculate new item stacks
                val newPreviousItem = packet.itemStack1
                newPreviousItem.count = packet.itemStack1.count
                val newMovedItem = packet.itemStack2
                newMovedItem.count = packet.itemStack2.count

                val countToRemove = newPreviousItem.maxStackSize - newPreviousItem.count
                newMovedItem.count = newMovedItem.count - countToRemove
                newPreviousItem.count = newPreviousItem.maxStackSize

                // Change server item stacks
                player.inventory.setInventorySlotContents(packet.slotId1, newPreviousItem)
                player.inventory.setInventorySlotContents(packet.slotId2, newMovedItem)
            }
        }
    }
}