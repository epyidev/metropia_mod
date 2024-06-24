package fr.epyi.metropiamod.client

import com.google.common.collect.Maps
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import fr.epyi.metropiamod.MetropiaMod
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.player.AbstractClientPlayerEntity
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.texture.NativeImage
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.util.ResourceLocation
import java.awt.image.BufferedImage
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

object ClientSkinManager {
    private var textureManager: TextureManager? = null

    private val missing = ClientSkinData(ResourceLocation("textures/entity/alex.png"), "default")

    private val playerSkinMap: MutableMap<UUID, ClientSkinData?> = Maps.newHashMap()

    private val originalSkinMap: MutableMap<UUID, ClientSkinData> = Maps.newHashMap()

    private val cachedUrls: MutableMap<String, ResourceLocation> = Maps.newHashMap()

    private val texturesToLoad: MutableList<SkinLoadJob> = ArrayList()

    fun getTextureManager() {
        textureManager = Minecraft.getInstance().getTextureManager()
    }

    fun clearSkinCache() {
        // Release all cached resources and clear the cache list
        for (resource in cachedUrls.values) {
            textureManager!!.deleteTexture(resource)
        }
        cachedUrls.clear()

        for ((key) in playerSkinMap) {
            val skinData = originalSkinMap.getOrDefault(
                key, missing
            )

            playerSkinMap[key] = skinData
        }
    }

    fun cleanupSkinData() {
        playerSkinMap.clear()
        originalSkinMap.clear()
    }

