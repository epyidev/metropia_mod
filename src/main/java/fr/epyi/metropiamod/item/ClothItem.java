package fr.epyi.metropiamod.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class ClothItem extends Item {
    public ClothItem(Properties properties) {
        super(properties);
    }

    public static void setClothUrl(ItemStack stack, String url) {
        CompoundNBT compound = stack.getOrCreateTag();
        compound.putString("clothUrl", url);
    }

    public static String getClothUrl(ItemStack stack) {
        CompoundNBT compound = stack.getTag();
        if (compound != null && compound.contains("clothUrl")) {
            return compound.getString("clothUrl");
        }
        return "";
    }

    public static void setClothDirtiness(ItemStack stack, int dirtiness) {
        CompoundNBT compound = stack.getOrCreateTag();
        compound.putInt("clothDirtiness", dirtiness);
    }

    public static int getClothDirtiness(ItemStack stack) {
        CompoundNBT compound = stack.getTag();
        if (compound != null && compound.contains("clothDirtiness")) {
            return compound.getInt("clothDirtiness");
        }
        return 0;
    }

    public static void setClothArmor(ItemStack stack, int armor) {
        CompoundNBT compound = stack.getOrCreateTag();
        compound.putInt("clothArmor", armor);
    }

    public static int getClothArmor(ItemStack stack) {
        CompoundNBT compound = stack.getTag();
        if (compound != null && compound.contains("clothArmor")) {
            return compound.getInt("clothArmor");
        }
        return 0;
    }
}