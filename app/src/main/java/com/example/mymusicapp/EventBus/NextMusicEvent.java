package com.example.mymusicapp.EventBus;

import com.example.mymusicapp.API.Song;

import java.util.List;

public class NextMusicEvent {
    public static List<Song> songList;
    public NextMusicEvent(List<Song> songList) {
        this.songList = songList;
    }
}
