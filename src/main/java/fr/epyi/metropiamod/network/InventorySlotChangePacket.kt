package fr.epyi.metropiamod.network

import net.minecraft.item.ItemStack
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

class InventorySlotChangePacket(
    var itemStack1: ItemStack,
    var itemStack2: ItemStack,
    var slotId1: Int,
    var slotId2: Int
) {
    companion object {
        fun encode(inventorySlotChangePacket: InventorySlotChangePacket, packetBuffer: PacketBuffer) {
            packetBuffer.writeItemStack(inventorySlotChangePacket.itemStack1)
            packetBuffer.writeItemStack(inventorySlotChangePacket.itemStack2)
            packetBuffer.writeInt(inventorySlotChangePacket.slotId1)
            packetBuffer.writeInt(inventorySlotChangePacket.slotId2)
        }

        fun decode(packetBuffer: PacketBuffer): InventorySlotChangePacket {
            val itemStack1 = packetBuffer.readItemStack()
            val itemStack2 = packetBuffer.readItemStack()
            val slotId1 = packetBuffer.readInt()
            val slotId2 = packetBuffer.readInt()
            return InventorySlotChangePacket(itemStack1, itemStack2, slotId1, slotId2)
        }

        fun handle(packet: InventorySlotChangePacket, ctx: Supplier<NetworkEvent.Context?>?) {
            // Check if the packet is Cl->SV
            if (ctx!!.get()!!.direction == NetworkDirection.PLAY_TO_SERVER) {
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

                val movedItem = packet.itemStack1.copy()
                movedItem.count = packet.itemStack1.count

                val destItem = packet.itemStack2.copy()
                destItem.count = packet.itemStack2.count

                // Switch itemStacks inventory locations
                player.inventory.setInventorySlotContents(packet.slotId1, movedItem)
                player.inventory.setInventorySlotContents(packet.slotId2, destItem)
            }
        }
    }
}