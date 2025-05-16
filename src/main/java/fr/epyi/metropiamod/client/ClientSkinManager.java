package fr.epyi.metropiamod.client;

import com.google.common.collect.Maps;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import fr.epyi.metropiamod.MetropiaMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

public class ClientSkinManager {

    private static TextureManager textureManager;

    private static ClientSkinData missing = new ClientSkinData(new ResourceLocation("textures/entity/alex.png"), "default");

    private static final Map<UUID, ClientSkinData> playerSkinMap = Maps.newHashMap();

    private static final Map<UUID, ClientSkinData> originalSkinMap = Maps.newHashMap();

    private static Map<String, ResourceLocation> cachedUrls = Maps.newHashMap();

    private static List<SkinLoadJob> texturesToLoad = new ArrayList<>();

    public static void getTextureManager() {
        textureManager = Minecraft.getInstance().getTextureManager();
    }

    public static void clearSkinCache() {
        // Release all cached resources and clear the cache list
        for(ResourceLocation resource : cachedUrls.values()) {
            textureManager.deleteTexture(resource);
        }
        cachedUrls.clear();

        for(Map.Entry<UUID, ClientSkinData> entry : playerSkinMap.entrySet()) {
            ClientSkinData skinData = originalSkinMap.getOrDefault(entry.getKey(), missing);

            playerSkinMap.put(entry.getKey(), skinData == null ? missing : skinData);
        }
    }

    public static void cleanupSkinData() {
        playerSkinMap.clear();
        originalSkinMap.clear();
    }

