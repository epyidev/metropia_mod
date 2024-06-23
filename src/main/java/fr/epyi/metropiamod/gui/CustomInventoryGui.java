package fr.epyi.metropiamod.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import fr.epyi.metropiamod.MetropiaMod;
import fr.epyi.metropiamod.events.CustomInventoryEvents;
import fr.epyi.metropiamod.events.ModSoundEvents;
import fr.epyi.metropiamod.item.ClothItem;
import fr.epyi.metropiamod.item.ModItems;
import fr.epyi.metropiamod.client.ImageDownloader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.minecraft.client.Minecraft.getInstance;
import static net.minecraft.client.gui.screen.inventory.InventoryScreen.drawEntityOnScreen;

public class CustomInventoryGui extends ContainerScreen<CustomInventoryContainer> {

    public CustomInventoryGui(CustomInventoryContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
    }

    // USAGES OF DRAGGING AND HOVERED SLOT ID
    // -1 : Nothing
    // 0 to 299 : Only for minecraft slots
    // 300 : Minecraft survival inventory
    // 999 : Drop area
    public static int draggingSlotId = -1;
    public static boolean isDragging = false;

    public static int splittingSlotId = -1;
    public static boolean isSplitting = false;
    public static int maxSplitSize = 1;
    public static int spittingCount = 1;

    public static int hoveredSlotId = -1;
    public static int InventorySelectionMouseOffsetX = 0;
    public static int InventorySelectionMouseOffsetY = 0;
    int inventoryTitleMargins = 5;

    int loadingStringCounter = 0;
    String loadingString = ".";

    boolean isDownloading = false;
    DynamicTexture dynamicTexture;

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

        // Dessinez un fond noir
        if (
            !(mouseX >= Integer.parseInt(MetropiaMod.interfaces[0][3]) && mouseX < Integer.parseInt(MetropiaMod.interfaces[0][3]) + Integer.parseInt(MetropiaMod.interfaces[0][1]) && mouseY >= Integer.parseInt(MetropiaMod.interfaces[0][4]) && mouseY < Integer.parseInt(MetropiaMod.interfaces[0][4]) + Integer.parseInt(MetropiaMod.interfaces[0][2]))
            && !(mouseX >= Integer.parseInt(MetropiaMod.interfaces[1][3]) && mouseX < Integer.parseInt(MetropiaMod.interfaces[1][3]) + Integer.parseInt(MetropiaMod.interfaces[1][1]) && mouseY >= Integer.parseInt(MetropiaMod.interfaces[1][4]) && mouseY < Integer.parseInt(MetropiaMod.interfaces[1][4]) + Integer.parseInt(MetropiaMod.interfaces[1][2]))
            && !(mouseX >= Integer.parseInt(MetropiaMod.interfaces[2][3]) && mouseX < Integer.parseInt(MetropiaMod.interfaces[2][3]) + Integer.parseInt(MetropiaMod.interfaces[2][1]) && mouseY >= Integer.parseInt(MetropiaMod.interfaces[2][4]) && mouseY < Integer.parseInt(MetropiaMod.interfaces[2][4]) + Integer.parseInt(MetropiaMod.interfaces[2][2]))
            && isDragging
        ) {
            hoveredSlotId = 999;
            fill(matrixStack, 0, 0, this.width, this.height, 0x9dFF0000);
            font.drawString(matrixStack, "RELACHER POUR DROPPER", (this.width / 2f - (float) font.getStringWidth("RELACHER POUR DROPPER") / 2), 10, 0xFFFFFFFF);
        } else {
            fill(matrixStack, 0, 0, this.width, this.height, 0x9d000000);
        }

