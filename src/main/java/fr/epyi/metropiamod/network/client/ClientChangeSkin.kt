package fr.epyi.metropiamod.network.client

import fr.epyi.metropiamod.client.ClientSkinManager
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkEvent
import java.util.*
import java.util.function.Supplier
import kotlin.collections.ArrayList

class ClientChangeSkin(val uuid: UUID, val urls: ArrayList<String?>, val bodyType: String, val isTransparent: Boolean) {
    val size: Int = urls.size

    object Handler {
        fun handle(msg: ClientChangeSkin, ctx: Supplier<NetworkEvent.Context?>?) {
            ctx!!.get()!!.enqueueWork {
                ClientSkinManager.setSkin(
                    msg.uuid,
                    msg.urls,
                    msg.bodyType,
                    msg.isTransparent
                )
            }
            ctx.get()!!.packetHandled = true
        }
    }

    companion object {
        fun encode(msg: ClientChangeSkin, outBuffer: PacketBuffer) {
            outBuffer.writeUniqueId(msg.uuid)

            // Write the size of the list
            outBuffer.writeInt(msg.urls.size)

            // Write each string in the list
            for (url in msg.urls) {
                outBuffer.writeString(url)
            }

            outBuffer.writeString(msg.bodyType)
            outBuffer.writeBoolean(msg.isTransparent)
        }

        fun decode(inBuffer: PacketBuffer): ClientChangeSkin {
            val uuid = inBuffer.readUniqueId()

            // Read the size of the list
            val listSize = inBuffer.readInt()

            // Read each string in the list
            val urls = ArrayList<String?>()
            for (i in 0 until listSize) {
                urls.add(inBuffer.readString())
            }

            val bodyType = inBuffer.readString()
            val isTransparent = inBuffer.readBoolean()

            return ClientChangeSkin(uuid, urls, bodyType, isTransparent)
        }
    }
}