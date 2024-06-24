@file:Suppress("INACCESSIBLE_TYPE")

package fr.epyi.metropiamod

import fr.epyi.metropiamod.capabilities.CapabilityHandler
import fr.epyi.metropiamod.client.ClientSkinManager
import fr.epyi.metropiamod.commands.CustomCommand
import fr.epyi.metropiamod.config.SkinConfig
import fr.epyi.metropiamod.events.CustomInventoryEvents
import fr.epyi.metropiamod.events.ModSoundEvents
import fr.epyi.metropiamod.item.ModItems
import fr.epyi.metropiamod.network.*
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkEvent
import net.minecraftforge.fml.network.NetworkRegistry
import net.minecraftforge.fml.network.simple.SimpleChannel
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*
import java.util.function.Supplier

@Mod(MetropiaMod.MOD_ID)
class MetropiaMod {
    init {
        val loadingContext = ModLoadingContext.get()
        loadingContext.registerConfig(ModConfig.Type.COMMON, SkinConfig.SERVER_CONFIG, "re-skin.toml")

        val eventBus = FMLJavaModLoadingContext.get().modEventBus

        ModItems.register(eventBus)
        ModSoundEvents.register(eventBus)

        eventBus.addListener { event: FMLClientSetupEvent ->
            this.clientSetup(
                event
            )
        }
        eventBus.addListener { event: FMLCommonSetupEvent ->
            this.setup(
                event
            )
        }
        eventBus.addListener { event: InterModEnqueueEvent ->
            this.enqueueIMC(
                event
            )
        }
        eventBus.addListener { event: InterModProcessEvent ->
            this.processIMC(
                event
            )
        }
        eventBus.addListener { event: FMLClientSetupEvent ->
            this.doClientStuff(
                event
            )
        }
        MinecraftForge.EVENT_BUS.register(this)
    }

    private fun clientSetup(event: FMLClientSetupEvent) {
        ClientSkinManager.getTextureManager()
    }

    private fun setup(event: FMLCommonSetupEvent) {
        PacketHandler.init()
        CapabilityHandler.register()
        CustomCommand.registerNewArgTypes()

        var index = 0
        NETWORK.registerMessage(index,
            InventorySlotChangePacket::class.java,
            { inventorySlotChangePacket: InventorySlotChangePacket?, packetBuffer: PacketBuffer? ->
                InventorySlotChangePacket.encode(
                    inventorySlotChangePacket!!,
                    packetBuffer!!
                )
            },
            { packetBuffer: PacketBuffer? ->
                InventorySlotChangePacket.decode(
                    packetBuffer!!
                )
            },
            { packet: InventorySlotChangePacket?, ctx: Supplier<NetworkEvent.Context?>? ->
                InventorySlotChangePacket.handle(
                    packet!!,
                    ctx
                )
            }, Optional.of(NetworkDirection.PLAY_TO_SERVER)
        )

        index++
        NETWORK.registerMessage(index,
            InventoryDropPacket::class.java,
            { inventoryDropPacket: InventoryDropPacket?, packetBuffer: PacketBuffer? ->
                InventoryDropPacket.encode(
                    inventoryDropPacket!!,
                    packetBuffer!!
                )
            },
            { packetBuffer: PacketBuffer? ->
                InventoryDropPacket.decode(
                    packetBuffer!!
                )
            },
            { packet: InventoryDropPacket?, ctx: Supplier<NetworkEvent.Context?>? ->
                InventoryDropPacket.handle(
                    packet!!,
                    ctx
                )
            }, Optional.of(NetworkDirection.PLAY_TO_SERVER)
        )

        index++
        NETWORK.registerMessage(index,
            InventoryMergePacket::class.java,
            { inventoryMergePacket: InventoryMergePacket?, packetBuffer: PacketBuffer? ->
                InventoryMergePacket.encode(
                    inventoryMergePacket!!,
                    packetBuffer!!
                )
            },
            { packetBuffer: PacketBuffer? ->
                InventoryMergePacket.decode(
                    packetBuffer!!
                )
            },
            { packet: InventoryMergePacket?, ctx: Supplier<NetworkEvent.Context?>? ->
                InventoryMergePacket.handle(
                    packet!!,
                    ctx
                )
            }, Optional.of(NetworkDirection.PLAY_TO_SERVER)
        )

        index++
        NETWORK.registerMessage(index,
            InventorySplitPacket::class.java,
            { inventorySplitPacket: InventorySplitPacket?, packetBuffer: PacketBuffer? ->
                InventorySplitPacket.encode(
                    inventorySplitPacket!!,
                    packetBuffer!!
                )
            },
            { packetBuffer: PacketBuffer? ->
                InventorySplitPacket.decode(
                    packetBuffer!!
                )
            },
            { packet: InventorySplitPacket?, ctx: Supplier<NetworkEvent.Context?>? ->
                InventorySplitPacket.handle(
                    packet!!,
                    ctx
                )
            }, Optional.of(NetworkDirection.PLAY_TO_SERVER)
        )

        index++
        NETWORK.registerMessage(index,
            ClothChange::class.java,
            { clothChange: ClothChange?, packetBuffer: PacketBuffer? ->
                ClothChange.encode(
                    clothChange,
                    packetBuffer
                )
            },
            { packetBuffer: PacketBuffer? ->
                ClothChange.decode(
                    packetBuffer
                )
            },
            { packet: ClothChange?, ctx: Supplier<NetworkEvent.Context?>? ->
                ClothChange.handle(
                    packet,
                    ctx
                )
            }, Optional.of(NetworkDirection.PLAY_TO_SERVER)
        )
    }

    private fun doClientStuff(event: FMLClientSetupEvent) {
        MinecraftForge.EVENT_BUS.register(CustomInventoryEvents)
    }

    private fun enqueueIMC(event: InterModEnqueueEvent) {
    }

    private fun processIMC(event: InterModProcessEvent) {
    }

    @SubscribeEvent
    fun onServerStarting(event: FMLServerStartingEvent?) {
    }

    companion object {
        const val MOD_ID: String = "metropiamod"
        val LOGGER: Logger = LogManager.getLogger("MetropiaMod")

        private const val PROTOCOL_VERSION: String = "1"
        val NETWORK: SimpleChannel = NetworkRegistry.newSimpleChannel(ResourceLocation(MOD_ID, "channel"),
            { PROTOCOL_VERSION },
            { anObject: String? ->
                PROTOCOL_VERSION == anObject
            },
            { anObject: String? ->
                PROTOCOL_VERSION == anObject
            }
        )
    }
}