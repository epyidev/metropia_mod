package fr.epyi.metropiamod.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;

public class CharacterCreatorContainer extends Container {

    public CharacterCreatorContainer(int windowId, PlayerEntity player) {
        super(null, windowId);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return false;
    }

}