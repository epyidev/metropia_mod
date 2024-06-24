package fr.epyi.metropiamod.events

import fr.epyi.metropiamod.MetropiaMod
import fr.epyi.metropiamod.commands.CustomCommand
import fr.epyi.metropiamod.commands.GetNbtCommand
import fr.epyi.metropiamod.commands.SetNbtCommand
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber

@EventBusSubscriber(modid = MetropiaMod.MOD_ID)
object CustomCommandsEvents {
    @SubscribeEvent
    fun onCommandRegister(event: RegisterCommandsEvent) {
        SetNbtCommand(event.dispatcher)
        GetNbtCommand(event.dispatcher)

        CustomCommand.register(event.dispatcher)
    }
}