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
        if ("reset".equals(urls.get(0))) {
            resetSkin(uuid);
        } else {
            for (String url : urls) {
                if (url.startsWith("http") && !cachedUrls.containsKey(url)) {
                    texturesToLoad.add(new SkinLoadJob(uuid, url, bodyType, isTransparent));
                }
            }

            try {
                ClientSkinManager.loadQueuedSkins();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Thread thread = new Thread(() -> {
                String path = "skins/merge_" + urls.hashCode();
                ResourceLocation resourceLocation = new ResourceLocation(MetropiaMod.MOD_ID, path);

                ArrayList<BufferedImage> textures = new ArrayList<>();

                for (int i = 0; i < urls.size(); i++) {
                    String current = urls.get(i);

                    if (current.startsWith("http")) {
                        ResourceLocation cachedResource = cachedUrls.get(current);
                        DynamicTexture dynamicTexture = (DynamicTexture) textureManager.getTexture(cachedResource);
                        BufferedImage bufferedImage = new BufferedImage(dynamicTexture.getTextureData().getWidth(), dynamicTexture.getTextureData().getHeight(), BufferedImage.TYPE_INT_ARGB);

                        // Copie les données de texture
                        for (int y = 0; y < dynamicTexture.getTextureData().getHeight(); y++) {
                            for (int x = 0; x < dynamicTexture.getTextureData().getWidth(); x++) {
                                int rgb = dynamicTexture.getTextureData().getPixelRGBA(x, y);
                                bufferedImage.setRGB(x, y, rgb);
                            }
                        }

                        // Vérifie si le prochain élément est une couleur
                        if (i + 1 < urls.size() && urls.get(i + 1).matches("^[0-9A-Fa-f]{6}$")) {
                            String hexColor = urls.get(i + 1);
                            int color = Integer.parseInt(hexColor, 16);
                            int red = (color >> 16) & 0xFF;
                            int green = (color >> 8) & 0xFF;
                            int blue = color & 0xFF;

                            // Applique l'effet de multiply blending
                            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                                for (int x = 0; x < bufferedImage.getWidth(); x++) {
                                    int argb = bufferedImage.getRGB(x, y);

                                    int alpha = (argb >> 24) & 0xFF;
                                    int originalRed = (argb >> 16) & 0xFF;
                                    int originalGreen = (argb >> 8) & 0xFF;
                                    int originalBlue = argb & 0xFF;

                                    // Multiply blending
                                    int blendedRed = (originalRed * red) / 255;
                                    int blendedGreen = (originalGreen * green) / 255;
                                    int blendedBlue = (originalBlue * blue) / 255;

                                    // Préserve l'alpha
                                    int blendedColor = (alpha << 24) | (blendedRed << 16) | (blendedGreen << 8) | blendedBlue;
                                    bufferedImage.setRGB(x, y, blendedColor);
                                }
                            }

                            // Ignore la couleur pour la prochaine itération
                            i++;
                        }

                        textures.add(bufferedImage);
                    }
                }

                // Merge des textures avec transparence
                int size = 128;
                for (BufferedImage texture : textures) {
                    size = Math.max(size, Math.max(texture.getWidth(), texture.getHeight()));
                }

                BufferedImage merged = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                for (BufferedImage texture : textures) {
                    for (int y = 0; y < texture.getHeight(); y++) {
                        for (int x = 0; x < texture.getWidth(); x++) {
                            int topRgb = texture.getRGB(x, y);
                            int bottomRgb = merged.getRGB(x, y);

                            int topAlpha = (topRgb >> 24) & 0xFF;
                            int topRed = (topRgb >> 16) & 0xFF;
                            int topGreen = (topRgb >> 8) & 0xFF;
                            int topBlue = topRgb & 0xFF;

                            int bottomAlpha = (bottomRgb >> 24) & 0xFF;
                            int bottomRed = (bottomRgb >> 16) & 0xFF;
                            int bottomGreen = (bottomRgb >> 8) & 0xFF;
                            int bottomBlue = bottomRgb & 0xFF;

                            // Gestion de la transparence
                            float alphaTop = topAlpha / 255.0f;
                            float alphaBottom = bottomAlpha / 255.0f;
                            float finalAlpha = alphaTop + alphaBottom * (1 - alphaTop);

                            int finalRed = (int) ((topRed * alphaTop + bottomRed * alphaBottom * (1 - alphaTop)) / finalAlpha);
                            int finalGreen = (int) ((topGreen * alphaTop + bottomGreen * alphaBottom * (1 - alphaTop)) / finalAlpha);
                            int finalBlue = (int) ((topBlue * alphaTop + bottomBlue * alphaBottom * (1 - alphaTop)) / finalAlpha);
                            int finalAlphaInt = (int) (finalAlpha * 255);

                            int finalColor = (finalAlphaInt << 24) | (finalRed << 16) | (finalGreen << 8) | finalBlue;
                            merged.setRGB(x, y, finalColor);
                        }
                    }
                }

                // Conversion en NativeImage
                NativeImage nativeImage = new NativeImage(merged.getWidth(), merged.getHeight(), true);
                for (int y = 0; y < merged.getHeight(); y++) {
                    for (int x = 0; x < merged.getWidth(); x++) {
                        int rgb = merged.getRGB(x, y);
                        int alpha = (rgb >> 24) & 0xFF;
                        int red = (rgb >> 16) & 0xFF;
                        int green = (rgb >> 8) & 0xFF;
                        int blue = rgb & 0xFF;
                        int abgrColor = (alpha << 24) | (blue << 16) | (green << 8) | red;
                        nativeImage.setPixelRGBA(x, y, abgrColor);
                    }
                }

                // Enregistrement de la texture
                DynamicTexture dynamicTexture = new DynamicTexture(nativeImage);
                Minecraft.getInstance().getTextureManager().loadTexture(resourceLocation, dynamicTexture);
                playerSkinMap.put(uuid, new ClientSkinData(resourceLocation, bodyType));
            });
            thread.start();
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
