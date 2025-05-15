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

import static net.minecraft.client.Minecraft.getInstance;
import static net.minecraft.client.gui.screen.inventory.InventoryScreen.drawEntityOnScreen;

public class CustomInventoryGui extends ContainerScreen<CustomInventoryContainer> {

    public CustomInventoryGui(CustomInventoryContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
    }

    public static int draggingSlotId = -1;
    public static boolean isDragging = false;

    public static int splittingSlotId = -1;
    public static boolean isSplitting = false;
    public static int maxSplitSize = 1;
    public static int spittingCount = 1;

    public static int hoveredSlotId = -1;
    int inventoryTitleMargins = 5;

    int loadingStringCounter = 0;
    String loadingString = ".";

    boolean isDownloading = false;
    DynamicTexture dynamicTexture;

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int inventoryPosX = this.width - 240;
        int inventoryPosY = 115;
        int inventoryWidth = 200;
        int inventoryHeight = 83;
        int statusPosX = this.width - 240;
        int statusPosY = 40;
        int statusWidth = 200;
        int statusHeight = 70;
        int clothPosX = this.width - 240;;
        int clothPosY = 198;
        int clothWidth = 200;
        int clothHeight = 40;

        int titleHeight = 17;
        int titleMargins = 5;

        // Inventory background
        boolean isMouseInInventory = (mouseX >= inventoryPosX && mouseX < inventoryPosX + inventoryWidth && mouseY >= inventoryPosY && mouseY < inventoryPosY + inventoryHeight);
        boolean isMouseInStatus = (mouseX >= statusPosX && mouseX < statusPosX + statusWidth && mouseY >= statusPosY && mouseY < statusPosY + statusHeight);
        boolean isMouseInCloth = (mouseX >= clothPosX && mouseX < clothPosX + clothWidth && mouseY >= clothPosY && mouseY < clothPosY + clothHeight);
        if (
            !isMouseInInventory
            && !isMouseInStatus
            && !isMouseInCloth
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
            !isMouseInInventory
            && !isMouseInStatus
            && !isMouseInCloth
        ) {
            hoveredSlotId = 999;
        }

        // Draw inventory window
        fill(matrixStack, inventoryPosX, inventoryPosY, inventoryPosX + inventoryWidth - 2, inventoryPosY + inventoryHeight, 0x9d000000);
        fill(matrixStack, inventoryPosX, inventoryPosY, inventoryPosX + inventoryWidth - 2, inventoryPosY + titleHeight, 0x9d000000);
        font.drawString(matrixStack, "Votre inventaire", inventoryPosX + titleMargins, inventoryPosY + titleMargins, 0x9dFFFFFF);

        // Draw status window
        fill(matrixStack, statusPosX, statusPosY, statusPosX + statusWidth, statusPosY + statusHeight, 0x9d000000);
        fill(matrixStack, statusPosX, statusPosY, statusPosX + statusWidth, statusPosY + titleHeight, 0x9d000000);
        font.drawString(matrixStack, "Informations", statusPosX + titleMargins, statusPosY + titleMargins, 0x9dFFFFFF);

        // Draw cloth window
        fill(matrixStack, clothPosX, clothPosY, clothPosX + clothWidth - 2, clothPosY + clothHeight, 0x9d000000);
        fill(matrixStack, clothPosX, clothPosY, clothPosX + clothWidth - 2, clothPosY + titleHeight, 0x9d000000);
        font.drawString(matrixStack, "Vos vetements", clothPosX + titleMargins, clothPosY + titleMargins, 0x9dFFFFFF);

        assert Minecraft.getInstance().player != null;
        font.drawString(matrixStack, "Vie", statusPosX + titleMargins, statusPosY + 20, 0x9dFFFFFF);
        // HEALTH BG
        fill(matrixStack,
                statusPosX + 42,
                statusPosY + 19,
                statusPosX + statusWidth - 3,
                statusPosY + 29,
                0x9d9e3c3c
        );
        // HEALTH CONTENT
        fill(matrixStack,
                statusPosX + 43,
                statusPosY + 20,
                (int) (statusPosX + 43 + ((Minecraft.getInstance().player.getHealth() * 20 / Minecraft.getInstance().player.getMaxHealth()) * ((statusPosX + statusWidth - 4) - (statusPosX + 43)) / 20)),
                statusPosY + 28,
                0xffa60000
        );