        // Reset hovered slot id if out of any interface
        if (
            !(mouseX >= Integer.parseInt(MetropiaMod.interfaces[0][3]) && mouseX < Integer.parseInt(MetropiaMod.interfaces[0][3]) + Integer.parseInt(MetropiaMod.interfaces[0][1]) && mouseY >= Integer.parseInt(MetropiaMod.interfaces[0][4]) && mouseY < Integer.parseInt(MetropiaMod.interfaces[0][4]) + Integer.parseInt(MetropiaMod.interfaces[0][2]))
            && !(mouseX >= Integer.parseInt(MetropiaMod.interfaces[1][3]) && mouseX < Integer.parseInt(MetropiaMod.interfaces[1][3]) + Integer.parseInt(MetropiaMod.interfaces[1][1]) && mouseY >= Integer.parseInt(MetropiaMod.interfaces[1][4]) && mouseY < Integer.parseInt(MetropiaMod.interfaces[1][4]) + Integer.parseInt(MetropiaMod.interfaces[1][2]))
            && !(mouseX >= Integer.parseInt(MetropiaMod.interfaces[2][3]) && mouseX < Integer.parseInt(MetropiaMod.interfaces[2][3]) + Integer.parseInt(MetropiaMod.interfaces[2][1]) && mouseY >= Integer.parseInt(MetropiaMod.interfaces[2][4]) && mouseY < Integer.parseInt(MetropiaMod.interfaces[2][4]) + Integer.parseInt(MetropiaMod.interfaces[2][2]))
        ) {
            hoveredSlotId = 999;
        }

//        MetropiaMod.interfaces[0][3] = String.valueOf(this.width - (180 + 10));
//        MetropiaMod.interfaces[1][3] = String.valueOf(this.width - (180 + 10));
//        MetropiaMod.interfaces[2][3] = String.valueOf(this.width - (180 + 10));

        // Draw inventory window
        fill(matrixStack, Integer.parseInt(MetropiaMod.interfaces[0][3]), Integer.parseInt(MetropiaMod.interfaces[0][4]), Integer.parseInt(MetropiaMod.interfaces[0][3]) + Integer.parseInt(MetropiaMod.interfaces[0][1]), Integer.parseInt(MetropiaMod.interfaces[0][4]) + Integer.parseInt(MetropiaMod.interfaces[0][2]), 0x9d000000);
        fill(matrixStack, Integer.parseInt(MetropiaMod.interfaces[0][3]), Integer.parseInt(MetropiaMod.interfaces[0][4]), Integer.parseInt(MetropiaMod.interfaces[0][3]) + Integer.parseInt(MetropiaMod.interfaces[0][1]), Integer.parseInt(MetropiaMod.interfaces[0][4]) + 17, 0x9d000000);
        font.drawString(matrixStack, "Votre inventaire", Integer.parseInt(MetropiaMod.interfaces[0][3]) + inventoryTitleMargins, Integer.parseInt(MetropiaMod.interfaces[0][4]) + inventoryTitleMargins, 0x9dFFFFFF);

        // Draw status window
        fill(matrixStack, Integer.parseInt(MetropiaMod.interfaces[1][3]), Integer.parseInt(MetropiaMod.interfaces[1][4]), Integer.parseInt(MetropiaMod.interfaces[1][3]) + Integer.parseInt(MetropiaMod.interfaces[1][1]), Integer.parseInt(MetropiaMod.interfaces[1][4]) + Integer.parseInt(MetropiaMod.interfaces[1][2]), 0x9d000000);
        fill(matrixStack, Integer.parseInt(MetropiaMod.interfaces[1][3]), Integer.parseInt(MetropiaMod.interfaces[1][4]), Integer.parseInt(MetropiaMod.interfaces[1][3]) + Integer.parseInt(MetropiaMod.interfaces[1][1]), Integer.parseInt(MetropiaMod.interfaces[1][4]) + 17, 0x9d000000);
        font.drawString(matrixStack, "Informations", Integer.parseInt(MetropiaMod.interfaces[1][3]) + inventoryTitleMargins, Integer.parseInt(MetropiaMod.interfaces[1][4]) + inventoryTitleMargins, 0x9dFFFFFF);

        // Draw cloth window
        fill(matrixStack, Integer.parseInt(MetropiaMod.interfaces[2][3]), Integer.parseInt(MetropiaMod.interfaces[2][4]), Integer.parseInt(MetropiaMod.interfaces[2][3]) + Integer.parseInt(MetropiaMod.interfaces[2][1]), Integer.parseInt(MetropiaMod.interfaces[2][4]) + Integer.parseInt(MetropiaMod.interfaces[2][2]), 0x9d000000);
        fill(matrixStack, Integer.parseInt(MetropiaMod.interfaces[2][3]), Integer.parseInt(MetropiaMod.interfaces[2][4]), Integer.parseInt(MetropiaMod.interfaces[2][3]) + Integer.parseInt(MetropiaMod.interfaces[2][1]), Integer.parseInt(MetropiaMod.interfaces[2][4]) + 17, 0x9d000000);
        font.drawString(matrixStack, "Vos vetements", Integer.parseInt(MetropiaMod.interfaces[2][3]) + inventoryTitleMargins, Integer.parseInt(MetropiaMod.interfaces[2][4]) + inventoryTitleMargins, 0x9dFFFFFF);

