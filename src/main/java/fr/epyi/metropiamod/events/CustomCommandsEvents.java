package fr.epyi.metropiamod.events;

import fr.epyi.metropiamod.MetropiaMod;
import fr.epyi.metropiamod.commands.CustomCommand;
import fr.epyi.metropiamod.commands.GetNbtCommand;
import fr.epyi.metropiamod.commands.SetNbtCommand;
import fr.epyi.metropiamod.commands.SetSkinCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(modid = MetropiaMod.MOD_ID)
public class CustomCommandsEvents {

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        new SetNbtCommand(event.getDispatcher());
        new GetNbtCommand(event.getDispatcher());

        CustomCommand.register(event.getDispatcher());
    }

}
