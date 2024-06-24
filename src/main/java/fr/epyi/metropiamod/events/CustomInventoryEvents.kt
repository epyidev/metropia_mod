package fr.epyi.metropiamod.events

import fr.epyi.metropiamod.MetropiaMod
import fr.epyi.metropiamod.gui.CustomInventoryContainer
import fr.epyi.metropiamod.gui.CustomInventoryGui
import fr.epyi.metropiamod.item.ModItems
import fr.epyi.metropiamod.network.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screen.inventory.InventoryScreen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.client.event.GuiScreenEvent.MouseClickedEvent
import net.minecraftforge.client.event.GuiScreenEvent.MouseReleasedEvent
import net.minecraftforge.event.entity.player.PlayerContainerEvent.Close
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber

@EventBusSubscriber(modid = MetropiaMod.MOD_ID)
object CustomInventoryEvents {
    var isLeftClickHold: Boolean = false
    var isRightClickHold: Boolean = false
    var hasInventoryOpened: Boolean = false

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    fun onGuiOpen(event: GuiOpenEvent) {
        val player: PlayerEntity? = Minecraft.getInstance().player
        if (event.gui is InventoryScreen) {
            checkNotNull(player)
            if (!player.isSpectator && !player.isCreative) {
                event.isCanceled = true

                val customContainer: CustomInventoryContainer = CustomInventoryContainer(0, player)

                Minecraft.getInstance()
                    .displayGuiScreen(CustomInventoryGui(customContainer, player.inventory, event.gui.title))

                hasInventoryOpened = true

                // Play open sounds
                player.playSound(ModSoundEvents.OPEN_BAG.get(), 1f, 1f)
                player.playSound(ModSoundEvents.OPEN_BASS.get(), 0.5f, 1f)
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    fun onGuiClose(event: Close?) {
        if (hasInventoryOpened) {
            hasInventoryOpened = false
            val player: PlayerEntity = checkNotNull(Minecraft.getInstance().player)
            // Play close sound (open sound with lower pitch)
            player.playSound(ModSoundEvents.OPEN_BAG.get(), 1f, 0.8f)
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    fun onInventoryClick(event: GuiScreenEvent.MouseDragEvent) {
        if (event.gui is CustomInventoryGui) {
            checkNotNull(Minecraft.getInstance().player)
            if ((!Minecraft.getInstance().player!!.inventory.getStackInSlot(CustomInventoryGui.hoveredSlotId).isEmpty
                        || CustomInventoryGui.hoveredSlotId > 299)
                && !CustomInventoryGui.isSplitting
            ) {
                isLeftClickHold = event.mouseButton == 0
                isRightClickHold = event.mouseButton == 1
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    fun onInventoryClickOnce(event: MouseClickedEvent) {
        if (event.gui is CustomInventoryGui) {
            if (event.button == 0 && CustomInventoryGui.isSplitting && CustomInventoryGui.splittingSlotId !== -1
            ) {
                checkNotNull(Minecraft.getInstance().player)
                if (Minecraft.getInstance().player!!.inventory.getStackInSlot(CustomInventoryGui.splittingSlotId).count - (CustomInventoryGui.spittingCount + 1) > 0) {
                    CustomInventoryGui.spittingCount = CustomInventoryGui.spittingCount + 1

                    // Play tick sound
                    Minecraft.getInstance().player!!.playSound(ModSoundEvents.TICK.get(), 0.7f, 1f)
                }
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    fun onInventoryScroll(event: GuiScreenEvent.MouseScrollEvent) {
        if (event.gui is CustomInventoryGui) {
            if (CustomInventoryGui.isSplitting
                && CustomInventoryGui.splittingSlotId !== -1
            ) {
                checkNotNull(Minecraft.getInstance().player)
                if (event.scrollDelta > 0) {
                    if (Minecraft.getInstance().player!!.inventory.getStackInSlot(CustomInventoryGui.splittingSlotId).count - (CustomInventoryGui.spittingCount + 1) > 0) {
                        CustomInventoryGui.spittingCount = CustomInventoryGui.spittingCount + 1

                        // Play tick sound
                        Minecraft.getInstance().player!!.playSound(ModSoundEvents.TICK.get(), 0.7f, 1f)
                    }
                } else {
                    if (CustomInventoryGui.spittingCount > 1) {
                        CustomInventoryGui.spittingCount = CustomInventoryGui.spittingCount - 1

                        // Play tick sound (lower pitch)
                        Minecraft.getInstance().player!!.playSound(ModSoundEvents.TICK.get(), 0.7f, 0.8f)
                    }
                }
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    fun onInventoryMouseUp(event: MouseReleasedEvent) {
        if (event.gui is CustomInventoryGui) {
            if (event.button == 0 && !CustomInventoryGui.isSplitting) {
                isLeftClickHold = false
                val player: PlayerEntity? = Minecraft.getInstance().player
                if (CustomInventoryGui.draggingSlotId !== -1 && CustomInventoryGui.draggingSlotId < 300 && CustomInventoryGui.hoveredSlotId < 300) {
                    checkNotNull(player)
                    val playerInventory = player.inventory
                    val movedItem = playerInventory.getStackInSlot(CustomInventoryGui.draggingSlotId)
                    val previousItem = playerInventory.getStackInSlot(CustomInventoryGui.hoveredSlotId)
                    if (CustomInventoryGui.draggingSlotId !== CustomInventoryGui.hoveredSlotId) {
                        if (playerInventory.getStackInSlot(CustomInventoryGui.hoveredSlotId).isEmpty) {
                            // Send packet to server
                            MetropiaMod.NETWORK.sendToServer<InventorySlotChangePacket>(
                                InventorySlotChangePacket(
                                    movedItem, ItemStack(
                                        Items.AIR
                                    ), CustomInventoryGui.hoveredSlotId, CustomInventoryGui.draggingSlotId
                                )
                            )

                            // Update clients slot to prevent item blinking
                            playerInventory.removeStackFromSlot(CustomInventoryGui.draggingSlotId)
                            playerInventory.setInventorySlotContents(CustomInventoryGui.hoveredSlotId, movedItem)

                            // Check if moved item is a cloth
                            if (movedItem.item == ItemStack(ModItems.CLOTH.get()).item) {
                                if (CustomInventoryGui.hoveredSlotId > 26 || CustomInventoryGui.draggingSlotId > 26) {
                                    MetropiaMod.NETWORK.sendToServer<ClothChange>(ClothChange)
                                }
                            }

                            // Play put sound
                            Minecraft.getInstance().player!!.playSound(ModSoundEvents.PUT.get(), 1f, 0.8f)
                        } else {
                            if (!previousItem.isEmpty
                                && (previousItem.item === movedItem.item
                                        && ItemStack.areItemStackTagsEqual(previousItem, movedItem))
                                && previousItem.isStackable
                            ) {
                                if (previousItem.count + movedItem.count <= previousItem.maxStackSize
                                    && previousItem.count + movedItem.count <= 64
                                ) {
                                    // Change moved item to addition of moved and previous items
                                    movedItem.count = movedItem.count + previousItem.count


                                    // Send packet to server
                                    MetropiaMod.NETWORK.sendToServer<InventorySlotChangePacket>(
                                        InventorySlotChangePacket(
                                            movedItem, ItemStack(
                                                Items.AIR
                                            ), CustomInventoryGui.hoveredSlotId, CustomInventoryGui.draggingSlotId
                                        )
                                    )

                                    // Update clients slot to prevent item blinking
                                    playerInventory.removeStackFromSlot(CustomInventoryGui.draggingSlotId)
                                    playerInventory.setInventorySlotContents(
                                        CustomInventoryGui.hoveredSlotId,
                                        movedItem
                                    )

                                    // Check if moved item is a cloth
                                    if (movedItem.item == ItemStack(ModItems.CLOTH.get()).item) {
                                        if (CustomInventoryGui.hoveredSlotId > 26 || CustomInventoryGui.draggingSlotId > 26) {
                                            MetropiaMod.NETWORK.sendToServer<ClothChange>(ClothChange)
                                        }
                                    }

                                    // Play put sound
                                    Minecraft.getInstance().player!!.playSound(ModSoundEvents.PUT.get(), 1f, 0.8f)
                                } else {
                                    val newPreviousItem = previousItem
                                    val newMovedItem = movedItem

                                    val countToRemove = newPreviousItem.maxStackSize - newPreviousItem.count
                                    newMovedItem.count = movedItem.count - countToRemove
                                    newPreviousItem.count = newPreviousItem.maxStackSize

                                    // Update clients slot to prevent item blinking
                                    playerInventory.setInventorySlotContents(
                                        CustomInventoryGui.hoveredSlotId,
                                        newPreviousItem
                                    )
                                    playerInventory.setInventorySlotContents(
                                        CustomInventoryGui.draggingSlotId,
                                        newMovedItem
                                    )

                                    // Send packets to server
                                    MetropiaMod.NETWORK.sendToServer<InventoryMergePacket>(
                                        InventoryMergePacket(
                                            previousItem,
                                            movedItem,
                                            CustomInventoryGui.hoveredSlotId,
                                            CustomInventoryGui.draggingSlotId
                                        )
                                    )

                                    // Check if moved item is a cloth
                                    if (movedItem.item == ItemStack(ModItems.CLOTH.get()).item) {
                                        if (CustomInventoryGui.hoveredSlotId > 26 || CustomInventoryGui.draggingSlotId > 26) {
                                            MetropiaMod.NETWORK.sendToServer<ClothChange>(ClothChange)
                                        }
                                    }

                                    // Play put sound
                                    Minecraft.getInstance().player!!.playSound(ModSoundEvents.PUT.get(), 1f, 0.8f)
                                }
                            } else {
                                // Send packets to server
                                MetropiaMod.NETWORK.sendToServer<InventorySlotChangePacket>(
                                    InventorySlotChangePacket(
                                        movedItem,
                                        previousItem,
                                        CustomInventoryGui.hoveredSlotId,
                                        CustomInventoryGui.draggingSlotId
                                    )
                                )

                                // Update clients slot to prevent item blinking
                                playerInventory.setInventorySlotContents(CustomInventoryGui.hoveredSlotId, movedItem)
                                playerInventory.setInventorySlotContents(
                                    CustomInventoryGui.draggingSlotId,
                                    previousItem
                                )

                                // Check if moved item is a cloth
                                if (movedItem.item == ItemStack(ModItems.CLOTH.get()).item) {
                                    if (CustomInventoryGui.hoveredSlotId > 26 || CustomInventoryGui.draggingSlotId > 26) {
                                        MetropiaMod.NETWORK.sendToServer<ClothChange>(ClothChange)
                                    }
                                }

                                // Play put sound
                                Minecraft.getInstance().player!!.playSound(ModSoundEvents.PUT.get(), 1f, 0.8f)
                            }
                        }
                    } else {
                        // Play put sound
                        Minecraft.getInstance().player!!.playSound(ModSoundEvents.PUT.get(), 1f, 0.8f)
                    }
                    CustomInventoryGui.draggingSlotId = -1
                    CustomInventoryGui.isDragging = false
                } else if (CustomInventoryGui.hoveredSlotId === 999
                    && CustomInventoryGui.draggingSlotId !== -1
                ) {
                    checkNotNull(player)
                    val droppedItem = player.inventory.getStackInSlot(CustomInventoryGui.draggingSlotId)
                    val playerInventory = player.inventory

                    // Update clients slot to prevent item blinking
                    playerInventory.setInventorySlotContents(CustomInventoryGui.draggingSlotId, ItemStack(Items.AIR))

                    // Send packet to the server
                    MetropiaMod.NETWORK.sendToServer<InventoryDropPacket>(
                        InventoryDropPacket(
                            droppedItem,
                            CustomInventoryGui.draggingSlotId
                        )
                    )

                    // Check if moved item is a cloth
                    if (droppedItem.item == ItemStack(ModItems.CLOTH.get()).item) {
                        if (CustomInventoryGui.hoveredSlotId > 26 || CustomInventoryGui.draggingSlotId > 26) {
                            MetropiaMod.NETWORK.sendToServer<ClothChange>(ClothChange)
                        }
                    }

                    // Play drop sound
                    player.playSound(ModSoundEvents.DROP.get(), 1f, 1f)

                    CustomInventoryGui.draggingSlotId = -1
                    CustomInventoryGui.isDragging = false
                } else {
                    CustomInventoryGui.draggingSlotId = -1
                    CustomInventoryGui.isDragging = false
                }
            } else if (event.button == 1 && CustomInventoryGui.isSplitting && CustomInventoryGui.splittingSlotId !== -1) {
                checkNotNull(Minecraft.getInstance().player)
                // Get the item stack to split
                val splittedItemStack =
                    Minecraft.getInstance().player!!.inventory.getStackInSlot(CustomInventoryGui.splittingSlotId)

                // Check if item split destination is empty
                if (Minecraft.getInstance().player!!.inventory.getStackInSlot(CustomInventoryGui.hoveredSlotId).isEmpty && CustomInventoryGui.hoveredSlotId < 300) {
                    // Update client slots to prevent item blinking
                    val newSplittedItemStack = splittedItemStack.copy()
                    newSplittedItemStack.count = splittedItemStack.count - CustomInventoryGui.spittingCount

                    val newItemStack = splittedItemStack.copy()
                    newItemStack.count = CustomInventoryGui.spittingCount

                    // Send packet to the server
                    MetropiaMod.NETWORK.sendToServer<InventorySplitPacket>(
                        InventorySplitPacket(
                            splittedItemStack,
                            CustomInventoryGui.splittingSlotId,
                            CustomInventoryGui.hoveredSlotId,
                            CustomInventoryGui.spittingCount
                        )
                    )

                    Minecraft.getInstance().player!!.inventory.setInventorySlotContents(
                        CustomInventoryGui.splittingSlotId,
                        newSplittedItemStack
                    )
                    Minecraft.getInstance().player!!.inventory.setInventorySlotContents(
                        CustomInventoryGui.hoveredSlotId,
                        newItemStack
                    )

                    // Check if moved item is a cloth
                    if (splittedItemStack.item == ItemStack(ModItems.CLOTH.get()).item) {
                        if (CustomInventoryGui.hoveredSlotId > 26 || CustomInventoryGui.draggingSlotId > 26) {
                            MetropiaMod.NETWORK.sendToServer<ClothChange>(ClothChange)
                        }
                    }

                    // Play put sound
                    Minecraft.getInstance().player!!.playSound(ModSoundEvents.PUT.get(), 1f, 0.8f)
                } else {
                    // Play error sound
                    Minecraft.getInstance().player!!.playSound(ModSoundEvents.ERROR.get(), 0.5f, 1f)
                }

                // Stop the splitting action
                CustomInventoryGui.isSplitting = false
                CustomInventoryGui.splittingSlotId = -1
                CustomInventoryGui.spittingCount = 1
                CustomInventoryGui.maxSplitSize = 1
                isRightClickHold = false
            }
        }
    }
}