        assert Minecraft.getInstance().player != null;
        font.drawString(matrixStack, "Vie", Integer.parseInt(MetropiaMod.interfaces[1][3]) + inventoryTitleMargins, Integer.parseInt(MetropiaMod.interfaces[1][4]) + 20, 0x9dFFFFFF);
        // HEALTH BG
        fill(matrixStack,
                Integer.parseInt(MetropiaMod.interfaces[1][3]) + 42,
                Integer.parseInt(MetropiaMod.interfaces[1][4]) + 19,
                Integer.parseInt(MetropiaMod.interfaces[1][3]) + Integer.parseInt(MetropiaMod.interfaces[1][1]) - 3,
                Integer.parseInt(MetropiaMod.interfaces[1][4]) + 29,
                0x9d9e3c3c
        );
        // HEALTH CONTENT
        fill(matrixStack,
                Integer.parseInt(MetropiaMod.interfaces[1][3]) + 43,
                Integer.parseInt(MetropiaMod.interfaces[1][4]) + 20,
                (int) (Integer.parseInt(MetropiaMod.interfaces[1][3]) + 43 + ((Minecraft.getInstance().player.getHealth() * 20 / Minecraft.getInstance().player.getMaxHealth()) * ((Integer.parseInt(MetropiaMod.interfaces[1][3]) + Integer.parseInt(MetropiaMod.interfaces[1][1]) - 4) - (Integer.parseInt(MetropiaMod.interfaces[1][3]) + 43)) / 20)),
                Integer.parseInt(MetropiaMod.interfaces[1][4]) + 28,
                0xffa60000
        );

        font.drawString(matrixStack, "Faim", Integer.parseInt(MetropiaMod.interfaces[1][3]) + inventoryTitleMargins, Integer.parseInt(MetropiaMod.interfaces[1][4]) + 32, 0x9dFFFFFF);
        // HUNGER BG
        fill(matrixStack,
                Integer.parseInt(MetropiaMod.interfaces[1][3]) + 42,
                Integer.parseInt(MetropiaMod.interfaces[1][4]) + 31,
                Integer.parseInt(MetropiaMod.interfaces[1][3]) + Integer.parseInt(MetropiaMod.interfaces[1][1]) - 3,
                Integer.parseInt(MetropiaMod.interfaces[1][4]) + 41,
                0x9d9e8d42
        );
        // HUNGER CONTENT
        fill(matrixStack,
                Integer.parseInt(MetropiaMod.interfaces[1][3]) + 43,
                Integer.parseInt(MetropiaMod.interfaces[1][4]) + 32,
                Integer.parseInt(MetropiaMod.interfaces[1][3]) + 43 + (Minecraft.getInstance().player.getFoodStats().getFoodLevel() * ((Integer.parseInt(MetropiaMod.interfaces[1][3]) + Integer.parseInt(MetropiaMod.interfaces[1][1]) - 4) - (Integer.parseInt(MetropiaMod.interfaces[1][3]) + 43)) / 20),
                Integer.parseInt(MetropiaMod.interfaces[1][4]) + 40,
                0xffc7a71c
        );

        font.drawString(matrixStack, "Armure", Integer.parseInt(MetropiaMod.interfaces[1][3]) + inventoryTitleMargins, Integer.parseInt(MetropiaMod.interfaces[1][4]) + 44, 0x9dFFFFFF);
        // WATER BG
        fill(matrixStack,
                Integer.parseInt(MetropiaMod.interfaces[1][3]) + 42,
                Integer.parseInt(MetropiaMod.interfaces[1][4]) + 43,
                Integer.parseInt(MetropiaMod.interfaces[1][3]) + Integer.parseInt(MetropiaMod.interfaces[1][1]) - 3,
                Integer.parseInt(MetropiaMod.interfaces[1][4]) + 53,
                0x9d969696
        );
        // WATER CONTENT
        fill(matrixStack,
                Integer.parseInt(MetropiaMod.interfaces[1][3]) + 43,
                Integer.parseInt(MetropiaMod.interfaces[1][4]) + 44,
                Integer.parseInt(MetropiaMod.interfaces[1][3]) + 43 + (Minecraft.getInstance().player.getTotalArmorValue() * ((Integer.parseInt(MetropiaMod.interfaces[1][3]) + Integer.parseInt(MetropiaMod.interfaces[1][1]) - 4) - (Integer.parseInt(MetropiaMod.interfaces[1][3]) + 43)) / 20),
                Integer.parseInt(MetropiaMod.interfaces[1][4]) + 52,
                0xff212121
        );

