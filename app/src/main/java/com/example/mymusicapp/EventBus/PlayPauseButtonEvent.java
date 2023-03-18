package com.example.mymusicapp.EventBus;

import com.example.mymusicapp.API.Song;

import java.util.List;

public class PlayPauseButtonEvent {
    public int position;
    public List<Song> songList;
    public PlayPauseButtonEvent(int position, List<Song> songList) {
        this.position = position;
        this.songList = songList;
    }
}
