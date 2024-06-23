package fr.epyi.metropiamod.network;

import fr.epyi.metropiamod.network.client.ClientChangeSkin;
import fr.epyi.metropiamod.network.client.ClientClearSkinCache;
import fr.epyi.metropiamod.network.server.ServerRequestSkins;
import fr.epyi.metropiamod.MetropiaMod;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {

    public static final String PROTOCOL_VERSION = "2";

    /**
     * Could just use {@link NetworkRegistry#newSimpleChannel} but this is more descriptive.
     */
    public static final SimpleChannel SKIN_CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MetropiaMod.MOD_ID, "skin_data"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    public static void init() {
        SKIN_CHANNEL.registerMessage(0, ClientChangeSkin.class, ClientChangeSkin::encode, ClientChangeSkin::decode, ClientChangeSkin.Handler::handle);
        SKIN_CHANNEL.registerMessage(1, ClientClearSkinCache.class, ClientClearSkinCache::encode, ClientClearSkinCache::decode, ClientClearSkinCache.Handler::handle);
        SKIN_CHANNEL.registerMessage(100, ServerRequestSkins.class, ServerRequestSkins::encode, ServerRequestSkins::decode, ServerRequestSkins.Handler::handle);
    }

    public static void sendToPlayer(Object obj, ServerPlayerEntity player) {
        SKIN_CHANNEL.sendTo(obj, player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    }
}
