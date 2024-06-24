package fr.epyi.metropiamod.item

import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class ClothItem(properties: Properties?) : Item(properties) {
    companion object {
        fun setClothUrl(stack: ItemStack, url: String?) {
            val compound = stack.getOrCreateTag()
            compound.putString("clothUrl", url)
        }

        fun getClothUrl(stack: ItemStack): String {
            val compound = stack.tag
            if (compound != null && compound.contains("clothUrl")) {
                return compound.getString("clothUrl")
            }
            return ""
        }

        fun setClothDirtiness(stack: ItemStack, dirtiness: Int) {
            val compound = stack.getOrCreateTag()
            compound.putInt("clothDirtiness", dirtiness)
        }

        fun getClothDirtiness(stack: ItemStack): Int {
            val compound = stack.tag
            if (compound != null && compound.contains("clothDirtiness")) {
                return compound.getInt("clothDirtiness")
            }
            return 0
        }
    }
}