package com.example.mymusicapp.EventBus;

import com.example.mymusicapp.API.Song;

import java.util.List;

public class CurrentlyPlayingEvent {
    public int position;
    public List<Song> songList;
    public int currentPosition;
    public int duration;
    public CurrentlyPlayingEvent(int position, List<Song> songList, int currentPosition, int duration) {
        this.position = position;
        this.songList = songList;
        this.currentPosition = currentPosition;
        this.duration = duration;
    }
}
