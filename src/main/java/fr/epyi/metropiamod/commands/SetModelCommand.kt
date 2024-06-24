package fr.epyi.metropiamod.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import fr.epyi.metropiamod.CustomSkinManager
import fr.epyi.metropiamod.config.SkinConfig
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.ISuggestionProvider
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.command.arguments.EntitySelector
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.text.TranslationTextComponent
import java.util.function.Consumer

object SetModelCommand {
    private const val MODEL_ARG = "modelType"
    private val MODEL_SUGGESTIONS = SuggestionProvider { ctx: CommandContext<CommandSource?>?, builder: SuggestionsBuilder? ->
        ISuggestionProvider.suggest(arrayOf("default", "slim"), builder!!)
    }

    fun register(dispatcher: CommandDispatcher<CommandSource?>) {
        dispatcher.register(Commands.literal("setmodel")
            .requires { sender: CommandSource ->
                (!SkinConfig.SELF_SKIN_NEEDS_OP.get() || sender.hasPermissionLevel(2))
            }.then(
                Commands.argument<String>(MODEL_ARG, StringArgumentType.word()
            ).suggests(
                MODEL_SUGGESTIONS
            ).executes { ctx: CommandContext<CommandSource> ->
                val entity = ctx.source.asPlayer()
                val modelType = StringArgumentType.getString(ctx, MODEL_ARG)
                execute(ctx.source, listOf<ServerPlayerEntity>(entity), modelType)
            }.requires { sender: CommandSource ->
                (!SkinConfig.OTHERS_SELF_SKIN_NEEDS_OP.get() || sender.hasPermissionLevel(2))
            }.then(
                Commands.argument<EntitySelector>("targets", EntityArgument.players()
            ).executes { ctx: CommandContext<CommandSource> ->
                val targetPlayers = EntityArgument.getPlayers(ctx, "targets")
                val modelType = StringArgumentType.getString(ctx, MODEL_ARG)
                execute(ctx.source, targetPlayers, modelType)
            })
        ))
    }

    private fun execute(source: CommandSource, targets: Collection<ServerPlayerEntity>, modelType: String): Int {
        targets.forEach(Consumer<ServerPlayerEntity> forEach@{ target: ServerPlayerEntity? ->
            if (target == null) {
                return@forEach
            }
            CustomSkinManager.setModel(target, modelType)
            source.sendFeedback(TranslationTextComponent("messages.setModelSuccess", target.displayName, modelType), false)
        })
        if (targets.isEmpty()) {
            return -1
        }
        return Command.SINGLE_SUCCESS
    }
}