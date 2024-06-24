package fr.epyi.metropiamod.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import fr.epyi.metropiamod.CustomSkinManager
import fr.epyi.metropiamod.config.SkinConfig
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.command.arguments.EntitySelector
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.text.TranslationTextComponent
import java.util.function.Consumer

object ClearSkinCommand {
    fun register(dispatcher: CommandDispatcher<CommandSource?>) {
        dispatcher.register(Commands.literal("clearskin")
            .requires { sender: CommandSource ->
                (!SkinConfig.SELF_SKIN_NEEDS_OP.get() || sender.hasPermissionLevel(2))
            }.executes { ctx: CommandContext<CommandSource> ->
                val entity = ctx.source.asPlayer()
                execute(ctx.source, listOf<ServerPlayerEntity>(entity))
            }.requires { sender: CommandSource ->
                (!SkinConfig.OTHERS_SELF_SKIN_NEEDS_OP.get() || sender.hasPermissionLevel(2))
            }.then(
                Commands.argument<EntitySelector>("targets", EntityArgument.players()
            ).executes { ctx: CommandContext<CommandSource> ->
                val targetPlayers =
                EntityArgument.getPlayers(ctx, "targets")
                execute(ctx.source, targetPlayers)
            })
        )
    }

    private fun execute(source: CommandSource, targets: Collection<ServerPlayerEntity>): Int {
        targets.forEach(Consumer<ServerPlayerEntity> forEach@{ target: ServerPlayerEntity? ->
            if (target == null) {
                return@forEach
            }
            source.sendFeedback(TranslationTextComponent("messages.reloadClearSuccess", target.displayName), false)
            CustomSkinManager.resetSkin(target)
        })
        if (targets.isEmpty()) {
            return -1
        }
        return Command.SINGLE_SUCCESS
    }
}