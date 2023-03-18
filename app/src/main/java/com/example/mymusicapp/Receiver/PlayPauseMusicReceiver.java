package com.example.mymusicapp.Receiver;

//import static com.example.mymusicapp.App.App.position;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mymusicapp.API.Song;
import com.example.mymusicapp.EventBus.MusicStartEvent;
import com.example.mymusicapp.EventBus.PlayPauseMusicEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class PlayPauseMusicReceiver extends BroadcastReceiver {
//    int position;
//    private List<Song> songList;
    @Override
    public void onReceive(Context context, Intent intent) {
//        songList = (List<Song>) intent.getSerializableExtra("music");
//        position = intent.getIntExtra("position" , -1);
        EventBus.getDefault().post(new PlayPauseMusicEvent());
//        EventBus.getDefault().post(new MusicStartEvent(songList, position));
    }
}