        font.drawString(matrixStack, "Air", Integer.parseInt(MetropiaMod.interfaces[1][3]) + inventoryTitleMargins, Integer.parseInt(MetropiaMod.interfaces[1][4]) + 56, 0x9dFFFFFF);
        // AIR BG
        fill(matrixStack,
                Integer.parseInt(MetropiaMod.interfaces[1][3]) + 42,
                Integer.parseInt(MetropiaMod.interfaces[1][4]) + 55,
                Integer.parseInt(MetropiaMod.interfaces[1][3]) + Integer.parseInt(MetropiaMod.interfaces[1][1]) - 3,
                Integer.parseInt(MetropiaMod.interfaces[1][4]) + 65,
                0x9d3d84b8
        );
        // AIR CONTENT
        fill(matrixStack,
                Integer.parseInt(MetropiaMod.interfaces[1][3]) + 43,
                Integer.parseInt(MetropiaMod.interfaces[1][4]) + 56,
                (int) (Integer.parseInt(MetropiaMod.interfaces[1][3]) + 43 + ((Minecraft.getInstance().player.getAir() * 20 / Minecraft.getInstance().player.getMaxAir()) * ((Integer.parseInt(MetropiaMod.interfaces[1][3]) + Integer.parseInt(MetropiaMod.interfaces[1][1]) - 4) - (Integer.parseInt(MetropiaMod.interfaces[1][3]) + 43)) / 20)),
                Integer.parseInt(MetropiaMod.interfaces[1][4]) + 64,
                0xff1b9af7
        );

        // Get the player's inventory
        PlayerInventory playerInventory = this.playerInventory;

        // Draw slots and hovered slots of inventory
        int lineSlotID = 1;
        int columnSlotId = 1;
        for (int i = 0; i <= 35; i++) {
            int slotSize = (Integer.parseInt(MetropiaMod.interfaces[0][1]) / 5);
            int initX = Integer.parseInt(MetropiaMod.interfaces[0][3]);
            int initY = Integer.parseInt(MetropiaMod.interfaces[0][4]) + 17;

            if (i == 24) {
                i = 26;
            }

            if (i > 24) {
                slotSize = (Integer.parseInt(MetropiaMod.interfaces[2][1]) / 9);
                initX = Integer.parseInt(MetropiaMod.interfaces[2][3]);
                initY = Integer.parseInt(MetropiaMod.interfaces[2][4]) + 17;
                lineSlotID = 1;;
            }

            if (columnSlotId > 1) {
                initX += ((columnSlotId - 1) * slotSize);
            }
            if (lineSlotID > 1) {
                initY += ((lineSlotID - 1) * slotSize);
            }

            if (mouseX >= initX + 1 && mouseX < initX + slotSize - 1 && mouseY >= initY + 1 && mouseY < initY + slotSize - 1) {
                if (hoveredSlotId != i) {
                    hoveredSlotId = i; // Mettez à jour la case survolée
                    Minecraft.getInstance().player.playSound(ModSoundEvents.HOVER.get(), 0.2F, 0.8F);
                }
            }

            if (i == hoveredSlotId) {

                if (CustomInventoryEvents.isLeftClickHold) {
                    if (!isDragging) {
                        draggingSlotId = i;
                        isDragging = true;
                        // Play put sound
                        getInstance().player.playSound(ModSoundEvents.PUT.get(), 1F, 1F);
                    }
                    fill(matrixStack, initX + 1, initY + 1, initX + slotSize - 1, initY + slotSize - 1, 0x9d00ff00);
                } else if (CustomInventoryEvents.isRightClickHold) {
                    if (!isSplitting) {
                        if (playerInventory.getStackInSlot(i).getCount() > 1) {
                            splittingSlotId = i;
                            maxSplitSize = playerInventory.getStackInSlot(i).getCount();
                            isSplitting = true;
                            spittingCount = 1;

                            // Play put sound
                            getInstance().player.playSound(ModSoundEvents.PUT.get(), 1F, 1F);
                        } else {
                            CustomInventoryEvents.isRightClickHold = false;
                        }
                    } else {
                        if (playerInventory.getStackInSlot(i).isEmpty()) {
                            fill(matrixStack, initX + 1, initY + 1, initX + slotSize - 1, initY + slotSize - 1, 0x9dfcba03);
                        } else {
                            fill(matrixStack, initX + 1, initY + 1, initX + slotSize - 1, initY + slotSize - 1, 0x9dFF0000);
                        }
                    }
                } else {
                    fill(matrixStack, initX + 1, initY + 1, initX + slotSize - 1, initY + slotSize - 1, 0x3F525252);
                }
            } else {
                fill(matrixStack, initX + 1, initY + 1, initX + slotSize - 1, initY + slotSize - 1, 0x3F000000);
            }

            if (i > 24 ? (columnSlotId == 9) : (columnSlotId == 5)) {
                columnSlotId = 1;
                lineSlotID++;
            }else {
                columnSlotId++;
            }
        }

