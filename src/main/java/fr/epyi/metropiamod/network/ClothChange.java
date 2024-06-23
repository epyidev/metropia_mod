package fr.epyi.metropiamod.network;

import fr.epyi.metropiamod.CustomSkinManager;
import fr.epyi.metropiamod.MetropiaMod;
import fr.epyi.metropiamod.item.ClothItem;
import fr.epyi.metropiamod.item.ModItems;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Supplier;

public class ClothChange {

    // ClothChange packet class
    public ClothChange() {
    }

    public static void encode(ClothChange clothChange, PacketBuffer packetBuffer) {
    }

    public static ClothChange decode(PacketBuffer packetBuffer) {
        return new ClothChange();
    }

    public static void handle(ClothChange packet, Supplier<NetworkEvent.Context> ctx) {

        // Check if the packet is Cl->SV
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            // Get the player source
            ServerPlayerEntity player = ctx.get().getSender();

            // Save inventory items to a list (only items slots 27-35)
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

            layerList.add("https://i.imgur.com/6BBXz5m.png");


            if (layerList.get(0) != null && !layerList.get(0).isEmpty()) {
                CustomSkinManager.setSkin(player, layerList);
            }
        }
    }
}
