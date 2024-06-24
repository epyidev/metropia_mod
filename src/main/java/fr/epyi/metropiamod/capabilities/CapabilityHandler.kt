package fr.epyi.metropiamod.capabilities

import net.minecraftforge.common.capabilities.CapabilityManager

object CapabilityHandler {
    fun register() {
        CapabilityManager.INSTANCE.register(ISkinData::class.java, SkinLocationStorage()) { SkinData() }
    }
}