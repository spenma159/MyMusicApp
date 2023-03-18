package com.example.mymusicapp.EventBus;

import com.example.mymusicapp.API.Song;

import java.util.List;

public class SyncNotificationLayoutEvent {
    public int position;
    public List<Song> songList;
    public SyncNotificationLayoutEvent(int i, List<Song> songList) {
        this.position = i;
        this.songList = songList;
    }
}
