package fr.epyi.metropiamod.network

import net.minecraft.item.ItemStack
import net.minecraft.network.PacketBuffer
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

class InventorySplitPacket(
    var previousItemStack: ItemStack,
    var splitSlotId: Int,
    var hoveredSlotId: Int,
    var splitCount: Int
) {
    companion object {
        fun encode(inventorySplitPacket: InventorySplitPacket, packetBuffer: PacketBuffer) {
            packetBuffer.writeItemStack(inventorySplitPacket.previousItemStack)
            packetBuffer.writeInt(inventorySplitPacket.splitSlotId)
            packetBuffer.writeInt(inventorySplitPacket.hoveredSlotId)
            packetBuffer.writeInt(inventorySplitPacket.splitCount)
        }

        fun decode(packetBuffer: PacketBuffer): InventorySplitPacket {
            val previousItemStack = packetBuffer.readItemStack()
            val splitSlotId = packetBuffer.readInt()
            val hoveredSlotId = packetBuffer.readInt()
            val splitCount = packetBuffer.readInt()
            return InventorySplitPacket(previousItemStack, splitSlotId, hoveredSlotId, splitCount)
        }

        fun handle(packet: InventorySplitPacket, ctx: Supplier<NetworkEvent.Context?>?) {
            // Check if the packet is Cl->SV

            if (ctx!!.get()!!.direction == NetworkDirection.PLAY_TO_SERVER) {
                // Get the player source
                val player = checkNotNull(ctx.get()!!.sender)

                if (!player.inventory.hasItemStack(packet.previousItemStack)) {
                    player.connection.disconnect(StringTextComponent("Anticheat: Ejecté pour tentative de triche (Invalid packet usage)"))
                    return
                }

                val newPreviousItemStack = player.inventory.getStackInSlot(packet.splitSlotId).copy()
                newPreviousItemStack.count =
                    player.inventory.getStackInSlot(packet.splitSlotId).count - packet.splitCount

                val newItemStack = player.inventory.getStackInSlot(packet.splitSlotId).copy()
                newItemStack.count = packet.splitCount

                player.inventory.setInventorySlotContents(packet.splitSlotId, newPreviousItemStack)
                player.inventory.setInventorySlotContents(packet.hoveredSlotId, newItemStack)
            }
        }
    }
}