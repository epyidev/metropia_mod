package fr.epyi.metropiamod.server

import fr.epyi.metropiamod.CustomSkinManager
import fr.epyi.metropiamod.MetropiaMod
import fr.epyi.metropiamod.capabilities.ISkinData
import fr.epyi.metropiamod.capabilities.SkinLocationProvider
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber

@EventBusSubscriber(modid = MetropiaMod.MOD_ID)
object ServerEventHook {
    val SKIN_LOCATION: ResourceLocation = ResourceLocation(MetropiaMod.MOD_ID, "skin")

    @SubscribeEvent
    fun attachCapability(event: AttachCapabilitiesEvent<Entity?>) {
        if (event.getObject() is PlayerEntity) {
            event.addCapability(SKIN_LOCATION, SkinLocationProvider())
        }
    }

    @SubscribeEvent
    fun playerJoin(event: EntityJoinWorldEvent) {
        if (event.entity is PlayerEntity) {
            event.entity.getCapability<ISkinData>(SkinLocationProvider.SKIN_LOC, null).ifPresent { skin: ISkinData ->
                if (skin.getSkin().isNotEmpty()) {
                    CustomSkinManager.setSkin(
                        event.entity as PlayerEntity,
                        skin.getSkin()
                    )
                    if (event.entity is ServerPlayerEntity) {
                        CustomSkinManager.sendAllToPlayer(event.entity as ServerPlayerEntity, true)
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun playerLeave(event: PlayerLoggedOutEvent) {
        CustomSkinManager.playerLoggedOut(event.player.uniqueID)
    }
}