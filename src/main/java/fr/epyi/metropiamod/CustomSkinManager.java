package fr.epyi.metropiamod;

import com.google.common.collect.Maps;
import fr.epyi.metropiamod.capabilities.SkinLocationProvider;
import fr.epyi.metropiamod.config.SkinConfig;
import fr.epyi.metropiamod.network.PacketHandler;
import fr.epyi.metropiamod.network.client.ClientChangeSkin;
import fr.epyi.metropiamod.server.ServerSkinData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class CustomSkinManager {

    private static Map<UUID, ServerSkinData> playerSkins = Maps.newHashMap();

    public static void setSkin(PlayerEntity target, ArrayList<String> urls) {
        if(target != null) {
            target.getCapability(SkinLocationProvider.SKIN_LOC, null).ifPresent(skinCap -> {
                skinCap.setSkin(urls);
                if(urls.get(0) != null) {
                    PacketHandler.SKIN_CHANNEL.send(PacketDistributor.ALL.noArg(), new ClientChangeSkin(target.getUniqueID(), urls, skinCap.getModelType(), SkinConfig.ALLOW_TRANSPARENT_SKIN.get()));
                    playerSkins.put(target.getUniqueID(), new ServerSkinData(urls, skinCap.getModelType()));
                }
            });
        }
    }

    /**
     * Reset the skin of the target player.
     *
     * @param target the target player
     */
    public static void resetSkin(PlayerEntity target) {
        ArrayList<String> reset = new ArrayList<String>(1);
        reset.add("reset");
        setSkin(target, reset);
    }

    public static void setModel(PlayerEntity target, String modelType) {
        if(target != null) {
            target.getCapability(SkinLocationProvider.SKIN_LOC, null).ifPresent(skinCap -> {
                skinCap.setModelType(modelType);
                if(modelType.length() > 0) {
                    PacketHandler.SKIN_CHANNEL.send(PacketDistributor.ALL.noArg(), new ClientChangeSkin(target.getUniqueID(), skinCap.getSkin(), modelType, SkinConfig.ALLOW_TRANSPARENT_SKIN.get()));
                    playerSkins.put(target.getUniqueID(), new ServerSkinData(skinCap.getSkin(), modelType));
                }
            });
        }
    }

    /**
     * Send all the loaded skins to a player
     * @param player
     * @param excludeSelf
     */
    public static void sendAllToPlayer(ServerPlayerEntity player, boolean excludeSelf) {
        for(Map.Entry<UUID, ServerSkinData> skin : playerSkins.entrySet()) {
            if(!(excludeSelf && skin.getKey() == player.getUniqueID()) && skin.getValue() != null) {
                PacketHandler.sendToPlayer(new ClientChangeSkin(skin.getKey(), skin.getValue().urls, skin.getValue().modelType, SkinConfig.ALLOW_TRANSPARENT_SKIN.get()), player);
            }
        }
    }

    public static void playerLoggedOut(UUID uuid) {
        playerSkins.remove(uuid);
    }
}
