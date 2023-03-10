package com.example.mymusicapp.API;

import java.io.Serializable;

public class Song implements Serializable {
    private int id;
    private String title;
    private String singer;
    private String url;
    private String album;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSinger() {
        return singer;
    }

    public String getUrl() {
        return url;
    }

    public String getAlbum() {
        return album;
    }

    public Song(){
    }
}
