package fr.epyi.metropiamod.network.client

import fr.epyi.metropiamod.client.ClientSkinManager
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

object ClientClearSkinCache {
    fun encode(msg: ClientClearSkinCache?, outBuffer: PacketBuffer?) {
    }

    fun decode(inBuffer: PacketBuffer?): ClientClearSkinCache {
        return ClientClearSkinCache
    }

    object Handler {
        fun handle(msg: ClientClearSkinCache?, ctx: Supplier<NetworkEvent.Context?>?) {
            ctx!!.get()!!.enqueueWork { ClientSkinManager.clearSkinCache() }
            ctx.get()!!.packetHandled = true
        }
    }
}