package fr.epyi.metropiamod.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import fr.epyi.metropiamod.MetropiaMod
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.ITextComponent

object HomeCommand {
    fun register(dispatcher: CommandDispatcher<CommandSource?>) {
        dispatcher.register(Commands.literal("home").executes(this::execute))
    }
    private fun execute(context: CommandContext<CommandSource>): Int {
        val source = context.source
        val player = source.asPlayer()
        val home = player.persistentData.getIntArray(MetropiaMod.MOD_ID + ":home")

        if (home.isEmpty()) {
            source.sendFeedback(ITextComponent.getTextComponentOrEmpty("Vous n'avez pas de home défini."), false)
            return 0
        } else {
            player.teleport(
                player.serverWorld,
                home[0].toDouble(),
                home[1].toDouble(),
                home[2].toDouble(),
                player.rotationYaw,
                player.rotationPitch
            )
            source.sendFeedback(ITextComponent.getTextComponentOrEmpty("Téléportation en ${home[0]}, y: ${home[1]}, z: ${home[2]}"), false)
        }
        return 1
    }
}