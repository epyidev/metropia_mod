package fr.epyi.metropiamod.capabilities

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.INBT
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.Capability.IStorage

class SkinLocationStorage : IStorage<ISkinData> {
    override fun writeNBT(capability: Capability<ISkinData>, instance: ISkinData, side: Direction): INBT? {
        val nbt = CompoundNBT()
        nbt.putString(SKIN_NBT, GSON.toJson(instance.getSkin()))
        nbt.putString(BODY_TYPE_NBT, instance.getModelType())
        return nbt
    }

    override fun readNBT(capability: Capability<ISkinData>, instance: ISkinData, side: Direction, nbt: INBT) {
        if (nbt is CompoundNBT) {
            val compoundNBT = nbt
            val listType = object : TypeToken<ArrayList<String?>?>() {}.type
            val skinList = GSON.fromJson<ArrayList<String?>>(compoundNBT.getString(SKIN_NBT), listType)
            instance.setSkin(skinList)
            instance.setModelType(compoundNBT.getString(BODY_TYPE_NBT))
        }
    }

    companion object {
        private const val SKIN_NBT = "skin"
        private const val BODY_TYPE_NBT = "bodyType"

        private val GSON = Gson()
    }
}