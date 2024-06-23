package fr.epyi.metropiamod.network.client;

import fr.epyi.metropiamod.client.ClientSkinManager;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Supplier;

public class ClientChangeSkin {


    public final UUID uuid;
    public final int size;
    public final ArrayList<String> urls;
    public final String bodyType;
    public final boolean isTransparent;

    public ClientChangeSkin(UUID uuid, ArrayList<String> urls, String bodyType, boolean isTransparent) {
        this.uuid = uuid;
        this.size = urls.size();
        this.urls = urls;
        this.bodyType = bodyType;
        this.isTransparent = isTransparent;
    }

    public static void encode(ClientChangeSkin msg, PacketBuffer outBuffer) {
        outBuffer.writeUniqueId(msg.uuid);

        // Write the size of the list
        outBuffer.writeInt(msg.urls.size());

        // Write each string in the list
        for (String url : msg.urls) {
            outBuffer.writeString(url);
        }

        outBuffer.writeString(msg.bodyType);
        outBuffer.writeBoolean(msg.isTransparent);
    }

    public static ClientChangeSkin decode(PacketBuffer inBuffer) {
        UUID uuid = inBuffer.readUniqueId();

        // Read the size of the list
        int listSize = inBuffer.readInt();

        // Read each string in the list
        ArrayList<String> urls = new ArrayList<>();
        for (int i = 0; i < listSize; i++) {
            urls.add(inBuffer.readString());
        }

        String bodyType = inBuffer.readString();
        boolean isTransparent = inBuffer.readBoolean();

        return new ClientChangeSkin(uuid, urls, bodyType, isTransparent);
    }

    public static class Handler {
        public static void handle(ClientChangeSkin msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() ->
                    ClientSkinManager.setSkin(msg.uuid, msg.urls, msg.bodyType, msg.isTransparent));
            ctx.get().setPacketHandled(true);
        }
    }
}
