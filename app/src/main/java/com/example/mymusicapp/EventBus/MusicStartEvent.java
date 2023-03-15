package com.example.mymusicapp.EventBus;

import android.media.MediaPlayer;

import com.example.mymusicapp.API.Song;

import java.util.List;

public class MusicStartEvent {
    public static List<Song> songList;
    public static int position;
    public MusicStartEvent(List<Song> songList, int position){
        this.songList = songList;
        this.position = position;
    }
}
