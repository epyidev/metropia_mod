package fr.epyi.metropiamod.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.TranslationTextComponent

class GetNbtCommand(dispatcher: CommandDispatcher<CommandSource?>) {
    init {
        dispatcher.register(
            Commands.literal("getnbt").requires { source: CommandSource ->
                source.hasPermissionLevel(
                    2
                )
            }.then(
                Commands.argument("nbt", StringArgumentType.string())
                    .executes { context: CommandContext<CommandSource> ->
                        this.getNbt(
                            context
                        )
                    }
            ))
    }

    @Throws(CommandSyntaxException::class)
    private fun getNbt(context: CommandContext<CommandSource>): Int {
        val nbtKey = StringArgumentType.getString(context, "nbt")
        val source = context.source
        val player = source.asPlayer()

        val heldItem = player.heldItemMainhand

        if (!heldItem.isEmpty && heldItem.hasTag()) {
            val nbt = checkNotNull(heldItem.tag)
            if (nbt.contains(nbtKey)) {
                val nbtValue = nbt.getString(nbtKey)
                source.sendFeedback(TranslationTextComponent("messages.NBTKey", nbtKey, nbtValue), true)
            } else {
                source.sendFeedback(TranslationTextComponent("messages.noNBTKey", nbtKey), true)
            }
            return 1
        } else {
            source.sendFeedback(TranslationTextComponent("messages.noItemMainHand"), true)
            return -1
        }
    }
}