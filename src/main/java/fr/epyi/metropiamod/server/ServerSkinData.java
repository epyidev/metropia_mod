package fr.epyi.metropiamod.server;

import java.util.ArrayList;

public class ServerSkinData {

    public final ArrayList<String> urls;
    public final String modelType;

    public ServerSkinData(ArrayList<String> urls, String modelType) {
        this.urls = urls;
        this.modelType = modelType;
    }
}
