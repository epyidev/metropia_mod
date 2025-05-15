package fr.epyi.metropiamod.events;

import fr.epyi.metropiamod.MetropiaMod;
import fr.epyi.metropiamod.gui.CharacterCreatorGui;
import fr.epyi.metropiamod.gui.CustomInventoryContainer;
import fr.epyi.metropiamod.gui.CustomInventoryGui;
import fr.epyi.metropiamod.item.ModItems;
import fr.epyi.metropiamod.network.*;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

import static net.minecraft.client.Minecraft.getInstance;

@Mod.EventBusSubscriber(modid = MetropiaMod.MOD_ID)
public class CustomInventoryEvents {

    public static boolean isLeftClickHold = false;
    public static boolean isRightClickHold = false;
    public static boolean hasInventoryOpened = false;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onGuiOpen(GuiOpenEvent event) {
        PlayerEntity player = getInstance().player;
        if (event.getGui() instanceof InventoryScreen) {
            assert player != null;
            if (!player.isSpectator() && !player.isCreative()) {
                event.setCanceled(true);

                CustomInventoryContainer customContainer = new CustomInventoryContainer(0, player);

                getInstance().displayGuiScreen(new CustomInventoryGui(customContainer, player.inventory, event.getGui().getTitle()));

                hasInventoryOpened = true;

                // Play open sounds
                player.playSound(ModSoundEvents.OPEN_BAG.get(), 0.3F, 1);
                player.playSound(ModSoundEvents.OPEN_BASS.get(), 0.3F, 1);
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onGuiClose(PlayerContainerEvent.Close event) {
        if (hasInventoryOpened) {
            hasInventoryOpened = false;
            PlayerEntity player = getInstance().player;
            assert player != null;

            // Play close sound (open sound with lower pitch)
            player.playSound(ModSoundEvents.OPEN_BAG.get(), 0.3F, 0.8F);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onInventoryClick(GuiScreenEvent.MouseDragEvent event) {
        if (event.getGui() instanceof CustomInventoryGui) {
            assert getInstance().player != null;
            if (
                (!getInstance().player.inventory.getStackInSlot(CustomInventoryGui.hoveredSlotId).isEmpty()
                || CustomInventoryGui.hoveredSlotId > 299)
                && !CustomInventoryGui.isSplitting
            ) {
                isLeftClickHold = event.getMouseButton() == 0;
                isRightClickHold = event.getMouseButton() == 1;
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onInventoryClickOnce(GuiScreenEvent.MouseClickedEvent event) {
        if (event.getGui() instanceof CustomInventoryGui) {
            if (
                event.getButton() == 0
                && CustomInventoryGui.isSplitting
                && CustomInventoryGui.splittingSlotId != -1
            ) {
                assert getInstance().player != null;
                if (getInstance().player.inventory.getStackInSlot(CustomInventoryGui.splittingSlotId).getCount() - (CustomInventoryGui.spittingCount + 1) > 0) {
                    CustomInventoryGui.spittingCount = CustomInventoryGui.spittingCount + 1;

                    // Play tick sound
                    getInstance().player.playSound(ModSoundEvents.TICK.get(), 0.7F, 1F);
                }
            }
        }

        if (event.getGui() instanceof CharacterCreatorGui) {
            CharacterCreatorGui.clicked = true; // Set clicked to true
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onInventoryScroll(GuiScreenEvent.MouseScrollEvent event) {
        if (event.getGui() instanceof CustomInventoryGui) {
            if (
                CustomInventoryGui.isSplitting
                && CustomInventoryGui.splittingSlotId != -1
            ) {
                assert getInstance().player != null;
                if (event.getScrollDelta() > 0 ) {
                    if (getInstance().player.inventory.getStackInSlot(CustomInventoryGui.splittingSlotId).getCount() - (CustomInventoryGui.spittingCount + 1) > 0) {
                        CustomInventoryGui.spittingCount = CustomInventoryGui.spittingCount + 1;

                        // Play tick sound
                        getInstance().player.playSound(ModSoundEvents.TICK.get(), 0.7F, 1F);
                    }
                } else {
                    if (CustomInventoryGui.spittingCount > 1) {
                        CustomInventoryGui.spittingCount = CustomInventoryGui.spittingCount - 1;

                        // Play tick sound (lower pitch)
                        getInstance().player.playSound(ModSoundEvents.TICK.get(), 0.7F, 0.8F);
                    }
                }
            }
        }

        if (event.getGui() instanceof CharacterCreatorGui) {
            if (event.getGui() instanceof CharacterCreatorGui) {
                CharacterCreatorGui.scrollSpeed = 10; // Set initial scroll speed
                CharacterCreatorGui.scrollDelta = event.getScrollDelta();

                event.setCanceled(true); // Prevent default scrolling behavior
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onInventoryMouseUp(GuiScreenEvent.MouseReleasedEvent event) {
        if (event.getGui() instanceof CustomInventoryGui) {
            if (event.getButton() == 0 && !CustomInventoryGui.isSplitting ) {
                isLeftClickHold = false;
                PlayerEntity player = getInstance().player;
                if (CustomInventoryGui.draggingSlotId != -1 && CustomInventoryGui.draggingSlotId < 300 && CustomInventoryGui.hoveredSlotId < 300) {
                    assert player != null;
                    PlayerInventory playerInventory = player.inventory;
                    ItemStack movedItem = playerInventory.getStackInSlot(CustomInventoryGui.draggingSlotId);
                    ItemStack previousItem = playerInventory.getStackInSlot(CustomInventoryGui.hoveredSlotId);
                    if (CustomInventoryGui.draggingSlotId != CustomInventoryGui.hoveredSlotId) {
                        if (playerInventory.getStackInSlot(CustomInventoryGui.hoveredSlotId ).isEmpty()) {
                            // Send packet to server
                            MetropiaMod.NETWORK.sendToServer(new InventorySlotChangePacket(movedItem, new ItemStack(Items.AIR), CustomInventoryGui.hoveredSlotId, CustomInventoryGui.draggingSlotId));

                            // Update clients slot to prevent item blinking
                            playerInventory.removeStackFromSlot(CustomInventoryGui.draggingSlotId);
                            playerInventory.setInventorySlotContents(CustomInventoryGui.hoveredSlotId, movedItem);

                            // Check if moved item is a cloth
                            if (Objects.equals(movedItem.getItem(), new ItemStack(ModItems.CLOTH.get()).getItem())) {
                                if (CustomInventoryGui.hoveredSlotId > 26 || CustomInventoryGui.draggingSlotId > 26) {
                                    MetropiaMod.NETWORK.sendToServer(new ClothChange());
                                }
                            }

                            // Play put sound
                            getInstance().player.playSound(ModSoundEvents.PUT.get(), 1F, 0.8F);
                        } else {

                            if (
                                    !previousItem.isEmpty()
                                            && (previousItem.getItem() == movedItem.getItem()
                                            && ItemStack.areItemStackTagsEqual(previousItem, movedItem))
                                            && previousItem.isStackable()
                            ) {
                                if (
                                        previousItem.getCount() + movedItem.getCount() <= previousItem.getMaxStackSize()
                                                && previousItem.getCount() + movedItem.getCount() <= 64
                                ) {
                                    // Change moved item to addition of moved and previous items
                                    movedItem.setCount(movedItem.getCount() + previousItem.getCount());


                                    // Send packet to server
                                    MetropiaMod.NETWORK.sendToServer(new InventorySlotChangePacket(movedItem, new ItemStack(Items.AIR), CustomInventoryGui.hoveredSlotId, CustomInventoryGui.draggingSlotId));

                                    // Update clients slot to prevent item blinking
                                    playerInventory.removeStackFromSlot(CustomInventoryGui.draggingSlotId);
                                    playerInventory.setInventorySlotContents(CustomInventoryGui.hoveredSlotId, movedItem);

                                    // Check if moved item is a cloth
                                    if (Objects.equals(movedItem.getItem(), new ItemStack(ModItems.CLOTH.get()).getItem())) {
                                        if (CustomInventoryGui.hoveredSlotId > 26 || CustomInventoryGui.draggingSlotId > 26) {
                                            MetropiaMod.NETWORK.sendToServer(new ClothChange());
                                        }
                                    }

                                    // Play put sound
                                    getInstance().player.playSound(ModSoundEvents.PUT.get(), 1F, 0.8F);
                                } else {
                                    ItemStack newPreviousItem;
                                    newPreviousItem = previousItem;
                                    ItemStack newMovedItem;
                                    newMovedItem = movedItem;

                                    int countToRemove = newPreviousItem.getMaxStackSize() - newPreviousItem.getCount();
                                    newMovedItem.setCount(movedItem.getCount() - countToRemove);
                                    newPreviousItem.setCount(newPreviousItem.getMaxStackSize());

                                    // Update clients slot to prevent item blinking
                                    playerInventory.setInventorySlotContents(CustomInventoryGui.hoveredSlotId, newPreviousItem);
                                    playerInventory.setInventorySlotContents(CustomInventoryGui.draggingSlotId, newMovedItem);

                                    // Send packets to server
                                    MetropiaMod.NETWORK.sendToServer(new InventoryMergePacket(previousItem, movedItem, CustomInventoryGui.hoveredSlotId, CustomInventoryGui.draggingSlotId));

                                    // Check if moved item is a cloth
                                    if (Objects.equals(movedItem.getItem(), new ItemStack(ModItems.CLOTH.get()).getItem())) {
                                        if (CustomInventoryGui.hoveredSlotId > 26 || CustomInventoryGui.draggingSlotId > 26) {
                                            MetropiaMod.NETWORK.sendToServer(new ClothChange());
                                        }
                                    }

                                    // Play put sound
                                    getInstance().player.playSound(ModSoundEvents.PUT.get(), 1F, 0.8F);
                                }
                            } else {
                                // Send packets to server
                                MetropiaMod.NETWORK.sendToServer(new InventorySlotChangePacket(movedItem, previousItem, CustomInventoryGui.hoveredSlotId, CustomInventoryGui.draggingSlotId));

                                // Update clients slot to prevent item blinking
                                playerInventory.setInventorySlotContents(CustomInventoryGui.hoveredSlotId, movedItem);
                                playerInventory.setInventorySlotContents(CustomInventoryGui.draggingSlotId, previousItem);

                                // Check if moved item is a cloth
                                if (Objects.equals(movedItem.getItem(), new ItemStack(ModItems.CLOTH.get()).getItem())) {
                                    if (CustomInventoryGui.hoveredSlotId > 26 || CustomInventoryGui.draggingSlotId > 26) {
                                        MetropiaMod.NETWORK.sendToServer(new ClothChange());
                                    }
                                }

                                // Play put sound
                                getInstance().player.playSound(ModSoundEvents.PUT.get(), 1F, 0.8F);
                            }
                        }
                    } else {
                        // Play put sound
                        getInstance().player.playSound(ModSoundEvents.PUT.get(), 1F, 0.8F);
                    }
                    CustomInventoryGui.draggingSlotId = -1;
                    CustomInventoryGui.isDragging = false;
                } else if (
                        CustomInventoryGui.hoveredSlotId == 999
                        && CustomInventoryGui.draggingSlotId != -1
                ) {

                    assert player != null;
                    ItemStack droppedItem = player.inventory.getStackInSlot(CustomInventoryGui.draggingSlotId);
                    PlayerInventory playerInventory = player.inventory;

                    // Update clients slot to prevent item blinking
                    playerInventory.setInventorySlotContents(CustomInventoryGui.draggingSlotId, new ItemStack(Items.AIR));

                    // Send packet to the server
                    MetropiaMod.NETWORK.sendToServer(new InventoryDropPacket(droppedItem, CustomInventoryGui.draggingSlotId));

                    // Check if moved item is a cloth
                    if (Objects.equals(droppedItem.getItem(), new ItemStack(ModItems.CLOTH.get()).getItem())) {
                        if (CustomInventoryGui.hoveredSlotId > 26 || CustomInventoryGui.draggingSlotId > 26) {
                            MetropiaMod.NETWORK.sendToServer(new ClothChange());
                        }
                    }

                    // Play drop sound
                    player.playSound(ModSoundEvents.DROP.get(), 1, 1);

                    CustomInventoryGui.draggingSlotId = -1;
                    CustomInventoryGui.isDragging = false;

                } else {
                    CustomInventoryGui.draggingSlotId = -1;
                    CustomInventoryGui.isDragging = false;
                }
            } else if (event.getButton() == 1 && CustomInventoryGui.isSplitting && CustomInventoryGui.splittingSlotId != -1) {
                assert getInstance().player != null;
                // Get the item stack to split
                ItemStack splittedItemStack = getInstance().player.inventory.getStackInSlot(CustomInventoryGui.splittingSlotId);

                // Check if item split destination is empty
                if (getInstance().player.inventory.getStackInSlot(CustomInventoryGui.hoveredSlotId).isEmpty() && CustomInventoryGui.hoveredSlotId < 300) {
                    // Update client slots to prevent item blinking
                    ItemStack newSplittedItemStack = splittedItemStack.copy();
                    newSplittedItemStack.setCount(splittedItemStack.getCount() - CustomInventoryGui.spittingCount);

                    ItemStack newItemStack = splittedItemStack.copy();
                    newItemStack.setCount(CustomInventoryGui.spittingCount);

                    // Send packet to the server
                    MetropiaMod.NETWORK.sendToServer(new InventorySplitPacket(splittedItemStack, CustomInventoryGui.splittingSlotId, CustomInventoryGui.hoveredSlotId, CustomInventoryGui.spittingCount));

                    getInstance().player.inventory.setInventorySlotContents(CustomInventoryGui.splittingSlotId, newSplittedItemStack);
                    getInstance().player.inventory.setInventorySlotContents(CustomInventoryGui.hoveredSlotId, newItemStack);

                    // Check if moved item is a cloth
                    if (Objects.equals(splittedItemStack.getItem(), new ItemStack(ModItems.CLOTH.get()).getItem())) {
                        if (CustomInventoryGui.hoveredSlotId > 26 || CustomInventoryGui.draggingSlotId > 26) {
                            MetropiaMod.NETWORK.sendToServer(new ClothChange());
                        }
                    }

                    // Play put sound
                    getInstance().player.playSound(ModSoundEvents.PUT.get(), 1F, 0.8F);
                } else {
                    // Play error sound
                    getInstance().player.playSound(ModSoundEvents.ERROR.get(), 0.5F, 1);
                }

                // Stop the splitting action
                CustomInventoryGui.isSplitting = false;
                CustomInventoryGui.splittingSlotId = -1;
                CustomInventoryGui.spittingCount = 1;
                CustomInventoryGui.maxSplitSize = 1;
                isRightClickHold = false;

            }
        }
    }
}
