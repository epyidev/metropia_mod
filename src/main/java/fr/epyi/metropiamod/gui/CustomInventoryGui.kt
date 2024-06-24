package fr.epyi.metropiamod.gui

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import fr.epyi.metropiamod.MetropiaMod
import fr.epyi.metropiamod.client.ImageDownloader
import fr.epyi.metropiamod.events.CustomInventoryEvents
import fr.epyi.metropiamod.events.ModSoundEvents
import fr.epyi.metropiamod.item.ClothItem
import fr.epyi.metropiamod.item.ModItems
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.client.gui.screen.inventory.InventoryScreen
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.texture.NativeImage
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent

class CustomInventoryGui(
    container: CustomInventoryContainer?,
    playerInventory: PlayerInventory?,
    title: ITextComponent?
) :
    ContainerScreen<CustomInventoryContainer?>(container, playerInventory, title) {
    var inventoryTitleMargins: Int = 5

    var loadingStringCounter: Int = 0
    var loadingString: String = "."

    var isDownloading: Boolean = false
    var dynamicTexture: DynamicTexture? = null

    override fun render(matrixStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        val inventoryPosX = 40
        val inventoryPosY = 40
        val inventoryWidth = 200
        val inventoryHeight = 217
        val statusPosX = 400
        val statusPosY = 40
        val statusWidth = 200
        val statusHeight = 70
        val clothPosX = 40
        val clothPosY = 270
        val clothWidth = 200
        val clothHeight = 40

        val titleHeight = 17
        val titleMargins = 5

        // Inventory background
        val isMouseInInventory =
            (mouseX >= inventoryPosX && mouseX < inventoryPosX + inventoryWidth && mouseY >= inventoryPosY && mouseY < inventoryPosY + inventoryHeight)
        val isMouseInStatus =
            (mouseX >= statusPosX && mouseX < statusPosX + statusWidth && mouseY >= statusPosY && mouseY < statusPosY + statusHeight)
        val isMouseInCloth =
            (mouseX >= clothPosX && mouseX < clothPosX + clothWidth && mouseY >= clothPosY && mouseY < clothPosY + clothHeight)
        if (!isMouseInInventory
            && !isMouseInStatus
            && !isMouseInCloth
            && Companion.isDragging
        ) {
            hoveredSlotId = 999
            fill(matrixStack, 0, 0, this.width, this.height, -0x62010000)
            font.drawString(
                matrixStack, "RELACHER POUR DROPPER",
                (this.width / 2f - font.getStringWidth("RELACHER POUR DROPPER").toFloat() / 2), 10f, -0x1
            )
        } else {
            fill(matrixStack, 0, 0, this.width, this.height, -0x63000000)
        }

        // Reset hovered slot id if out of any interface
        if (!isMouseInInventory
            && !isMouseInStatus
            && !isMouseInCloth
        ) {
            hoveredSlotId = 999
        }

        // Draw inventory window
        fill(
            matrixStack,
            inventoryPosX,
            inventoryPosY,
            inventoryPosX + inventoryWidth,
            inventoryPosY + inventoryHeight,
            -0x63000000
        )
        fill(
            matrixStack,
            inventoryPosX,
            inventoryPosY,
            inventoryPosX + inventoryWidth,
            inventoryPosY + titleHeight,
            -0x63000000
        )
        font.drawString(
            matrixStack,
            "Votre inventaire",
            (inventoryPosX + titleMargins).toFloat(),
            (inventoryPosY + titleMargins).toFloat(),
            -0x62000001
        )

        // Draw status window
        fill(matrixStack, statusPosX, statusPosY, statusPosX + statusWidth, statusPosY + statusHeight, -0x63000000)
        fill(matrixStack, statusPosX, statusPosY, statusPosX + statusWidth, statusPosY + titleHeight, -0x63000000)
        font.drawString(
            matrixStack,
            "Informations",
            (statusPosX + titleMargins).toFloat(),
            (statusPosY + titleMargins).toFloat(),
            -0x62000001
        )

        // Draw cloth window
        fill(matrixStack, clothPosX, clothPosY, clothPosX + clothWidth, clothPosY + clothHeight, -0x63000000)
        fill(matrixStack, clothPosX, clothPosY, clothPosX + clothWidth, clothPosY + titleHeight, -0x63000000)
        font.drawString(
            matrixStack,
            "Vos vetements",
            (clothPosX + titleMargins).toFloat(),
            (clothPosY + titleMargins).toFloat(),
            -0x62000001
        )

        checkNotNull(Minecraft.getInstance().player)
        font.drawString(
            matrixStack,
            "Vie",
            (statusPosX + titleMargins).toFloat(),
            (statusPosY + 20).toFloat(),
            -0x62000001
        )
        // HEALTH BG
        fill(
            matrixStack,
            statusPosX + 42,
            statusPosY + 19,
            statusPosX + statusWidth - 3,
            statusPosY + 29,
            -0x6261c3c4
        )
        // HEALTH CONTENT
        fill(
            matrixStack,
            statusPosX + 43,
            statusPosY + 20,
            (statusPosX + 43 + ((Minecraft.getInstance().player!!.health * 20 / Minecraft.getInstance().player!!.maxHealth) * ((statusPosX + statusWidth - 4) - (statusPosX + 43)) / 20)).toInt(),
            statusPosY + 28,
            -0x5a0000
        )

        font.drawString(
            matrixStack,
            "Faim",
            (statusPosX + inventoryTitleMargins).toFloat(),
            (statusPosY + 32).toFloat(),
            -0x62000001
        )
        // HUNGER BG
        fill(
            matrixStack,
            statusPosX + 42,
            statusPosY + 31,
            statusPosX + statusWidth - 3,
            statusPosY + 41,
            -0x626172be
        )
        // HUNGER CONTENT
        fill(
            matrixStack,
            statusPosX + 43,
            statusPosY + 32,
            statusPosX + 43 + (Minecraft.getInstance().player!!.foodStats.foodLevel * ((statusPosX + statusWidth - 4) - (statusPosX + 43)) / 20),
            statusPosY + 40,
            -0x3858e4
        )

        font.drawString(
            matrixStack,
            "Armure",
            (statusPosX + titleMargins).toFloat(),
            (statusPosY + 44).toFloat(),
            -0x62000001
        )
        // WATER BG
        fill(
            matrixStack,
            statusPosX + 42,
            statusPosY + 43,
            statusPosX + statusWidth - 3,
            statusPosY + 53,
            -0x6269696a
        )
        // WATER CONTENT
        fill(
            matrixStack,
            statusPosX + 43,
            statusPosY + 44,
            statusPosX + 43 + (Minecraft.getInstance().player!!.totalArmorValue * ((statusPosX + statusWidth - 4) - (statusPosX + 43)) / 20),
            statusPosY + 52,
            -0xdededf
        )

        font.drawString(
            matrixStack,
            "Air",
            (statusPosX + titleMargins).toFloat(),
            (statusPosY + 56).toFloat(),
            -0x62000001
        )
        // AIR BG
        fill(
            matrixStack,
            statusPosX + 42,
            statusPosY + 55,
            statusPosX + statusWidth - 3,
            statusPosY + 65,
            -0x62c27b48
        )
        // AIR CONTENT
        fill(
            matrixStack,
            statusPosX + 43,
            statusPosY + 56,
            (statusPosX + 43 + ((Minecraft.getInstance().player!!.air * 20 / Minecraft.getInstance().player!!.maxAir) * ((statusPosX + statusWidth - 4) - (statusPosX + 43)) / 20)),
            statusPosY + 64,
            -0xe46509
        )

        // Get the player's inventory
        val playerInventory = this.playerInventory

        // Draw slots and hovered slots of inventory
        var lineSlotID = 1
        var columnSlotId = 1
        for (i in 0..24) {
            val slotSize = (inventoryWidth / 5)
            var initX = inventoryPosX
            var initY = inventoryPosY + titleHeight

            if (columnSlotId > 1) {
                initX += ((columnSlotId - 1) * slotSize)
            }
            if (lineSlotID > 1) {
                initY += ((lineSlotID - 1) * slotSize)
            }

            if (mouseX >= initX + 1 && mouseX < initX + slotSize - 1 && mouseY >= initY + 1 && mouseY < initY + slotSize - 1) {
                if (hoveredSlotId != i) {
                    hoveredSlotId = i // Mettez à jour la case survolée
                    Minecraft.getInstance().player!!.playSound(ModSoundEvents.HOVER.get(), 0.2f, 0.8f)
                }
            }

            if (i == hoveredSlotId) {
                if (CustomInventoryEvents.isLeftClickHold) {
                    if (!Companion.isDragging) {
                        draggingSlotId = i
                        Companion.isDragging = true
                        // Play put sound
                        Minecraft.getInstance().player!!.playSound(ModSoundEvents.PUT.get(), 1f, 1f)
                    }
                    fill(matrixStack, initX + 1, initY + 1, initX + slotSize - 1, initY + slotSize - 1, -0x62ff0100)
                } else if (CustomInventoryEvents.isRightClickHold) {
                    if (!isSplitting) {
                        if (playerInventory.getStackInSlot(i).count > 1) {
                            splittingSlotId = i
                            maxSplitSize = playerInventory.getStackInSlot(i).count
                            isSplitting = true
                            spittingCount = 1

                            // Play put sound
                            Minecraft.getInstance().player!!.playSound(ModSoundEvents.PUT.get(), 1f, 1f)
                        } else {
                            CustomInventoryEvents.isRightClickHold = false
                        }
                    } else {
                        if (playerInventory.getStackInSlot(i).isEmpty) {
                            fill(
                                matrixStack,
                                initX + 1,
                                initY + 1,
                                initX + slotSize - 1,
                                initY + slotSize - 1,
                                -0x620345fd
                            )
                        } else {
                            fill(
                                matrixStack,
                                initX + 1,
                                initY + 1,
                                initX + slotSize - 1,
                                initY + slotSize - 1,
                                -0x62010000
                            )
                        }
                    }
                } else {
                    fill(matrixStack, initX + 1, initY + 1, initX + slotSize - 1, initY + slotSize - 1, 0x3F525252)
                }
            } else {
                fill(matrixStack, initX + 1, initY + 1, initX + slotSize - 1, initY + slotSize - 1, 0x3F000000)
            }

            if (columnSlotId == 5) {
                columnSlotId = 1
                lineSlotID++
            } else {
                columnSlotId++
            }
        }

        // Draw items in inventory
        lineSlotID = 1
        columnSlotId = 1
        for (i in 0..24) {
            val slotSize = (inventoryWidth / 5)
            var initX = inventoryPosX
            var initY = inventoryPosY + titleHeight

            if (columnSlotId > 1) {
                initX += ((columnSlotId - 1) * slotSize)
            }
            if (lineSlotID > 1) {
                initY += ((lineSlotID - 1) * slotSize)
            }

            val itemStack = playerInventory.getStackInSlot(i)

            val itemCount = itemStack.count

            checkNotNull(minecraft)
            if (draggingSlotId == i) {
                minecraft!!.itemRenderer.renderItemAndEffectIntoGUI(
                    itemStack,
                    (mouseX + 2) - ((slotSize - 1) / 2),
                    (mouseY + 2) - ((slotSize - 1) / 2)
                )
            } else {
                minecraft!!.itemRenderer.renderItemAndEffectIntoGUI(itemStack, initX + 2, initY + 2)
            }

            // Draw item count
            if (!itemStack.isEmpty) {
                RenderSystem.pushMatrix()
                RenderSystem.translatef(0.0f, 0.0f, 400.0f)
                if (draggingSlotId == i) {
                    font.drawStringWithShadow(
                        matrixStack,
                        itemCount.toString(),
                        mouseX + ((slotSize - 1).toFloat() / 2) - 8,
                        mouseY - ((slotSize - 1).toFloat() / 2) + 10,
                        0xFFFFFF
                    )
                } else {
                    if (!isSplitting) {
                        font.drawStringWithShadow(
                            matrixStack,
                            itemCount.toString(),
                            (initX + slotSize - 8).toFloat(),
                            (initY + 10).toFloat(),
                            0xFFFFFF
                        )
                    } else {
                        if (splittingSlotId == i) {
                            font.drawStringWithShadow(
                                matrixStack,
                                (itemCount - spittingCount).toString(),
                                (initX + slotSize - 8).toFloat(),
                                (initY + 10).toFloat(),
                                0xFFFFFF
                            )
                        } else {
                            font.drawStringWithShadow(
                                matrixStack,
                                itemCount.toString(),
                                (initX + slotSize - 8).toFloat(),
                                (initY + 10).toFloat(),
                                0xFFFFFF
                            )
                        }
                    }
                }
                RenderSystem.popMatrix()
            }
            // Calculate inventory slots
            if (columnSlotId == 5) {
                columnSlotId = 1
                lineSlotID++
            } else {
                columnSlotId++
            }
        }

        // Draw splitting item
        if (isSplitting && splittingSlotId != -1) {
            val slotSize = (inventoryWidth / 9)
            val splittingItem = playerInventory.getStackInSlot(splittingSlotId)
            minecraft!!.itemRenderer.renderItemAndEffectIntoGUI(
                splittingItem,
                (mouseX + 2) - ((slotSize - 1) / 2),
                (mouseY + 2) - ((slotSize - 1) / 2)
            )
            RenderSystem.pushMatrix()
            RenderSystem.translatef(0.0f, 0.0f, 400.0f)
            font.drawStringWithShadow(
                matrixStack,
                spittingCount.toString(),
                mouseX + ((slotSize - 1).toFloat() / 2) - 8,
                mouseY - ((slotSize - 1).toFloat() / 2) + 10,
                0xFFFFFF
            )
            RenderSystem.popMatrix()
        }

        RenderSystem.pushMatrix()
        RenderSystem.translatef(0.0f, 0.0f, 500.0f)
        // Draw item tooltip
        if (60 > hoveredSlotId && hoveredSlotId >= 0 && !Companion.isDragging && !isSplitting) {
            val hoveredItem = playerInventory.getStackInSlot(hoveredSlotId)
            if (!hoveredItem.isEmpty) {
                super.renderTooltip(matrixStack, hoveredItem, mouseX, mouseY)
                if (hoveredItem.item == ItemStack(ModItems.CLOTH.get()).item) {
                    val imageUrl: String = ClothItem.getClothUrl(hoveredItem)
                    val dirtiness: Int = ClothItem.getClothDirtiness(hoveredItem)

                    if (!imageUrl.isEmpty()) {
                        val downloadThread = Thread {
                            isDownloading = true
                            val downloadedImage = ImageDownloader.downloadImage(imageUrl)
                            if (downloadedImage != null) {
                                val nativeImage =
                                    NativeImage(downloadedImage.width, downloadedImage.height, true)
                                for (y in 0 until downloadedImage.height) {
                                    for (x in 0 until downloadedImage.width) {
                                        val rgb = downloadedImage.getRGB(x, y)
                                        val alpha = (rgb shr 24) and 0xFF
                                        val red = (rgb shr 16) and 0xFF
                                        val green = (rgb shr 8) and 0xFF
                                        val blue = (rgb) and 0xFF
                                        val abgrColor =
                                            alpha shl 24 or (blue shl 16) or (green shl 8) or red
                                        nativeImage.setPixelRGBA(x, y, abgrColor)
                                    }
                                }
                                dynamicTexture = DynamicTexture(nativeImage)
                                Minecraft.getInstance().getTextureManager().loadTexture(
                                    ResourceLocation(MetropiaMod.MOD_ID, "skins/" + imageUrl.hashCode()),
                                    dynamicTexture
                                )
                                isDownloading = false
                            }
                        }
                        val resourceLocation = ResourceLocation(MetropiaMod.MOD_ID, "skins/" + imageUrl.hashCode())
                        if (Minecraft.getInstance().getTextureManager()
                                .getTexture(resourceLocation) == null && !downloadThread.isAlive && !isDownloading
                        ) {
                            isDownloading = true
                            downloadThread.start()
                        }
                        fill(matrixStack, mouseX, mouseY - 80, mouseX + 60, mouseY - 80 + 60, -0x63000000)
                        if (!isDownloading) {
                            try {
                                Minecraft.getInstance().getTextureManager().bindTexture(resourceLocation)
                            } finally {
                                blit(matrixStack, mouseX, mouseY - 80, 0.0f, 0.0f, 60, 60, 60, 60)
                            }
                        } else {
                            loadingStringCounter++
                            if (loadingStringCounter > 4) {
                                loadingStringCounter = 0
                                loadingString = if (loadingString.length > 3) {
                                    "."
                                } else {
                                    "$loadingString."
                                }
                            }
                            font.drawString(
                                matrixStack,
                                loadingString,
                                mouseX + 28 + (4f / loadingString.length),
                                (mouseY - 55).toFloat(),
                                0xFFFFFF
                            )
                        }
                    }
                }
            }
        }
        RenderSystem.popMatrix()

        // Draw the player entity
        checkNotNull(minecraft!!.player)
        InventoryScreen.drawEntityOnScreen(
            this.width / 2,
            (this.height / 2) + 100,
            100,
            -(mouseX - (this.width / 2.0f)),
            -(mouseY - (this.height / 2.0f)),
            minecraft!!.player
        )
    }

    override fun drawGuiContainerBackgroundLayer(matrixStack: MatrixStack, partialTicks: Float, x: Int, y: Int) {
    }

    companion object {
        var draggingSlotId: Int = -1
        var isDragging: Boolean = false

        var splittingSlotId: Int = -1
        var isSplitting: Boolean = false
        var maxSplitSize: Int = 1
        var spittingCount: Int = 1

        var hoveredSlotId: Int = -1
    }
}