package fr.epyi.metropiamod.events;

import fr.epyi.metropiamod.MetropiaMod;
import net.minecraft.client.audio.Sound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModSoundEvents {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MetropiaMod.MOD_ID);

    public static final RegistryObject<SoundEvent> OPEN_BAG = registerSoundEvent("open_bag");
    public static final RegistryObject<SoundEvent> OPEN_BASS = registerSoundEvent("open_bass");
    public static final RegistryObject<SoundEvent> DROP = registerSoundEvent("drop");
    public static final RegistryObject<SoundEvent> ERROR = registerSoundEvent("error");
    public static final RegistryObject<SoundEvent> TICK = registerSoundEvent("tick");
    public static final RegistryObject<SoundEvent> PUT = registerSoundEvent("put");
    public static final RegistryObject<SoundEvent> HOVER = registerSoundEvent("hover");

    public static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(MetropiaMod.MOD_ID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
