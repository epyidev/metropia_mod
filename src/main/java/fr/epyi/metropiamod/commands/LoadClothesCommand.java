package fr.epyi.metropiamod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.epyi.metropiamod.CustomSkinManager;
import fr.epyi.metropiamod.config.SkinConfig;
import fr.epyi.metropiamod.item.ClothItem;
import fr.epyi.metropiamod.item.ModItems;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkDirection;

import java.util.ArrayList;
import java.util.Objects;

public class LoadClothesCommand {
    public LoadClothesCommand(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("loadclothes").executes(this::loadClothes));
    }

    private int loadClothes(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        ServerPlayerEntity player = source.asPlayer();

        ItemStack heldItem = player.getHeldItemMainhand();

        ArrayList<String> layerList = new ArrayList<>();

        for (int i = 27; i < 36; i++) {
            ItemStack slotItem = player.inventory.getStackInSlot(i);
            if (Objects.equals(slotItem.getItem(), new ItemStack(ModItems.CLOTH.get()).getItem())) {
                String layerUrl = ClothItem.getClothUrl(slotItem);
                if (!layerUrl.isEmpty()) {
                    layerList.add(layerUrl);
                }
            }
        }

        layerList.add(SkinConfig.SKIN_VOID_SKIN_URL.get());


        if (layerList.get(0) != null && !layerList.get(0).isEmpty()) {
            CustomSkinManager.setSkin(player, layerList);
        }

        return 1;

    }
}
