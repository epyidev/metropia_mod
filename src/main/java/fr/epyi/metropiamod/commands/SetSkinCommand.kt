package fr.epyi.metropiamod.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import fr.epyi.metropiamod.CustomSkinManager
import fr.epyi.metropiamod.commands.arguments.URLArgument
import fr.epyi.metropiamod.config.SkinConfig
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.ISuggestionProvider
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.command.arguments.EntitySelector
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.TranslationTextComponent
import java.lang.Boolean
import java.util.function.Consumer
import java.util.function.Predicate
import kotlin.Int
import kotlin.String
import kotlin.arrayOf

object SetSkinCommand {
    private const val URL_ARG = "url"

    private val URL_SUGGESTIONS =
        SuggestionProvider { ctx: CommandContext<CommandSource?>?, builder: SuggestionsBuilder? ->
            ISuggestionProvider.suggest(
                arrayOf("https://", "https://i.imgur.com/9L9Ifze.png"),
                builder
            )
        }

    fun register(dispatcher: CommandDispatcher<CommandSource?>) {
        // Thing to note, arguments are handled in alphabetical order.

        val setSkin: LiteralArgumentBuilder<CommandSource?> = Commands.literal("setskin")
            .requires { sender: CommandSource ->
                (!SkinConfig.SELF_SKIN_NEEDS_OP.get() || sender.hasPermissionLevel(
                    2
                ))
            }
            .then(
                Commands.argument(URL_ARG, URLArgument.urlArg())
                    .suggests(URL_SUGGESTIONS)
                    .executes(Command<CommandSource> { ctx: CommandContext<CommandSource> ->
                        val entity = ctx.source.asPlayer()
                        val url: String = URLArgument.getURL(ctx, URL_ARG)
                        execute(
                            ctx.source,
                            listOf<ServerPlayerEntity>(entity),
                            url
                        )
                    })
                    .requires(Predicate<CommandSource> { sender: CommandSource ->
                        (!SkinConfig.OTHERS_SELF_SKIN_NEEDS_OP.get() || sender.hasPermissionLevel(
                            2
                        ))
                    })
                    .then(
                        Commands.argument<EntitySelector>("targets", EntityArgument.players())
                            .executes { ctx: CommandContext<CommandSource> ->
                                val url: String = URLArgument.getURL(ctx, URL_ARG)
                                val targetPlayers =
                                    EntityArgument.getPlayers(ctx, "targets")
                                execute(ctx.source, targetPlayers, url)
                            })
            )

        dispatcher.register(setSkin)
    }

    private fun execute(source: CommandSource, targets: Collection<ServerPlayerEntity>, skinUrl: String): Int {
        val url = skinUrl
        val whitelist: List<String> = SkinConfig.SKIN_SERVER_WHITELIST.get()
        val passedWhitelist = whitelist.stream().filter { value: String? -> url.startsWith(value!!) }.count()
        if (Boolean.TRUE == SkinConfig.ENABLE_SKIN_SERVER_WHITELIST.get() && passedWhitelist == 0L) {
            val message = TranslationTextComponent("messages.blockedSource")
            val redMessage = message.style.mergeWithFormatting(TextFormatting.RED)
            source.sendFeedback(message.setStyle(redMessage), false)
            return -1
        }
        targets.forEach(Consumer<ServerPlayerEntity> forEach@{ target: ServerPlayerEntity? ->
            if (target == null) {
                return@forEach
            }
            source.sendFeedback(
                TranslationTextComponent("messages.setSkinSuccess", target.displayName, url),
                false
            )
            val skins = ArrayList<String?>()
            skins.add(url)
            CustomSkinManager.setSkin(target, skins)
        })
        if (targets.size == 0) {
            return -1
        }
        return Command.SINGLE_SUCCESS
    }
}