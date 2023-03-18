package com.example.mymusicapp.Receiver;

//import static com.example.mymusicapp.App.App.position;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.metrics.Event;

import com.example.mymusicapp.API.Song;
import com.example.mymusicapp.EventBus.MusicStartEvent;
import com.example.mymusicapp.EventBus.NextMusicEvent;
import com.example.mymusicapp.Service.MusicService;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class NextMusicReceiver extends BroadcastReceiver {
//    int position;
//    private List<Song> songList;
    @Override
    public void onReceive(Context context, Intent intent) {
        EventBus.getDefault().post(new NextMusicEvent());
    }
}
