package com.iiitd.ucsf.models;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Data implements Serializable {
    private String id;

    public String getAndroid_version() {
        return android_version;
    }

    public void setAndroid_version(String android_version) {
        this.android_version = android_version;
    }

    private String android_version;

    public String getAndroid_api_level() {
        return android_api_level;
    }

    public void setAndroid_api_level(String android_api_level) {
        this.android_api_level = android_api_level;
    }

    private String android_api_level;


    public HashMap<String, String> getAudio_data() {
        return audio_data;
    }

    public void setAudio_data(HashMap<String, String> audio_data) {
        this.audio_data = audio_data;
    }

    private HashMap<String, String> audio_data;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Data() {
    }

    public Data(String id,String api,String version,HashMap<String,String> audio_data) {
        this.id = id;
        this.audio_data=audio_data;
        this.android_api_level=api;
         this.android_version=version;


    }



}