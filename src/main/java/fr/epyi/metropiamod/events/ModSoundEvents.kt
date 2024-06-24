package fr.epyi.metropiamod.events

import fr.epyi.metropiamod.MetropiaMod
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.RegistryObject
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object ModSoundEvents {
    val SOUND_EVENTS: DeferredRegister<SoundEvent> =
        DeferredRegister.create<SoundEvent>(ForgeRegistries.SOUND_EVENTS, MetropiaMod.MOD_ID)

    val OPEN_BAG: RegistryObject<SoundEvent> = registerSoundEvent("open_bag")
    val OPEN_BASS: RegistryObject<SoundEvent> = registerSoundEvent("open_bass")
    val DROP: RegistryObject<SoundEvent> = registerSoundEvent("drop")
    val ERROR: RegistryObject<SoundEvent> = registerSoundEvent("error")
    val TICK: RegistryObject<SoundEvent> = registerSoundEvent("tick")
    val PUT: RegistryObject<SoundEvent> = registerSoundEvent("put")
    val HOVER: RegistryObject<SoundEvent> = registerSoundEvent("hover")

    fun registerSoundEvent(name: String?): RegistryObject<SoundEvent> {
        return SOUND_EVENTS.register<SoundEvent>(
            name
        ) {
            SoundEvent(
                ResourceLocation(
                    MetropiaMod.MOD_ID,
                    name
                )
            )
        }
    }

    fun register(eventBus: IEventBus?) {
        SOUND_EVENTS.register(eventBus)
    }
}