package fr.epyi.metropiamod.capabilities

import net.minecraft.nbt.INBT
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.common.util.LazyOptional
import javax.annotation.Nonnull
import javax.annotation.Nullable

class SkinLocationProvider : ICapabilitySerializable<INBT> {
    companion object {
        @CapabilityInject(ISkinData::class)
        @JvmStatic
        lateinit var SKIN_LOC: Capability<ISkinData>
    }

    private val instance: LazyOptional<ISkinData> = LazyOptional.of { SKIN_LOC.defaultInstance!! }

    @Nonnull
    override fun <T : Any> getCapability(@Nonnull cap: Capability<T>, @Nullable side: Direction?): LazyOptional<T> {
        return if (cap == SKIN_LOC) instance.cast() else LazyOptional.empty()
    }

    override fun serializeNBT(): INBT {
        return SKIN_LOC.storage.writeNBT(SKIN_LOC, instance.orElse(null), null)!!
    }

    override fun deserializeNBT(nbt: INBT) {
        SKIN_LOC.storage.readNBT(SKIN_LOC, instance.orElse(null), null, nbt)
    }
}