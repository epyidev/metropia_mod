package fr.epyi.metropiamod.events

import fr.epyi.metropiamod.MetropiaMod
import fr.epyi.metropiamod.commands.*
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber

@EventBusSubscriber(modid = MetropiaMod.MOD_ID)
object CustomCommandsEvents {

    @SubscribeEvent
    fun onCommandRegister(event: RegisterCommandsEvent) {

        // Exemples de commandes pour ne pas oublier comment ça fonctionne
        // SetHomeCommand.register(event.dispatcher)
        // HomeCommand.register(event.dispatcher)

        SetSkinCommand.register(event.dispatcher)
        SetNbtCommand.register(event.dispatcher)
        SetModelCommand.register(event.dispatcher)
        GetNbtCommand.register(event.dispatcher)
        ClearSkinCommand.register(event.dispatcher)
        ClearSkinCacheCommand.register(event.dispatcher)
    }
}