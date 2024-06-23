package fr.epyi.metropiamod.capabilities;

import java.util.ArrayList;

public class SkinData implements ISkinData {

    private ArrayList<String> urls = new ArrayList<String>(0);
    private String bodyType = "default";

    @Override
    public ArrayList<String> getSkin() {
        return this.urls;
    }

    @Override
    public void setSkin(ArrayList<String> url) {
        this.urls = url == null ? new ArrayList<String>() : url;
    }

    @Override
    public String getModelType() {
        return this.bodyType;
    }

    @Override
    public void setModelType(String bodyType) {
        this.bodyType = bodyType;
    }
}
