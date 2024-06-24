package fr.epyi.metropiamod

import com.google.common.collect.Maps
import fr.epyi.metropiamod.capabilities.ISkinData
import fr.epyi.metropiamod.capabilities.SkinLocationProvider
import fr.epyi.metropiamod.config.SkinConfig
import fr.epyi.metropiamod.network.PacketHandler
import fr.epyi.metropiamod.network.PacketHandler.sendToPlayer
import fr.epyi.metropiamod.network.client.ClientChangeSkin
import fr.epyi.metropiamod.server.ServerSkinData
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.fml.network.PacketDistributor
import java.util.*
import kotlin.collections.ArrayList

object CustomSkinManager {
    private val playerSkins: MutableMap<UUID, ServerSkinData?> = Maps.newHashMap()

    fun setSkin(target: PlayerEntity?, urls: ArrayList<String?>) {
        target?.getCapability(SkinLocationProvider.SKIN_LOC, null)?.ifPresent { skinCap: ISkinData ->
            skinCap.setSkin(urls)
            if (urls[0] != null) {
                PacketHandler.SKIN_CHANNEL.send(
                    PacketDistributor.ALL.noArg(), ClientChangeSkin(
                        target.uniqueID, urls,
                        skinCap.getModelType(), SkinConfig.ALLOW_TRANSPARENT_SKIN.get()
                    )
                )
                playerSkins[target.uniqueID] = ServerSkinData(urls, skinCap.getModelType())
            }
        }
    }

    /**
     * Reset the skin of the target player.
     *
     * @param target the target player
     */
    fun resetSkin(target: PlayerEntity?) {
        val reset = ArrayList<String?>(1)
        reset.add("reset")
        setSkin(target, reset)
    }

    fun setModel(target: PlayerEntity?, modelType: String) {
        target?.getCapability(SkinLocationProvider.SKIN_LOC, null)?.ifPresent { skinCap: ISkinData ->
            skinCap.setModelType(modelType)
            if (modelType.length > 0) {
                PacketHandler.SKIN_CHANNEL.send(
                    PacketDistributor.ALL.noArg(),
                    ClientChangeSkin(
                        target.uniqueID,
                        skinCap.getSkin(),
                        modelType,
                        SkinConfig.ALLOW_TRANSPARENT_SKIN.get()
                    )
                )
                playerSkins[target.uniqueID] = ServerSkinData(skinCap.getSkin(), modelType)
            }
        }
    }

    /**
     * Send all the loaded skins to a player
     * @param player
     * @param excludeSelf
     */
    fun sendAllToPlayer(player: ServerPlayerEntity, excludeSelf: Boolean) {
        for ((key, value) in playerSkins) {
            if (!(excludeSelf && key === player.uniqueID) && value != null) {
                sendToPlayer(
                    ClientChangeSkin(
                        key,
                        value.urls,
                        value.modelType,
                        SkinConfig.ALLOW_TRANSPARENT_SKIN.get()
                    ), player
                )
            }
        }
    }

    fun playerLoggedOut(uuid: UUID) {
        playerSkins.remove(uuid)
    }
}
