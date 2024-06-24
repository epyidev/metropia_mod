package fr.epyi.metropiamod.capabilities

class SkinData : ISkinData {

    private var urls = ArrayList<String?>()
    private var bodyType: String = "default"

    override fun getSkin(): ArrayList<String?> {
        return this.urls
    }

    override fun setSkin(urls: ArrayList<String?>) {
        this.urls = urls
    }

    override fun getModelType(): String {
        return this.bodyType
    }

    override fun setModelType(url: String) {
        this.bodyType = url
    }
}


