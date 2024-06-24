package fr.epyi.metropiamod.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import fr.epyi.metropiamod.MetropiaMod
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.ITextComponent

object SetHomeCommand {
    fun register(dispatcher: CommandDispatcher<CommandSource?>) {
        dispatcher.register(Commands.literal("sethome").executes(this::execute))
    }
    private fun execute(context: CommandContext<CommandSource>): Int {
        val source = context.source
        val player = source.asPlayer()
        val playerCoords = kotlin.IntArray(3)
        playerCoords[0] = player.posX.toInt()
        playerCoords[1] = player.posY.toInt()
        playerCoords[2] = player.posZ.toInt()

        player.persistentData.putIntArray(MetropiaMod.MOD_ID + ":home", playerCoords)

        val message = ITextComponent.getTextComponentOrEmpty("Home défini en x: ${playerCoords[0]}, y: ${playerCoords[1]}, z: ${playerCoords[2]}")
        source.sendFeedback(message, false)
        return 1
    }
}
