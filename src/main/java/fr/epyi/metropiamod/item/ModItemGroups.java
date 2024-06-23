package fr.epyi.metropiamod.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroups {

    public static final ItemGroup METROPIA_UTILITIES = new ItemGroup("metropiaUtilitiesTab") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.CLOTH.get());
        }
    };

}