        font.drawString(matrixStack, "Faim", statusPosX + inventoryTitleMargins, statusPosY + 32, 0x9dFFFFFF);
        // HUNGER BG
        fill(matrixStack,
                statusPosX + 42,
                statusPosY + 31,
                statusPosX + statusWidth - 3,
                statusPosY + 41,
                0x9d9e8d42
        );
        // HUNGER CONTENT
        fill(matrixStack,
                statusPosX + 43,
                statusPosY + 32,
                statusPosX + 43 + (Minecraft.getInstance().player.getFoodStats().getFoodLevel() * ((statusPosX + statusWidth - 4) - (statusPosX + 43)) / 20),
                statusPosY + 40,
                0xffc7a71c
        );

        font.drawString(matrixStack, "Armure", statusPosX + titleMargins, statusPosY + 44, 0x9dFFFFFF);
        // WATER BG
        fill(matrixStack,
                statusPosX + 42,
                statusPosY + 43,
                statusPosX + statusWidth - 3,
                statusPosY + 53,
                0x9d969696
        );
        Integer armorValue = Minecraft.getInstance().player.getTotalArmorValue();
        if (armorValue > 100) {
            armorValue = 100;
        }
        // WATER CONTENT
        fill(matrixStack,
                statusPosX + 43,
                statusPosY + 44,
                statusPosX + 43 + (armorValue * ((statusPosX + statusWidth - 4) - (statusPosX + 43)) / 100),
                statusPosY + 52,
                0xff212121
        );

        font.drawString(matrixStack, "Air", statusPosX + titleMargins, statusPosY + 56, 0x9dFFFFFF);
        // AIR BG
        fill(matrixStack,
                statusPosX + 42,
                statusPosY + 55,
                statusPosX + statusWidth - 3,
                statusPosY + 65,
                0x9d3d84b8
        );
        // AIR CONTENT
        fill(matrixStack,
                statusPosX + 43,
                statusPosY + 56,
                (int) (statusPosX + 43 + ((Minecraft.getInstance().player.getAir() * 20 / Minecraft.getInstance().player.getMaxAir()) * ((statusPosX + statusWidth - 4) - (statusPosX + 43)) / 20)),
                statusPosY + 64,
                0xff1b9af7
        );

        // Get the player's inventory
        PlayerInventory playerInventory = this.playerInventory;

        // Draw slots and hovered slots of inventory
        int lineSlotID = 1;
        int columnSlotId = 1;
        for (int i = 0; i <= 35; i++) {
            int slotSize = (inventoryWidth / 9);
            int initX = inventoryPosX;
            int initY = inventoryPosY + titleHeight;

            if (i > 26) {
                initX = clothPosX;
                initY = clothPosY + titleHeight;
                lineSlotID = 1;
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

            if (columnSlotId == 9) {
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
            int slotSize = (inventoryWidth / 9);
            int initX = inventoryPosX;
            int initY = inventoryPosY + titleHeight;

            if (i > 26) {
                initX = clothPosX;
                initY = clothPosY + titleHeight;
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
            if (draggingSlotId == i) {
                minecraft.getItemRenderer().renderItemAndEffectIntoGUI(itemStack, (mouseX + 2) - ((slotSize - 1) / 2), (mouseY + 2) - ((slotSize - 1) / 2));
                if  (i > 26) {
                    minecraft.getItemRenderer().renderItemAndEffectIntoGUI(new ItemStack(ModItems.EMPTY_CLOTH.get()), initX + 2, initY + 2);
                }
            } else {
                minecraft.getItemRenderer().renderItemAndEffectIntoGUI(itemStack, initX + 2, initY + 2);
                if  (i > 26) {
                    minecraft.getItemRenderer().renderItemAndEffectIntoGUI(new ItemStack(ModItems.EMPTY_CLOTH.get()), initX + 2, initY + 2);
                }
            }

            // Draw item count
            if (!itemStack.isEmpty() && itemCount > 1) {
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
            if (columnSlotId == 9) {
                columnSlotId = 1;
                lineSlotID++;
            }else {
                columnSlotId++;
            }
        }

        // Draw splitting item
        if (isSplitting && splittingSlotId != -1) {
            int slotSize = (inventoryWidth / 9);
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
