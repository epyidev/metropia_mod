@file:Suppress("INACCESSIBLE_TYPE")

package fr.epyi.metropiamod.network

import fr.epyi.metropiamod.MetropiaMod
import fr.epyi.metropiamod.network.client.ClientChangeSkin
import fr.epyi.metropiamod.network.client.ClientClearSkinCache
import fr.epyi.metropiamod.network.server.ServerRequestSkins
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkRegistry
import net.minecraftforge.fml.network.simple.SimpleChannel

object PacketHandler {
    const val PROTOCOL_VERSION = "2"

    /**
     * Could just use [NetworkRegistry.newSimpleChannel] but this is more descriptive.
     */
    val SKIN_CHANNEL: SimpleChannel = NetworkRegistry.ChannelBuilder
        .named(ResourceLocation(MetropiaMod.MOD_ID, "skin_data"))
        .networkProtocolVersion { PROTOCOL_VERSION }
        .clientAcceptedVersions { it == PROTOCOL_VERSION }
        .serverAcceptedVersions { it == PROTOCOL_VERSION }
        .simpleChannel()

    fun init() {
        SKIN_CHANNEL.registerMessage(
            0, ClientChangeSkin::class.java, ClientChangeSkin::encode, ClientChangeSkin::decode, ClientChangeSkin.Handler::handle
        )
        SKIN_CHANNEL.registerMessage(
            1, ClientClearSkinCache::class.java, ClientClearSkinCache::encode, ClientClearSkinCache::decode, ClientClearSkinCache.Handler::handle
        )
        SKIN_CHANNEL.registerMessage(
            100, ServerRequestSkins::class.java, ServerRequestSkins::encode, ServerRequestSkins::decode, ServerRequestSkins.Handler::handle
        )
    }

    fun sendToPlayer(obj: Any, player: ServerPlayerEntity) {
        SKIN_CHANNEL.sendTo(obj, player.connection.networkManager, NetworkDirection.PLAY_TO_CLIENT)
    }
}

