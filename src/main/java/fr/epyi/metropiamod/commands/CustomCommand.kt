package fr.epyi.metropiamod.commands

import fr.epyi.metropiamod.MetropiaMod
import fr.epyi.metropiamod.commands.arguments.URLArgument
import net.minecraft.command.arguments.ArgumentSerializer
import net.minecraft.command.arguments.ArgumentTypes

object CustomCommand {
    fun registerNewArgTypes() {
        ArgumentTypes.register(
            MetropiaMod.MOD_ID + ":url_argument",
            URLArgument::class.java, ArgumentSerializer { URLArgument() }
        )
    }
}