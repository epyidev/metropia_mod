package fr.epyi.metropiamod.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import fr.epyi.metropiamod.CustomSkinManager
import fr.epyi.metropiamod.network.PacketHandler
import fr.epyi.metropiamod.network.client.ClientClearSkinCache
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.TranslationTextComponent

object ClearSkinCacheCommand {
    fun register(dispatcher: CommandDispatcher<CommandSource?>) {
        // Thing to note, arguments are handled in alphabetical order.

        val clear = Commands.literal("reloadskin").executes { obj: CommandContext<CommandSource?>? -> run(obj) }

        dispatcher.register(clear)
    }

    private fun run(ctx: CommandContext<CommandSource?>?): Int {
        try {
            PacketHandler.sendToPlayer(ClientClearSkinCache, ctx!!.source!!.asPlayer())
            CustomSkinManager.sendAllToPlayer(ctx.source!!.asPlayer(), false)
        } catch (e: CommandSyntaxException) {
            println("This command can only be run from the console")
        }
        ctx!!.source!!.sendFeedback(TranslationTextComponent("messages.reloadSkinSuccess"), false)
        return Command.SINGLE_SUCCESS
    }
}