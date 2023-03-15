package com.example.mymusicapp.EventBus;

import com.example.mymusicapp.API.Song;

import java.util.List;

public class PlayPauseMusicEvent {
    public static List<Song> songList;
    public static int position;

    public PlayPauseMusicEvent(int position, List<Song> songList){
        PlayPauseMusicEvent.position = position;
        PlayPauseMusicEvent.songList = songList;
    }
}
