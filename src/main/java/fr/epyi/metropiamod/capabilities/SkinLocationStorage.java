package fr.epyi.metropiamod.capabilities;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class SkinLocationStorage implements Capability.IStorage<ISkinData> {

    private static final String SKIN_NBT = "skin";
    private static final String BODY_TYPE_NBT = "bodyType";

    private static final Gson GSON = new Gson();

    @Nullable
    @Override
    public INBT writeNBT(Capability<ISkinData> capability, ISkinData instance, Direction side) {
        final CompoundNBT nbt = new CompoundNBT();
        nbt.putString(SKIN_NBT, GSON.toJson(instance.getSkin()));
        nbt.putString(BODY_TYPE_NBT, instance.getModelType());
        return nbt;
    }

    @Override
    public void readNBT(Capability<ISkinData> capability, ISkinData instance, Direction side, INBT nbt) {
        if(nbt instanceof CompoundNBT) {
            CompoundNBT compoundNBT = (CompoundNBT) nbt;
            Type listType = new TypeToken<ArrayList<String>>() {}.getType();
            ArrayList<String> skinList = GSON.fromJson(compoundNBT.getString(SKIN_NBT), listType);
            instance.setSkin(skinList);
            instance.setModelType(compoundNBT.getString(BODY_TYPE_NBT));
        }
    }
}