        // Draw items in inventory
        lineSlotID = 1;
        columnSlotId = 1;
        for (int i = 0; i <= 35; i++) {
            int slotSize = (Integer.parseInt(MetropiaMod.interfaces[0][1]) / 5);
            int initX = Integer.parseInt(MetropiaMod.interfaces[0][3]);
            int initY = Integer.parseInt(MetropiaMod.interfaces[0][4]) + 17;

            if (i > 24) {
                slotSize = (Integer.parseInt(MetropiaMod.interfaces[0][1]) / 9);
                initX = Integer.parseInt(MetropiaMod.interfaces[2][3]);
                initY = Integer.parseInt(MetropiaMod.interfaces[2][4]) + 17;
                lineSlotID = 1;
            }

            if (columnSlotId > 1) {
                initX += ((columnSlotId - 1) * slotSize);
            }
            if (lineSlotID > 1) {
                initY += ((lineSlotID - 1) * slotSize);
            }


            ItemStack itemStack = playerInventory.getStackInSlot(i);

            int itemCount = itemStack.getCount();

            assert minecraft != null;
            // Dessinez l'icône de l'objet
            if (draggingSlotId == i) {
                minecraft.getItemRenderer().renderItemAndEffectIntoGUI(itemStack, (mouseX + 2) - ((slotSize - 1) / 2), (mouseY + 2) - ((slotSize - 1) / 2));
                if (i > 24) {
                    minecraft.getItemRenderer().renderItemAndEffectIntoGUI(new ItemStack(ModItems.EMPTY_CLOTH.get()), initX + 2, initY + 2);
                }
            } else {
                minecraft.getItemRenderer().renderItemAndEffectIntoGUI(itemStack, initX + 2, initY + 2);
                if (i > 24 && itemStack.isEmpty()) {
                    minecraft.getItemRenderer().renderItemAndEffectIntoGUI(new ItemStack(ModItems.EMPTY_CLOTH.get()), initX + 2, initY + 2);
                }
            }

            // Draw item count
            if (!itemStack.isEmpty()) {

                RenderSystem.pushMatrix();
                RenderSystem.translatef(0.0F, 0.0F, 400.0F);

                if (draggingSlotId == i) {
                    font.drawStringWithShadow(matrixStack, Integer.toString(itemCount), mouseX + ((float) (slotSize - 1) / 2) - 8, mouseY - ((float) (slotSize - 1) / 2) + 10, 0xFFFFFF);
                } else {
                    if (!isSplitting) {
                        font.drawStringWithShadow(matrixStack, Integer.toString(itemCount), initX + slotSize - 8, initY + 10, 0xFFFFFF);
                    } else {
                        if (splittingSlotId == i) {
                            font.drawStringWithShadow(matrixStack, Integer.toString(itemCount - spittingCount), initX + slotSize - 8, initY + 10, 0xFFFFFF);
                        } else {
                            font.drawStringWithShadow(matrixStack, Integer.toString(itemCount), initX + slotSize - 8, initY + 10, 0xFFFFFF);
                        }
                    }

                }

                RenderSystem.popMatrix();

            }

            // Calculate inventory slots
            if (i > 24 ? (columnSlotId == 9) : (columnSlotId == 5)) {
                columnSlotId = 1;
                lineSlotID++;
            }else {
                columnSlotId++;
            }
        }

