package com.example.mymusicapp.Receiver;

//import static com.example.mymusicapp.App.App.position;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mymusicapp.API.Song;
import com.example.mymusicapp.EventBus.MusicStartEvent;
import com.example.mymusicapp.EventBus.NextMusicEvent;
import com.example.mymusicapp.EventBus.PrevMusicEvent;
import com.example.mymusicapp.Service.MusicService;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class PrevMusicReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        EventBus.getDefault().post(new PrevMusicEvent());
    }
}
