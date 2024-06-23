package fr.epyi.metropiamod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class SetNbtCommand {

    public SetNbtCommand(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("setnbt").requires(source -> source.hasPermissionLevel(2)).then(
            Commands.argument("nbt", StringArgumentType.string()).then(
                Commands.argument("value", StringArgumentType.string()).executes(this::setNbt)
            )
        ));
    }

    private int setNbt(CommandContext<CommandSource> context) throws CommandSyntaxException {
        String nbtKey = StringArgumentType.getString(context, "nbt");
        String nbtValue = StringArgumentType.getString(context, "value");
        CommandSource source = context.getSource();
        ServerPlayerEntity player = source.asPlayer();

        ItemStack heldItem = player.getHeldItemMainhand();

        if (!heldItem.isEmpty()) {
            CompoundNBT nbt = heldItem.getOrCreateTag();
            nbt.putString(nbtKey, nbtValue);
            heldItem.setTag(nbt);
            source.sendFeedback(new TranslationTextComponent("messages.setNBTSuccess", nbtKey, nbtValue), true);
            return 1;
        } else {
            source.sendFeedback(new TranslationTextComponent("messages.noItemMainHand"), true);
            return -1;
        }
    }

}
