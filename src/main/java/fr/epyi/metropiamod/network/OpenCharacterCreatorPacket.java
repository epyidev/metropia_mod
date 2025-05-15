package fr.epyi.metropiamod.network;

import fr.epyi.metropiamod.CustomSkinManager;
import fr.epyi.metropiamod.config.SkinConfig;
import fr.epyi.metropiamod.gui.CharacterCreatorContainer;
import fr.epyi.metropiamod.gui.CharacterCreatorGui;
import fr.epyi.metropiamod.gui.CustomInventoryContainer;
import fr.epyi.metropiamod.gui.CustomInventoryGui;
import fr.epyi.metropiamod.item.ClothItem;
import fr.epyi.metropiamod.item.ModItems;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static net.minecraft.client.Minecraft.getInstance;

public class OpenCharacterCreatorPacket {

    public OpenCharacterCreatorPacket() {
    }

    public static void encode(OpenCharacterCreatorPacket openCharacterCreatorPacket, PacketBuffer packetBuffer) {
    }

    public static OpenCharacterCreatorPacket decode(PacketBuffer packetBuffer) {
        return new OpenCharacterCreatorPacket();
    }

    public static void handle(OpenCharacterCreatorPacket packet, Supplier<NetworkEvent.Context> ctx) {
        // Check if the packet is Sv->Cl
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {

            PlayerEntity player = getInstance().player;
            assert player != null;

            CharacterCreatorContainer characterCreatorContainer = new CharacterCreatorContainer(0, player);

            ITextComponent title = new ITextComponent() {
                @Override
                public Style getStyle() {
                    return null;
                }

                @Override
                public String getUnformattedComponentText() {
                    return "";
                }

                @Override
                public String getString() {
                    return "Character Creator";
                }

                @Override
                public List<ITextComponent> getSiblings() {
                    return Collections.emptyList();
                }

                @Override
                public IFormattableTextComponent copyRaw() {
                    return null;
                }

                @Override
                public IFormattableTextComponent deepCopy() {
                    return null;
                }

                @Override
                public IReorderingProcessor func_241878_f() {
                    return null;
                }
            };

            getInstance().displayGuiScreen(new CharacterCreatorGui(characterCreatorContainer, player.inventory, title));
        }
    }
}
