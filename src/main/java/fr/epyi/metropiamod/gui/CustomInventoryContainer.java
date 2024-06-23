package fr.epyi.metropiamod.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;

public class CustomInventoryContainer extends Container {

    public CustomInventoryContainer(int windowId, PlayerEntity player) {
        super(null, windowId);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return false;
    }
}
