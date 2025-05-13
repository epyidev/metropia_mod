package fr.epyi.metropiamod.events;

import fr.epyi.metropiamod.item.ClothItem;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

public class PlayerArmorHandler {
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    public PlayerArmorHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            PlayerEntity player = event.player;

            int armorPoints = 0;

            // Iterate only through slots 26 to 35
            for (int i = 27; i <= 35; i++) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (stack.getItem() instanceof ClothItem) {
                    armorPoints += Integer.parseInt(ClothItem.getClothArmor(stack)); // Get armor value from the item
                }
            }
            player.getAttribute(Attributes.ARMOR).setBaseValue(armorPoints);
        }
    }
}