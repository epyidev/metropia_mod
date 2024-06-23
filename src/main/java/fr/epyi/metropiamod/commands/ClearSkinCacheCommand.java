package fr.epyi.metropiamod.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.epyi.metropiamod.CustomSkinManager;
import fr.epyi.metropiamod.MetropiaMod;
import fr.epyi.metropiamod.network.PacketHandler;
import fr.epyi.metropiamod.network.client.ClientClearSkinCache;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import static net.minecraft.command.Commands.literal;

public class ClearSkinCacheCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {

        // Thing to note, arguments are handled in alphabetical order.
        LiteralArgumentBuilder<CommandSource> clear = literal("reloadskin").executes(ClearSkinCacheCommand::run);

        dispatcher.register(clear);
    }

    private static int run(CommandContext<CommandSource> ctx) {
        try {
            PacketHandler.sendToPlayer(new ClientClearSkinCache(), ctx.getSource().asPlayer());
            CustomSkinManager.sendAllToPlayer(ctx.getSource().asPlayer(), false);
        } catch (CommandSyntaxException e) {
            MetropiaMod.LOGGER.info("This command can only be run from the console");
        }
        ctx.getSource().sendFeedback(new TranslationTextComponent("messages.reloadSkinSuccess"), false);
        return Command.SINGLE_SUCCESS;
    }
}
