package com.example.mymusicapp.EventBus;

import com.example.mymusicapp.API.Song;

import java.util.List;

public class PlayPauseMusicEvent {
    public static List<Song> songList;

    public PlayPauseMusicEvent(List<Song> songList){
        PlayPauseMusicEvent.songList = songList;
    }
}
