package fr.epyi.metropiamod.item

import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack

object ModItemGroups {
    val METROPIA_UTILITIES: ItemGroup = object : ItemGroup("metropiaUtilitiesTab") {
        override fun createIcon(): ItemStack {
            return ItemStack(ModItems.CLOTH.get())
        }
    }
}