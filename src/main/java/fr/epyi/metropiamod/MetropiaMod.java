package fr.epyi.metropiamod;

import fr.epyi.metropiamod.capabilities.CapabilityHandler;
import fr.epyi.metropiamod.client.ClientSkinManager;
import fr.epyi.metropiamod.commands.CustomCommand;
import fr.epyi.metropiamod.config.SkinConfig;
import fr.epyi.metropiamod.events.CustomInventoryEvents;
import fr.epyi.metropiamod.events.ModSoundEvents;
import fr.epyi.metropiamod.events.PlayerArmorHandler;
import fr.epyi.metropiamod.item.ModItems;
import fr.epyi.metropiamod.network.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

@Mod(MetropiaMod.MOD_ID)
public class MetropiaMod
{
    public static final String MOD_ID = "metropiamod";
    public static final Logger LOGGER = LogManager.getLogger("MetropiaMod");

    public static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, "channel"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public MetropiaMod() {
        ModLoadingContext loadingContext = ModLoadingContext.get();
        loadingContext.registerConfig(ModConfig.Type.COMMON, SkinConfig.SERVER_CONFIG, "re-skin.toml");

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(eventBus);
        ModSoundEvents.register(eventBus);

        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::setup);
        eventBus.addListener(this::enqueueIMC);
        eventBus.addListener(this::processIMC);
        eventBus.addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.register(this);

        new PlayerArmorHandler();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ClientSkinManager.getTextureManager();
    }

    private void setup(final FMLCommonSetupEvent event) {
        PacketHandler.init();
        CapabilityHandler.register();



        CustomCommand.registerNewArgTypes();

        int index = 0;
        NETWORK.registerMessage(index, InventorySlotChangePacket.class, InventorySlotChangePacket::encode, InventorySlotChangePacket::decode, InventorySlotChangePacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));

        index++;
        NETWORK.registerMessage(index, InventoryDropPacket.class, InventoryDropPacket::encode, InventoryDropPacket::decode, InventoryDropPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));

        index++;
        NETWORK.registerMessage(index, InventoryMergePacket.class, InventoryMergePacket::encode, InventoryMergePacket::decode, InventoryMergePacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));

        index++;
        NETWORK.registerMessage(index, InventorySplitPacket.class, InventorySplitPacket::encode, InventorySplitPacket::decode, InventorySplitPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));

        index++;
        NETWORK.registerMessage(index, ClothChange.class, ClothChange::encode, ClothChange::decode, ClothChange::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new CustomInventoryEvents());
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
    }

    private void processIMC(final InterModProcessEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
    }
}
