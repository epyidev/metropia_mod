package fr.epyi.metropiamod.client

import fr.epyi.metropiamod.MetropiaMod
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.player.AbstractClientPlayerEntity
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedOutEvent
import net.minecraftforge.client.event.RenderPlayerEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.TickEvent.RenderTickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import java.io.IOException

@EventBusSubscriber(value = [Dist.CLIENT], modid = MetropiaMod.MOD_ID)
object ClientEventHook {
    @SubscribeEvent
    @Throws(IOException::class)
    fun doRender(event: RenderTickEvent) {
        if (event.phase == TickEvent.Phase.START) {
            ClientSkinManager.loadQueuedSkins()
        }
        val client = Minecraft.getInstance().player
        if (client != null) {
            ClientSkinManager.checkSkin(client)
        }
    }

    @SubscribeEvent
    fun leaveWorldEvent(event: LoggedOutEvent) {
        ClientSkinManager.clearSkinCache()
        val client = event.player
        if (client is AbstractClientPlayerEntity) {
            ClientSkinManager.checkSkin(client)
        }
        ClientSkinManager.cleanupSkinData()
    }

    @SubscribeEvent
    fun renderPlayer(event: RenderPlayerEvent.Pre) {
        val client = Minecraft.getInstance().player
        if (event.player is AbstractClientPlayerEntity && event.player !== client) {
            ClientSkinManager.checkSkin(event.player as AbstractClientPlayerEntity)
        }
    }
}