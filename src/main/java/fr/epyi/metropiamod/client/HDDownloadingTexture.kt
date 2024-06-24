package fr.epyi.metropiamod.client

import com.mojang.blaze3d.systems.RenderSystem
import fr.epyi.metropiamod.MetropiaMod
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.texture.NativeImage
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Util
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.util.concurrent.CompletableFuture

@OnlyIn(Dist.CLIENT)
class HDDownloadingTexture(
    cacheFileIn: String?,
    imageUrlIn: String,
    textureResourceLocation: ResourceLocation,
    isTransparent: Boolean,
    processTaskIn: Runnable?
) :
    DynamicTexture(512, 512, true) {
    private var textureLocation = ResourceLocation(MetropiaMod.MOD_ID, "textures/entity/alex.png")
    private val cacheFile: String?
    private val imageUrl: String

    private var imageWidth = 0
    private var imageHeight = 0
    private val isTransparent: Boolean

    private val legacySkin = true
    private val processTask: Runnable?
    private var future: CompletableFuture<*>? = null
    private var textureUploaded = false

    init {
        this.textureLocation = textureResourceLocation
        this.cacheFile = cacheFileIn
        this.imageUrl = imageUrlIn
        this.isTransparent = isTransparent
        this.processTask = processTaskIn
    }

    private fun setImage(nativeImageIn: NativeImage) {
        if (this.processTask != null) {
            processTask.run()
        }

        Minecraft.getInstance().execute {
            this.textureUploaded = true
            if (!RenderSystem.isOnRenderThread()) {
                RenderSystem.recordRenderCall {
                    this.upload(nativeImageIn)
                }
            } else {
                this.upload(nativeImageIn)
            }
        }
    }

    private fun upload(imageIn: NativeImage) {
        TextureUtil.prepareImage(this.getGlTextureId(), imageIn.width, imageIn.height)
        imageIn.uploadTextureSub(0, 0, 0, true)
    }

    @Throws(IOException::class)
    fun load(manager: IResourceManager?) {
        Minecraft.getInstance().execute {
            if (!this.textureUploaded) {
                super.loadTexture(manager)

                this.textureUploaded = true
            }
        }
        if (this.future == null) {
            val nativeimage: NativeImage?
            if (this.cacheFile != null) {
                LOGGER.debug(
                    "Loading http texture from local cache ({})",
                    cacheFile as Any?
                )
                val fileinputstream = FileInputStream(this.cacheFile)
                nativeimage = this.loadTexture(fileinputstream)
            } else {
                nativeimage = null
            }

            if (nativeimage != null) {
                this.setImage(nativeimage)
            } else {
                this.future = CompletableFuture.runAsync({
                    val httpurlconnection: HttpURLConnection? = null
                    LOGGER.debug(
                        "Downloading http texture from {} to {}",
                        imageUrl,
                        cacheFile
                    )

                    val httpClient: HttpClient = HttpClients.createDefault()
                    val httpGet = HttpGet(this.imageUrl)
                    try {
                        val response = httpClient.execute(httpGet)
                        val entity = response.entity
                        val inputStream = entity.content

                        val nativeimage_1 = this.loadTexture(inputStream)
                        if (nativeimage_1 != null) {
                            this.setImage(nativeimage_1)
                            super.setTextureData(nativeimage_1)
                        }
                        return@runAsync
                    } catch (exception: Exception) {
                        LOGGER.error(
                            "Couldn't download http texture",
                            exception as Throwable
                        )
                        return@runAsync
                    } finally {
                        httpurlconnection?.disconnect()
                    }
                }, Util.getServerExecutor())
            }
        }
    }

    private fun loadTexture(inputStreamIn: InputStream): NativeImage? {
        var nativeimage: NativeImage? = null

        try {
            nativeimage = NativeImage.read(inputStreamIn)
            if (this.legacySkin) {
                nativeimage = processLegacySkin(nativeimage)
            }
        } catch (ioexception: IOException) {
            LOGGER.warn("Error while loading the skin texture", ioexception as Throwable)
        }

        return nativeimage
    }

    //    /**
    //     * {@link DownloadingTexture#processLegacySkin(net.minecraft.client.renderer.texture.NativeImage)}}
    //     * @param nativeImageIn
    //     * @return
    //     */
    private fun processLegacySkin(nativeImageIn: NativeImage?): NativeImage? {
        var nativeImageIn = nativeImageIn ?: return null
        this.imageWidth = nativeImageIn.width
        this.imageHeight = nativeImageIn.height
        val flag = this.imageHeight == this.imageWidth / 2
        val scaleFactor = (1f / 64f) * this.imageWidth
        if (flag) {
            val nativeimage = NativeImage(this.imageWidth, this.imageWidth, true)
            nativeimage.copyImageData(nativeImageIn)
            nativeImageIn.close()
            nativeImageIn = nativeimage
            if (!isTransparent) {
                nativeimage.fillAreaRGBA(
                    0,
                    (32 * scaleFactor).toInt(),
                    (64 * scaleFactor).toInt(),
                    (32 * scaleFactor).toInt(), 0
                )
            }
            copyAreaRGBAScale(nativeimage, 4, 16, 16, 32, 4, 4, true, false, scaleFactor)
            copyAreaRGBAScale(nativeimage, 8, 16, 16, 32, 4, 4, true, false, scaleFactor)
            copyAreaRGBAScale(nativeimage, 0, 20, 24, 32, 4, 12, true, false, scaleFactor)
            copyAreaRGBAScale(nativeimage, 4, 20, 16, 32, 4, 12, true, false, scaleFactor)
            copyAreaRGBAScale(nativeimage, 8, 20, 8, 32, 4, 12, true, false, scaleFactor)
            copyAreaRGBAScale(nativeimage, 12, 20, 16, 32, 4, 12, true, false, scaleFactor)
            copyAreaRGBAScale(nativeimage, 44, 16, -8, 32, 4, 4, true, false, scaleFactor)
            copyAreaRGBAScale(nativeimage, 48, 16, -8, 32, 4, 4, true, false, scaleFactor)
            copyAreaRGBAScale(nativeimage, 40, 20, 0, 32, 4, 12, true, false, scaleFactor)
            copyAreaRGBAScale(nativeimage, 44, 20, -8, 32, 4, 12, true, false, scaleFactor)
            copyAreaRGBAScale(nativeimage, 48, 20, -16, 32, 4, 12, true, false, scaleFactor)
            copyAreaRGBAScale(nativeimage, 52, 20, -8, 32, 4, 12, true, false, scaleFactor)
        }


        if (!isTransparent) {
            setAreaOpaque(
                nativeImageIn, 0, 0,
                (32 * scaleFactor).toInt(),
                (16 * scaleFactor).toInt()
            )
        }
        if (flag) {
            setAreaTransparent(
                nativeImageIn, 32, 0,
                (64 * scaleFactor).toInt(),
                (32 * scaleFactor).toInt()
            )
        }

        if (!isTransparent) {
            setAreaOpaque(
                nativeImageIn, 0,
                (16 * scaleFactor).toInt(),
                (64 * scaleFactor).toInt(),
                (32 * scaleFactor).toInt()
            )
            setAreaOpaque(
                nativeImageIn,
                (16 * scaleFactor).toInt(),
                (48 * scaleFactor).toInt(),
                (48 * scaleFactor).toInt(),
                (64 * scaleFactor).toInt()
            )
        }
        return nativeImageIn
    }

    companion object {
        private val LOGGER: Logger = LogManager.getLogger()

        private fun copyAreaRGBAScale(
            nativeimage: NativeImage,
            xFrom: Int,
            yFrom: Int,
            xToDelta: Int,
            yToDelta: Int,
            widthIn: Int,
            heightIn: Int,
            mirrorX: Boolean,
            mirrorY: Boolean,
            scaleFactor: Float
        ) {
            nativeimage.copyAreaRGBA(
                (xFrom * scaleFactor).toInt(),
                (yFrom * scaleFactor).toInt(),
                (xToDelta * scaleFactor).toInt(),
                (yToDelta * scaleFactor).toInt(),
                (widthIn * scaleFactor).toInt(),
                (heightIn * scaleFactor).toInt(),
                mirrorX,
                mirrorY
            )
        }

        private fun setAreaTransparent(image: NativeImage?, x: Int, y: Int, width: Int, height: Int) {
            for (i in x until width) {
                for (j in y until height) {
                    val k = image!!.getPixelRGBA(i, j)
                    if ((k shr 24 and 255) < 128) {
                        return
                    }
                }
            }

            for (l in x until width) {
                for (i1 in y until height) {
                    image!!.setPixelRGBA(l, i1, image.getPixelRGBA(l, i1) and 16777215)
                }
            }
        }

        private fun setAreaOpaque(image: NativeImage?, x: Int, y: Int, width: Int, height: Int) {
            for (i in x until width) {
                for (j in y until height) {
                    image!!.setPixelRGBA(i, j, image.getPixelRGBA(i, j) or -16777216)
                }
            }
        }
    }
}