        // Draw splitting item
        if (isSplitting && splittingSlotId != -1) {
            int slotSize = (Integer.parseInt(MetropiaMod.interfaces[0][1]) / 9);
            ItemStack splittingItem = playerInventory.getStackInSlot(splittingSlotId);
            minecraft.getItemRenderer().renderItemAndEffectIntoGUI(splittingItem, (mouseX + 2) - ((slotSize - 1) / 2), (mouseY + 2) - ((slotSize - 1) / 2));
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0.0F, 0.0F, 400.0F);
            font.drawStringWithShadow(matrixStack, Integer.toString(spittingCount), mouseX + ((float) (slotSize - 1) / 2) - 8, mouseY - ((float) (slotSize - 1) / 2) + 10, 0xFFFFFF);
            RenderSystem.popMatrix();
        }

        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0F, 0.0F, 500.0F);
        // Draw item tooltip
        if (60 > hoveredSlotId && hoveredSlotId >= 0 && !isDragging && !isSplitting) {
            ItemStack hoveredItem = playerInventory.getStackInSlot(hoveredSlotId);
            if (!hoveredItem.isEmpty()) {
                super.renderTooltip(matrixStack, hoveredItem, mouseX, mouseY);
                if (Objects.equals(hoveredItem.getItem(), new ItemStack(ModItems.CLOTH.get()).getItem())) {
                    String imageUrl = ClothItem.getClothUrl(hoveredItem);
                    int dirtiness = ClothItem.getClothDirtiness(hoveredItem);

                    if (!imageUrl.isEmpty()) {

                        Thread downloadThread = new Thread(() -> {
                            isDownloading = true;
                            BufferedImage downloadedImage = ImageDownloader.downloadImage(imageUrl);

                            if (downloadedImage != null) {
                                NativeImage nativeImage = new NativeImage(downloadedImage.getWidth(), downloadedImage.getHeight(), true);

                                for (int y = 0; y < downloadedImage.getHeight(); y++) {
                                    for (int x = 0; x < downloadedImage.getWidth(); x++) {
                                        int rgb = downloadedImage.getRGB(x, y);
                                        int alpha = (rgb >> 24) & 0xFF;
                                        int red = (rgb >> 16) & 0xFF;
                                        int green = (rgb >> 8) & 0xFF;
                                        int blue = (rgb) & 0xFF;

                                        int abgrColor = (alpha << 24) | (blue << 16) | (green << 8) | red;

                                        nativeImage.setPixelRGBA(x, y, abgrColor);
                                    }
                                }

                                dynamicTexture = new DynamicTexture(nativeImage);
                                Minecraft.getInstance().getTextureManager().loadTexture(new ResourceLocation(MetropiaMod.MOD_ID, "skins/" + imageUrl.hashCode()), dynamicTexture);


                                isDownloading = false;
                            }
                        });


                        ResourceLocation resourceLocation = new ResourceLocation(MetropiaMod.MOD_ID, "skins/" + imageUrl.hashCode());

                        if (Minecraft.getInstance().getTextureManager().getTexture(resourceLocation) == null && !downloadThread.isAlive() && !isDownloading) {
                            isDownloading = true;
                            downloadThread.start();
                        }

                        int borderThickness = 2;
                        int borderColor = 0xFFFFFF;

                        fill(matrixStack, mouseX, mouseY - 80, mouseX + 60, mouseY - 80 + 60, 0x9d000000);

                        if (!isDownloading) {
                            try {
                                Minecraft.getInstance().getTextureManager().bindTexture(resourceLocation);
                            } finally {
                                blit(matrixStack, mouseX, mouseY - 80, 0.0F, 0.0F, 60, 60, 60, 60);
                            }
                        } else {
                            loadingStringCounter++;
                            if (loadingStringCounter > 4) {
                                loadingStringCounter = 0;
                                if (loadingString.length() > 3) {
                                    loadingString = ".";
                                } else {
                                    loadingString = loadingString + ".";
                                }
                            }

                            font.drawString(matrixStack, loadingString, mouseX + 28 + ((float) 4 / loadingString.length() ), mouseY - 55, 0xFFFFFF);
                        }




                    }
                }
            }
        }
        RenderSystem.popMatrix();

        // Draw the player entity
        assert this.minecraft.player != null;
        drawEntityOnScreen(this.width / 2, (this.height / 2) + 100, 100, -(mouseX - (this.width / 2.0F)), -(mouseY - (this.height / 2.0F)), this.minecraft.player);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {

    }

}
