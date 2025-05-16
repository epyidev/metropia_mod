package fr.epyi.metropiamod.network;

import com.sun.org.apache.xpath.internal.operations.Bool;
import fr.epyi.metropiamod.CustomSkinManager;
import fr.epyi.metropiamod.config.SkinConfig;
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

public class ApplyAppearancePacket {

    public boolean applyClothes;
    public String bodyType;
    public String bodyColor;
    public String hairType;
    public String beardType;
    public String hairColor;
    public String eyeType;
    public String eyeColor;
    public String mouthType;
    public String noseType;

    // ClothChange packet class
    public ApplyAppearancePacket(Boolean applyClothes, String bodyType, String bodyColor, String hairType, String beardType, String hairColor, String eyeType, String eyeColor, String mouthType, String noseType) {
        this.applyClothes = applyClothes;
        this.bodyType = bodyType;
        this.bodyColor = bodyColor;
        this.hairType = hairType;
        this.beardType = beardType;
        this.hairColor = hairColor;
        this.eyeType = eyeType;
        this.eyeColor = eyeColor;
        this.mouthType = mouthType;
        this.noseType = noseType;

    }

    public static void encode(ApplyAppearancePacket applyAppearancePacket, PacketBuffer packetBuffer) {
        packetBuffer.writeBoolean(applyAppearancePacket.applyClothes);
        packetBuffer.writeString(applyAppearancePacket.bodyType);
        packetBuffer.writeString(applyAppearancePacket.bodyColor);
        packetBuffer.writeString(applyAppearancePacket.hairType);
        packetBuffer.writeString(applyAppearancePacket.beardType);
        packetBuffer.writeString(applyAppearancePacket.hairColor);
        packetBuffer.writeString(applyAppearancePacket.eyeType);
        packetBuffer.writeString(applyAppearancePacket.eyeColor);
        packetBuffer.writeString(applyAppearancePacket.mouthType);
        packetBuffer.writeString(applyAppearancePacket.noseType);
    }

    public static ApplyAppearancePacket decode(PacketBuffer packetBuffer) {
        Boolean applyClothes = packetBuffer.readBoolean();
        String bodyType = packetBuffer.readString();
        String bodyColor = packetBuffer.readString();
        String hairType = packetBuffer.readString();
        String beardType = packetBuffer.readString();
        String hairColor = packetBuffer.readString();
        String eyeType = packetBuffer.readString();
        String eyeColor = packetBuffer.readString();
        String mouthType = packetBuffer.readString();
        String noseType = packetBuffer.readString();

        return new ApplyAppearancePacket(applyClothes, bodyType, bodyColor, hairType, beardType, hairColor, eyeType, eyeColor, mouthType, noseType);
    }

    public static void handle(ApplyAppearancePacket packet, Supplier<NetworkEvent.Context> ctx) {

        // Check if the packet is Cl->SV
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            // Get the player source
            ServerPlayerEntity player = ctx.get().getSender();

            // Set up an array list to store the skin layers
            ArrayList<String> layerList = new ArrayList<>();

            // Add by default a transparent skin to ensure the player have at least a skin
            layerList.add(SkinConfig.SKIN_VOID_SKIN_URL.get());

            // Add the default skin without clothes to the layer list
            layerList.add(packet.bodyType);
            layerList.add(packet.bodyColor); // Add color for previous layer
            layerList.add(packet.hairType);
            layerList.add(packet.hairColor); // Add color for previous layer
            layerList.add(packet.beardType);
            layerList.add(packet.hairColor); // Add color for previous layer
            layerList.add(packet.eyeType);
            layerList.add(packet.eyeColor); // Add color for previous layer
            layerList.add(packet.mouthType);
            layerList.add(packet.bodyColor); // Add color for previous layer
            layerList.add(packet.noseType);
            layerList.add(packet.bodyColor); // Add color for previous layer

            // Add in the array clothes if we should
            if (packet.applyClothes) {
                for (int i = 27; i < 36; i++) {
                    ItemStack slotItem = player.inventory.getStackInSlot(i);
                    if (Objects.equals(slotItem.getItem(), new ItemStack(ModItems.CLOTH.get()).getItem())) {
                        String layerUrl = ClothItem.getClothUrl(slotItem);
                        if (!layerUrl.isEmpty()) {
                            layerList.add(layerUrl);
                        }
                    }
                }
            }

            if (layerList.get(0) != null && !layerList.get(0).isEmpty()) {
                CustomSkinManager.setSkin(player, layerList);
            }
        }

    }
}
