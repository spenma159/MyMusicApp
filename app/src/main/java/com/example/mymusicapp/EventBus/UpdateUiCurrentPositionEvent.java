package com.example.mymusicapp.EventBus;

public class UpdateUiCurrentPositionEvent {
    public int mediaCurrentPosition, mediaDuration;

    public UpdateUiCurrentPositionEvent(int mediaCurrentPosition, int mediaDuration) {
        this.mediaCurrentPosition = mediaCurrentPosition;
        this.mediaDuration = mediaDuration;
    }
}