    /**
     * Change the model of the target player.
     *
     * @param uuid the uuid of the player
     * @param urls url list of the skin
     * @param bodyType the body type of the player
     * @param isTransparent if the texture has transparency
     */
    public static void setSkin(UUID uuid, ArrayList<String> urls, String bodyType, boolean isTransparent) {
        if("reset".equals(urls.get(0))) {
            resetSkin(uuid);
        } else {
            // Loop skins and download if not cached
            for (String url : urls) {
                if(url.startsWith("http") && !cachedUrls.containsKey(url)) {
                    texturesToLoad.add(new SkinLoadJob(uuid, url, bodyType, isTransparent));
                }
            }

            // Load textures
            try {
                try {
                    ClientSkinManager.loadQueuedSkins();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } finally {
                Thread thread = new Thread(() -> {
                    // We just have downloaded all skins, we now have to merge them into one texture
                    String path = "skins/merge_" + urls.hashCode();
                    ResourceLocation resourceLocation = new ResourceLocation(MetropiaMod.MOD_ID, path);

                    // Create a new texture
                    ArrayList<BufferedImage> textures = new ArrayList<>();

                    for (String url : urls) {
                        if (url.startsWith("http")) {
                            ResourceLocation cachedResource = cachedUrls.get(url);
                            // Get the image at the texture location
                            DynamicTexture dynamicTexture = (DynamicTexture) textureManager.getTexture(cachedResource);
                            BufferedImage bufferedImage = new BufferedImage(dynamicTexture.getTextureData().getWidth(), dynamicTexture.getTextureData().getHeight(), BufferedImage.TYPE_INT_ARGB);
                            for (int y = 0; y < dynamicTexture.getTextureData().getHeight(); y++) {
                                for (int x = 0; x < dynamicTexture.getTextureData().getWidth(); x++) {
                                    int rgb = dynamicTexture.getTextureData().getPixelRGBA(x, y);
                                    bufferedImage.setRGB(x, y, rgb);
                                }
                            }
                            textures.add(bufferedImage);
                        }
                    }

                    int size = 128;

                    // Find the biggest texture
                    for (BufferedImage texture : textures) {
                        if(texture.getWidth() > size || texture.getHeight() > size) {
                            size = texture.getWidth();
                        }
                    }

                    BufferedImage merged = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                    // Merge the textures like a layer system
                    for (BufferedImage texture : textures) {
                        // Scale image to the largest size
                        BufferedImage scaled = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                        scaled.getGraphics().drawImage(texture, 0, 0, size, size, null);
                        for (int y = 0; y < scaled.getHeight(); y++) {
                            for (int x = 0; x < scaled.getWidth(); x++) {
                                int rgb = scaled.getRGB(x, y);
                                int alpha = (rgb >> 24) & 0xFF;
                                int red = (rgb >> 16) & 0xFF;
                                int green = (rgb >> 8) & 0xFF;
                                int blue = rgb & 0xFF;

                                int abgrColor = (alpha << 24) | (blue << 16) | (green << 8) | red;

                                if((abgrColor & 0xFF000000) != 0) {
                                    merged.setRGB(x, y, abgrColor);
                                }
                            }
                        }
                    }

                    NativeImage nativeImage = new NativeImage(merged.getWidth(), merged.getHeight(), true);

                    for (int y = 0; y < merged.getHeight(); y++) {
                        for (int x = 0; x < merged.getWidth(); x++) {
                            int rgb = merged.getRGB(x, y);
                            int alpha = (rgb >> 24) & 0xFF;
                            int red = (rgb >> 16) & 0xFF;
                            int green = (rgb >> 8) & 0xFF;
                            int blue = (rgb) & 0xFF;

                            int abgrColor = (alpha << 24) | (blue << 16) | (green << 8) | red;

                            nativeImage.setPixelRGBA(x, y, abgrColor);
                        }
                    }

                    DynamicTexture dynamicTexture = new DynamicTexture(nativeImage);
                    Minecraft.getInstance().getTextureManager().loadTexture(resourceLocation, dynamicTexture);


                    // Put the new skin in the playerSkinMap
                    playerSkinMap.put(uuid, new ClientSkinData(resourceLocation, bodyType));
                });
                thread.start();
            }
        }
    }

//    /**
//     * Reset the skin of the target player.
//     *
//     * If the player was not found no action will be performed.
//     * @param uuid the uuid of the player
//     */
    public static void resetSkin(UUID uuid) {
        if(originalSkinMap.containsKey(uuid)) {
            playerSkinMap.put(uuid, originalSkinMap.get(uuid));
        }
    }

    public static void loadQueuedSkins() throws IOException {
        Iterator<SkinLoadJob> i = texturesToLoad.iterator();
        while (i.hasNext()) {
            SkinLoadJob loadJob = i.next();

            ResourceLocation resourceLocation = new ResourceLocation(MetropiaMod.MOD_ID, "skins/" + loadJob.url.hashCode());
            MetropiaMod.LOGGER.info("Downloading skin from: {}", loadJob.url);

            BufferedImage downloadingTexture = ImageDownloader.downloadImage(loadJob.url);

            if(downloadingTexture != null) {
                NativeImage nativeImage = new NativeImage(downloadingTexture.getWidth(), downloadingTexture.getHeight(), true);

                for (int y = 0; y < downloadingTexture.getHeight(); y++) {
                    for (int x = 0; x < downloadingTexture.getWidth(); x++) {
                        int rgb = downloadingTexture.getRGB(x, y);
                        int alpha = (rgb >> 24) & 0xFF;
                        int red = (rgb >> 16) & 0xFF;
                        int green = (rgb >> 8) & 0xFF;
                        int blue = (rgb) & 0xFF;

                        int abgrColor = (alpha << 24) | (blue << 16) | (green << 8) | red;

                        nativeImage.setPixelRGBA(x, y, abgrColor);
                    }
                }

                DynamicTexture dynamicTexture = new DynamicTexture(nativeImage);
                textureManager.loadTexture(resourceLocation, dynamicTexture);
            }
            else {
                resourceLocation = missing.resourceLocation;
            }

            cachedUrls.put(loadJob.url, resourceLocation);
//            playerSkinMap.put(loadJob.uuid, new ClientSkinData(resourceLocation, loadJob.bodyType));
            i.remove();
        }
    }

    public static void checkSkin(AbstractClientPlayerEntity player) {
        if(!player.hasPlayerInfo()) return;

        ResourceLocation currentSkin = player.playerInfo.playerTextures.get(MinecraftProfileTexture.Type.SKIN);
        ClientSkinData wantedSkin = playerSkinMap.get(player.getUniqueID());
        if(wantedSkin != null) {
            if(currentSkin != wantedSkin.resourceLocation) {
                if(!originalSkinMap.containsKey(player.getUniqueID())) {
                    originalSkinMap.put(player.getUniqueID(), new ClientSkinData(currentSkin, player.playerInfo.skinType));
                }
                player.playerInfo.playerTextures.put(MinecraftProfileTexture.Type.SKIN, wantedSkin.resourceLocation);
            }

            player.playerInfo.skinType = wantedSkin.modelType;
        }
    }
}
