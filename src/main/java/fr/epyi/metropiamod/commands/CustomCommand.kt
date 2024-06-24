package fr.epyi.metropiamod.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import fr.epyi.metropiamod.MetropiaMod
import fr.epyi.metropiamod.commands.arguments.URLArgument
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.ArgumentSerializer
import net.minecraft.command.arguments.ArgumentTypes
import net.minecraft.item.ItemStack
import net.minecraft.util.text.TranslationTextComponent

object CustomCommand {
    fun register(dispatcher: CommandDispatcher<CommandSource?>) {
        dispatcher.register(
            Commands.literal("setnbt")
                .requires { source: CommandSource -> source.hasPermissionLevel(2) }
                .then(
                    Commands.argument("nbt", StringArgumentType.string())
                        .then(
                            Commands.argument("value", StringArgumentType.string())
                                .executes { obj: CommandContext<CommandSource?>? -> setNBT(obj) }
                        )
                )
        )

        dispatcher.register(
            Commands.literal("getnbt")
                .requires { source: CommandSource -> source.hasPermissionLevel(2) }
                .then(
                    Commands.argument("nbt", StringArgumentType.string())
                        .executes { obj: CommandContext<CommandSource?>? -> getNBT(obj) }
                )
        )

        SetSkinCommand.register(dispatcher)
        SetModelCommand.register(dispatcher)
        ClearSkinCacheCommand.register(dispatcher)
        ClearSkinCommand.register(dispatcher)
    }

    fun registerNewArgTypes() {
        ArgumentTypes.register(
            MetropiaMod.MOD_ID + ":url_argument",
            URLArgument::class.java, ArgumentSerializer { URLArgument() }
        )
    }

    @Throws(CommandException::class)
    private fun setNBT(context: CommandContext<CommandSource?>?): Int {
        val nbtKey = StringArgumentType.getString(context, "nbt")
        val nbtValue = StringArgumentType.getString(context, "value")
        val source = context!!.source
        var heldItem: ItemStack? = null
        try {
            heldItem = source!!.asPlayer().heldItemMainhand
        } catch (e: CommandSyntaxException) {
            throw RuntimeException(e)
        }

        if (!heldItem.isEmpty) {
            val nbt = heldItem.getOrCreateTag()
            nbt.putString(nbtKey, nbtValue)
            heldItem.tag = nbt
        }

        return 1
    }

    @Throws(CommandException::class)
    private fun getNBT(context: CommandContext<CommandSource?>?): Int {
        val nbtKey = StringArgumentType.getString(context, "nbt")
        val source = context!!.source
        var heldItem: ItemStack? = null
        try {
            heldItem = source!!.asPlayer().heldItemMainhand
        } catch (e: CommandSyntaxException) {
            throw RuntimeException(e)
        }

        if (!heldItem!!.isEmpty && heldItem.hasTag()) {
            val nbt = checkNotNull(heldItem.tag)
            if (nbt.contains(nbtKey)) {
                val nbtValue = nbt.getString(nbtKey)
                source.sendFeedback(TranslationTextComponent("messages.NBTKey", nbtKey, nbtValue), false)
            } else {
                source.sendErrorMessage(TranslationTextComponent("messages.noNBTKey", nbtKey))
            }
        } else {
            source.sendErrorMessage(TranslationTextComponent("messages.noItemMainHand"))
        }

        return 1
    }
}