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

public class GetNbtCommand {

    public GetNbtCommand(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("getnbt").requires(source -> source.hasPermissionLevel(2)).then(
            Commands.argument("nbt", StringArgumentType.string()).executes(this::getNbt)
        ));
    }

    private int getNbt(CommandContext<CommandSource> context) throws CommandSyntaxException {
        String nbtKey = StringArgumentType.getString(context, "nbt");
        CommandSource source = context.getSource();
        ServerPlayerEntity player = source.asPlayer();

        ItemStack heldItem = player.getHeldItemMainhand();

        if (!heldItem.isEmpty() && heldItem.hasTag()) {
            CompoundNBT nbt = heldItem.getTag();
            assert nbt != null;
            if (nbt.contains(nbtKey)) {
                String nbtValue = nbt.getString(nbtKey);
                source.sendFeedback(new TranslationTextComponent("messages.NBTKey", nbtKey, nbtValue), true);
            } else {
                source.sendFeedback(new TranslationTextComponent("messages.noNBTKey", nbtKey), true);
            }
            return 1;
        } else {
            source.sendFeedback(new TranslationTextComponent("messages.noItemMainHand"), true);
            return -1;
        }
    }

}
