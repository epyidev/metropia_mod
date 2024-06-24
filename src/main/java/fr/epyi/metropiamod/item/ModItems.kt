package fr.epyi.metropiamod.item

import fr.epyi.metropiamod.MetropiaMod
import net.minecraft.item.Item
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.RegistryObject
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object ModItems {
    val ITEMS: DeferredRegister<Item> = DeferredRegister.create<Item>(ForgeRegistries.ITEMS, MetropiaMod.MOD_ID)

    val CLOTH: RegistryObject<Item> = ITEMS.register<Item>(
        "cloth"
    ) {
        ClothItem(
            Item.Properties().group(ModItemGroups.METROPIA_UTILITIES)
        )
    }

    val EMPTY_CLOTH: RegistryObject<Item> = ITEMS.register<Item>(
        "empty_cloth"
    ) {
        ClothItem(
            Item.Properties().group(ModItemGroups.METROPIA_UTILITIES)
        )
    }

    fun register(eventBus: IEventBus?) {
        ITEMS.register(eventBus)
    }
}