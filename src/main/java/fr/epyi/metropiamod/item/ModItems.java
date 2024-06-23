package fr.epyi.metropiamod.item;

import fr.epyi.metropiamod.MetropiaMod;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MetropiaMod.MOD_ID);

    public static final RegistryObject<Item> CLOTH = ITEMS.register("cloth",
            () -> new ClothItem(new Item.Properties().group(ModItemGroups.METROPIA_UTILITIES)));

    public static final RegistryObject<Item> EMPTY_CLOTH = ITEMS.register("empty_cloth",
            () -> new ClothItem(new Item.Properties().group(ModItemGroups.METROPIA_UTILITIES)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
