package com.example.mymusicapp.App;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.MediaStore;

public class App extends Application {

    public static final String CHANNEL_ID = "channel music";
    public static int position;
    public static MediaPlayer mediaPlayer;




    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channelMusic =  new NotificationChannel(
                    CHANNEL_ID,
                    "Music Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            channelMusic.setDescription("This is channel for music that playing in the background");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channelMusic);
        }
    }
}
