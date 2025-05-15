package fr.epyi.metropiamod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.epyi.metropiamod.MetropiaMod;
import fr.epyi.metropiamod.network.OpenCharacterCreatorPacket;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkDirection;

public class OpenCharacterCreatorCommand {
    public OpenCharacterCreatorCommand(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("openCharacterCreator").executes(this::openCharacterCreator));
    }

    private int openCharacterCreator(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        ServerPlayerEntity player = source.asPlayer();

        MetropiaMod.NETWORK.sendTo(
                new OpenCharacterCreatorPacket(),
                player.connection.netManager,
                NetworkDirection.PLAY_TO_CLIENT
        );

        return 1;

    }
}
