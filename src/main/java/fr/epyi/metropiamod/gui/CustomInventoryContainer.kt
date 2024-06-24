package fr.epyi.metropiamod.gui

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.container.Container

class CustomInventoryContainer(windowId: Int, player: PlayerEntity?) :
    Container(null, windowId) {
    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return false
    }
}