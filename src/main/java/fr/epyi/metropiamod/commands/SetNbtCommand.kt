package fr.epyi.metropiamod.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.TranslationTextComponent

class SetNbtCommand(dispatcher: CommandDispatcher<CommandSource?>) {
    init {
        dispatcher.register(
            Commands.literal("setnbt").requires { source: CommandSource ->
                source.hasPermissionLevel(
                    2
                )
            }.then(
                Commands.argument("nbt", StringArgumentType.string()).then(
                    Commands.argument("value", StringArgumentType.string())
                        .executes { context: CommandContext<CommandSource> ->
                            this.setNbt(
                                context
                            )
                        }
                )
            ))
    }

    @Throws(CommandSyntaxException::class)
    private fun setNbt(context: CommandContext<CommandSource>): Int {
        val nbtKey = StringArgumentType.getString(context, "nbt")
        val nbtValue = StringArgumentType.getString(context, "value")
        val source = context.source
        val player = source.asPlayer()

        val heldItem = player.heldItemMainhand

        if (!heldItem.isEmpty) {
            val nbt = heldItem.getOrCreateTag()
            nbt.putString(nbtKey, nbtValue)
            heldItem.tag = nbt
            source.sendFeedback(TranslationTextComponent("messages.setNBTSuccess", nbtKey, nbtValue), true)
            return 1
        } else {
            source.sendFeedback(TranslationTextComponent("messages.noItemMainHand"), true)
            return -1
        }
    }
}