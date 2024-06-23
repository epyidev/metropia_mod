package fr.epyi.metropiamod.capabilities;

import java.util.ArrayList;

public interface ISkinData {
    ArrayList<String> getSkin();
    void setSkin(ArrayList<String> urls);
    String getModelType();
    void setModelType(String url);
}
