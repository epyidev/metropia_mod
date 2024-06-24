package fr.epyi.metropiamod.network

import fr.epyi.metropiamod.CustomSkinManager
import fr.epyi.metropiamod.item.ClothItem
import fr.epyi.metropiamod.item.ModItems
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

object ClothChange {
    fun encode(clothChange: ClothChange?, packetBuffer: PacketBuffer?) {
    }

    fun decode(packetBuffer: PacketBuffer?): ClothChange {
        return ClothChange
    }

    fun handle(packet: ClothChange?, ctx: Supplier<NetworkEvent.Context?>?) {
        // Check if the packet is Cl->SV

        if (ctx!!.get()!!.direction == NetworkDirection.PLAY_TO_SERVER) {
            // Get the player source
            val player = ctx.get()!!.sender

            // Save inventory items to a list (only items slots 27-35)
            val layerList = ArrayList<String?>()

            for (i in 0..35) {
                val slotItem = player!!.inventory.getStackInSlot(i)
                if (slotItem.item == ItemStack(ModItems.CLOTH.get()).item) {
                    val layerUrl = ClothItem.getClothUrl(slotItem)
                    if (layerUrl.isNotEmpty()) {
                        layerList.add(layerUrl)
                    }
                }
            }

            layerList.add("https://i.imgur.com/6BBXz5m.png")


            if (layerList[0] != null && !layerList[0]!!.isEmpty()) {
                CustomSkinManager.setSkin(player, layerList)
            }
        }
    }
}