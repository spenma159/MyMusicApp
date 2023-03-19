package com.example.mymusicapp.Receiver;

//import static com.example.mymusicapp.App.App.position;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mymusicapp.EventBus.NextButtonEvent;
import com.example.mymusicapp.EventBus.PrevButtonEvent;

import org.greenrobot.eventbus.EventBus;

public class PrevMusicReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        EventBus.getDefault().post(new PrevButtonEvent());
    }
}
