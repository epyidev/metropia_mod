package fr.epyi.metropiamod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.epyi.metropiamod.MetropiaMod;
import fr.epyi.metropiamod.commands.arguments.URLArgument;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class CustomCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
            Commands.literal("setnbt")
                .requires(source -> source.hasPermissionLevel(2))
                .then(
                    Commands.argument("nbt", StringArgumentType.string())
                        .then(
                            Commands.argument("value", StringArgumentType.string())
                                .executes(CustomCommand::setNBT)
                        )
                )
        );

        dispatcher.register(
            Commands.literal("getnbt")
                .requires(source -> source.hasPermissionLevel(2))
                .then(
                    Commands.argument("nbt", StringArgumentType.string())
                        .executes(CustomCommand::getNBT)
                )
        );

        SetSkinCommand.register(dispatcher);
        SetModelCommand.register(dispatcher);
        ClearSkinCacheCommand.register(dispatcher);
        ClearSkinCommand.register(dispatcher);
    }

    public static void registerNewArgTypes() {
        ArgumentTypes.register(MetropiaMod.MOD_ID + ":url_argument", URLArgument.class, new ArgumentSerializer<>(URLArgument::urlArg));
    }

    private static int setNBT(CommandContext<CommandSource> context) throws CommandException {
        String nbtKey = StringArgumentType.getString(context, "nbt");
        String nbtValue = StringArgumentType.getString(context, "value");
        CommandSource source = context.getSource();
        ItemStack heldItem = null;
        try {
            heldItem = source.asPlayer().getHeldItemMainhand();
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }

        if (!heldItem.isEmpty()) {
            CompoundNBT nbt = heldItem.getOrCreateTag();
            nbt.putString(nbtKey, nbtValue);
            heldItem.setTag(nbt);
        }

        return 1;
    }

    private static int getNBT(CommandContext<CommandSource> context) throws CommandException {
        String nbtKey = StringArgumentType.getString(context, "nbt");
        CommandSource source = context.getSource();
        ItemStack heldItem = null;
        try {
            heldItem = source.asPlayer().getHeldItemMainhand();
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }

        if (!heldItem.isEmpty() && heldItem.hasTag()) {
            CompoundNBT nbt = heldItem.getTag();
            assert nbt != null;
            if (nbt.contains(nbtKey)) {
                String nbtValue = nbt.getString(nbtKey);
                source.sendFeedback(new TranslationTextComponent("messages.NBTKey", nbtKey, nbtValue), false);
            } else {
                source.sendErrorMessage(new TranslationTextComponent("messages.noNBTKey", nbtKey));
            }
        } else {
            source.sendErrorMessage(new TranslationTextComponent("messages.noItemMainHand"));
        }

        return 1;
    }
}