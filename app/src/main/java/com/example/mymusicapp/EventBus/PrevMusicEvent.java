package com.example.mymusicapp.EventBus;

import com.example.mymusicapp.API.Song;

import java.util.List;

public class PrevMusicEvent {
    public static List<Song> songList;
    public PrevMusicEvent(List<Song> songList) {
        this.songList = songList;
    }
}
