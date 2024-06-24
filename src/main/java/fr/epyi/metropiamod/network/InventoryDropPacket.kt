package fr.epyi.metropiamod.network

import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.PacketBuffer
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

class InventoryDropPacket(var droppedItem: ItemStack, var SlotId: Int) {
    companion object {
        fun encode(inventoryDropPacket: InventoryDropPacket, packetBuffer: PacketBuffer) {
            packetBuffer.writeItemStack(inventoryDropPacket.droppedItem)
            packetBuffer.writeInt(inventoryDropPacket.SlotId)
        }

        fun decode(packetBuffer: PacketBuffer): InventoryDropPacket {
            val itemStack1 = packetBuffer.readItemStack()
            val slotId1 = packetBuffer.readInt()
            return InventoryDropPacket(itemStack1, slotId1)
        }

        fun handle(packet: InventoryDropPacket, ctx: Supplier<NetworkEvent.Context?>?) {
            if (ctx!!.get()!!.direction == NetworkDirection.PLAY_TO_SERVER) {
                val player = checkNotNull(ctx.get()!!.sender)

                if (!player.inventory.hasItemStack(packet.droppedItem)) {
                    player.connection.disconnect(StringTextComponent("Anticheat: Ejecté pour tentative de triche (Invalid packet usage)"))
                    return
                }

                // Drop an item from the player*
                player.dropItem(packet.droppedItem, false, true)

                // Remove item from player's inventory
                player.inventory.setInventorySlotContents(packet.SlotId, ItemStack(Items.AIR))
            }
        }
    }
}