    /**
     * Change the model of the target player.
     *
     * @param uuid the uuid of the player
     * @param urls url list of the skin
     * @param bodyType the body type of the player
     * @param isTransparent if the texture has transparency
     */
    fun setSkin(uuid: UUID, urls: ArrayList<String?>, bodyType: String?, isTransparent: Boolean) {
        if ("reset" == urls[0]) {
            resetSkin(uuid)
        } else {
            // Loop skins and download if not cached
            for (url in urls) {
                if (!cachedUrls.containsKey(url)) {
                    texturesToLoad.add(SkinLoadJob(uuid, url!!, bodyType.toString(), isTransparent))
                }
            }

            // Load textures
            try {
                try {
                    loadQueuedSkins()
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
            } finally {
                val thread = Thread {
                    // We just have downloaded all skins, we now have to merge them into one texture
                    val path = "skins/merge_" + urls.hashCode()
                    val resourceLocation = ResourceLocation(MetropiaMod.MOD_ID, path)

                    // Create a new texture
                    val textures = ArrayList<BufferedImage>()

                    for (url in urls) {
                        val cachedResource =
                            cachedUrls[url]
                        // Get the image at the texture location
                        val dynamicTexture =
                            textureManager!!.getTexture(cachedResource!!) as DynamicTexture?
                        val bufferedImage = BufferedImage(
                            dynamicTexture!!.textureData!!.width, dynamicTexture.textureData!!
                                .height, BufferedImage.TYPE_INT_ARGB
                        )
                        for (y in 0 until dynamicTexture.textureData!!.height) {
                            for (x in 0 until dynamicTexture.textureData!!.width) {
                                val rgb = dynamicTexture.textureData!!.getPixelRGBA(x, y)
                                bufferedImage.setRGB(x, y, rgb)
                            }
                        }
                        textures.add(bufferedImage)
                    }

                    var size = 128

                    // Find the biggest texture
                    for (texture in textures) {
                        if (texture.width > size || texture.height > size) {
                            size = texture.width
                        }
                    }

                    val merged = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
                    // Merge the textures like a layer system
                    for (texture in textures) {
                        // Scale image to the largest size
                        val scaled = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
                        scaled.graphics.drawImage(texture, 0, 0, size, size, null)
                        for (y in 0 until scaled.height) {
                            for (x in 0 until scaled.width) {
                                val rgb = scaled.getRGB(x, y)
                                val alpha = (rgb shr 24) and 0xFF
                                val red = (rgb shr 16) and 0xFF
                                val green = (rgb shr 8) and 0xFF
                                val blue = rgb and 0xFF

                                val abgrColor =
                                    alpha shl 24 or (blue shl 16) or (green shl 8) or red

                                if ((abgrColor and -0x1000000) != 0) {
                                    merged.setRGB(x, y, abgrColor)
                                }
                            }
                        }
                    }

                    val nativeImage = NativeImage(merged.width, merged.height, true)

                    for (y in 0 until merged.height) {
                        for (x in 0 until merged.width) {
                            val rgb = merged.getRGB(x, y)
                            val alpha = (rgb shr 24) and 0xFF
                            val red = (rgb shr 16) and 0xFF
                            val green = (rgb shr 8) and 0xFF
                            val blue = (rgb) and 0xFF

                            val abgrColor =
                                alpha shl 24 or (blue shl 16) or (green shl 8) or red

                            nativeImage.setPixelRGBA(x, y, abgrColor)
                        }
                    }

                    val dynamicTexture = DynamicTexture(nativeImage)
                    Minecraft.getInstance().getTextureManager().loadTexture(resourceLocation, dynamicTexture)


                    // Put the new skin in the playerSkinMap
                    playerSkinMap[uuid] = ClientSkinData(
                        resourceLocation,
                        bodyType!!
                    )
                }
                thread.start()
            }
        }
    }

    //    /**
    //     * Reset the skin of the target player.
    //     *
    //     * If the player was not found no action will be performed.
    //     * @param uuid the uuid of the player
    //     */
    fun resetSkin(uuid: UUID) {
        if (originalSkinMap.containsKey(uuid)) {
            playerSkinMap[uuid] = originalSkinMap[uuid]
        }
    }

    @Throws(IOException::class)
    fun loadQueuedSkins() {
        val i = texturesToLoad.iterator()
        while (i.hasNext()) {
            val loadJob = i.next()
            var resourceLocation = ResourceLocation(MetropiaMod.MOD_ID, "skins/" + loadJob.url.hashCode())
            val downloadingTexture = ImageDownloader.downloadImage(loadJob.url)
            if (downloadingTexture != null) {
                val nativeImage = NativeImage(downloadingTexture.width, downloadingTexture.height, true)

                for (y in 0 until downloadingTexture.height) {
                    for (x in 0 until downloadingTexture.width) {
                        val rgb = downloadingTexture.getRGB(x, y)
                        val alpha = (rgb shr 24) and 0xFF
                        val red = (rgb shr 16) and 0xFF
                        val green = (rgb shr 8) and 0xFF
                        val blue = (rgb) and 0xFF

                        val abgrColor = (alpha shl 24) or (blue shl 16) or (green shl 8) or red

                        nativeImage.setPixelRGBA(x, y, abgrColor)
                    }
                }

                val dynamicTexture = DynamicTexture(nativeImage)
                textureManager!!.loadTexture(resourceLocation, dynamicTexture)
            } else {
                resourceLocation = missing.resourceLocation
            }

            cachedUrls[loadJob.url] = resourceLocation
            //            playerSkinMap.put(loadJob.uuid, new ClientSkinData(resourceLocation, loadJob.bodyType));
            i.remove()
        }
    }

    fun checkSkin(player: AbstractClientPlayerEntity) {
        if (!player.hasPlayerInfo()) return

        val currentSkin = player.playerInfo.playerTextures[MinecraftProfileTexture.Type.SKIN]
        val wantedSkin = playerSkinMap[player.uniqueID]
        if (wantedSkin != null) {
            if (currentSkin !== wantedSkin.resourceLocation) {
                if (!originalSkinMap.containsKey(player.uniqueID)) {
                    originalSkinMap[player.uniqueID] = ClientSkinData(
                        currentSkin!!, player.playerInfo.skinType!!
                    )
                }
                player.playerInfo.playerTextures[MinecraftProfileTexture.Type.SKIN] = wantedSkin.resourceLocation
            }

            player.playerInfo.skinType = wantedSkin.modelType
        }
    }
}