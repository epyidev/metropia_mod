package fr.epyi.metropiamod.capabilities

interface ISkinData {
    fun getSkin(): ArrayList<String?>
    fun setSkin(urls: ArrayList<String?>)
    fun getModelType(): String
    fun setModelType(url: String)
}

