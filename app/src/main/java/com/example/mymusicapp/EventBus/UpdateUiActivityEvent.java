package com.example.mymusicapp.EventBus;

public class UpdateUiActivityEvent {
    public String textTitle, textSinger, albumCover;
    public boolean mediaIsPlaying;
    public UpdateUiActivityEvent(String textTitle, String textSinger, String albumCover, boolean mediaIsPlaying) {
        this.textTitle = textTitle;
        this.textSinger = textSinger;
        this.albumCover = albumCover;
        this.mediaIsPlaying = mediaIsPlaying;
    }
}
