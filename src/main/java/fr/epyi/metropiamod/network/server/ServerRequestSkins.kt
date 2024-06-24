package fr.epyi.metropiamod.network.server

import fr.epyi.metropiamod.CustomSkinManager
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

object ServerRequestSkins {
    fun encode(msg: ServerRequestSkins?, outBuffer: PacketBuffer?) {
    }

    fun decode(inBuffer: PacketBuffer?): ServerRequestSkins {
        return ServerRequestSkins
    }

    object Handler {
        fun handle(msg: ServerRequestSkins?, ctx: Supplier<NetworkEvent.Context?>?) {
            ctx!!.get()!!.enqueueWork {
                CustomSkinManager.sendAllToPlayer(
                    ctx.get()!!.sender!!,
                    false
                )
            }
            ctx.get()!!.packetHandled = true
        }
    }
}