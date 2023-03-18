package com.example.mymusicapp.Service;


import static com.example.mymusicapp.Activity.NowPlayingActivity.flagPlay;
//import static com.example.mymusicapp.Activity.NowPlayingActivity.position;
import static com.example.mymusicapp.App.App.CHANNEL_ID;
//import static com.example.mymusicapp.App.App.mediaPlayer;
//import static com.example.mymusicapp.App.App.position;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.mymusicapp.API.Song;
import com.example.mymusicapp.Activity.NowPlayingActivity;
import com.example.mymusicapp.EventBus.CurrentlyPlayingEvent;
import com.example.mymusicapp.EventBus.MusicStartEvent;
import com.example.mymusicapp.EventBus.NextMusicEvent;
import com.example.mymusicapp.EventBus.PauseMusicEvent;
import com.example.mymusicapp.EventBus.PlayPauseButtonEvent;
import com.example.mymusicapp.EventBus.PlayPauseMusicEvent;
import com.example.mymusicapp.EventBus.PrevMusicEvent;
import com.example.mymusicapp.EventBus.SeekEvent;
import com.example.mymusicapp.EventBus.ResumeMusicEvent;
import com.example.mymusicapp.R;
import com.example.mymusicapp.Receiver.NextMusicReceiver;
import com.example.mymusicapp.Receiver.PlayPauseMusicReceiver;
import com.example.mymusicapp.Receiver.PrevMusicReceiver;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    public static final String ACTION_START = "action_start";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_STOP = "action_stop";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREV = "action_prev";
    private static final String ACTION_RESUME = "action_resume";
    Timer timer;
    int position;

    List<Song> songList;
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        position = intent.getIntExtra("position", -1);
        songList = (List<Song>) intent.getSerializableExtra("music");

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);
        remoteViews.setTextViewText(R.id.title_song_notification, songList.get(position).getTitle());
        remoteViews.setTextViewText(R.id.singer_song_notification, songList.get(position).getSinger());
        if(!flagPlay) remoteViews.setImageViewResource(R.id.play_button_notification, R.drawable.ic_play2);
        else remoteViews.setImageViewResource(R.id.play_button_notification, R.drawable.ic_pause2);

//        PlayPause Button Notification
        Intent intentPlayPauseMusic = new Intent(this, PlayPauseMusicReceiver.class);
        PendingIntent pendingIntentPlayPauseMusic = PendingIntent.getBroadcast(this,0, intentPlayPauseMusic, 0);
        remoteViews.setOnClickPendingIntent(R.id.play_button_notification, pendingIntentPlayPauseMusic);

//        Next Button Notification
        Intent intentNextMusic = new Intent(this, NextMusicReceiver.class);
        PendingIntent pendingIntentNextMusic = PendingIntent.getBroadcast(this,0, intentNextMusic, 0);
        remoteViews.setOnClickPendingIntent(R.id.next_button_notification, pendingIntentNextMusic);

//        Prev Button Notification
        Intent intentPrevMusic = new Intent(this, PrevMusicReceiver.class);
        PendingIntent pendingIntentPrevMusic = PendingIntent.getBroadcast(this,0, intentPrevMusic, 0);
        remoteViews.setOnClickPendingIntent(R.id.prev_button_notification, pendingIntentPrevMusic);

        Intent clickIntent = new Intent(this, NowPlayingActivity.class);
        clickIntent.putExtra("position",position);
        clickIntent.putExtra("music", (Serializable) songList);
        PendingIntent pendingIntentClick = PendingIntent.getActivity(this,0,clickIntent,0);

        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_android)
                .setCustomBigContentView(remoteViews)
                .setContentIntent(pendingIntentClick)
                .build();

        startForeground(1,notification);

        if(intent != null && intent.getAction() != null){
            switch (intent.getAction()){
                case ACTION_START:
                case ACTION_PREV:
                case ACTION_NEXT:
                    play(songList, position);
                    break;
//                case ACTION_RESUME:
//                    resume();
//                    break;
//                case ACTION_PAUSE:
//                    pause();
//                    break;
                case ACTION_STOP:
                    stopService(intent);
                    break;
                default:break;
            }
        }

//        EventBus.getDefault().post(new MusicStartEvent(songList, position));

        return START_NOT_STICKY;
    }

    private static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void startMusic(Context context, int position, List<Song> songList){
        if(!isMyServiceRunning(MusicService.class, context)){
            Intent intent = new Intent(context, MusicService.class);
            intent.putExtra("position", position);
            intent.putExtra("music", (Serializable) songList);
            intent.setAction(ACTION_START);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                context.startForegroundService(intent);
            }else {
                context.startService(intent);
            }
        }else{
//            service sudah jalan tinggal ganti lagu
            EventBus.getDefault().post(new ResumeMusicEvent());
        }
    }

    public void play(List<Song> songList, int position){
        if(mediaPlayer != null) {
            stop();
        }
        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(songList.get(position).getUrl()));
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(getPosition() < songList.size() - 1){
                    EventBus.getDefault().post(new MusicStartEvent(songList, getPosition() + 1));
                }else {
                    stopMusic(getApplicationContext(),position,songList);
                }
            }
        });
        mediaPlayer.start();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                EventBus.getDefault().post(new CurrentlyPlayingEvent(position, songList, mediaPlayer.getCurrentPosition() / 1000, mediaPlayer.getDuration()));
            }
        },0,1000);
    }

    public static void stopMusic(Context context, int position, List<Song> songList){
        Intent intent = new Intent(context, MusicService.class);
        intent.putExtra("position", position);
        intent.putExtra("music", (Serializable) songList);
        intent.setAction(ACTION_STOP);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startForegroundService(intent);
        }else {
            context.startService(intent);
        }
    }
    public void stop(){
        flagPlay = false;
        stopPlayer();
    }

    public void stopPlayer(){
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if(timer != null){
            timer.cancel();
        }
    }

    public static void nextMusic(Context context, int position, List<Song> songList){
        Intent intent = new Intent(context, MusicService.class);
        intent.putExtra("position", position);
        intent.putExtra("music", (Serializable) songList);
        intent.setAction(ACTION_NEXT);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startForegroundService(intent);
        }else {
            context.startService(intent);
        }
    }

    public static void prevMusic(Context context, int position, List<Song> songList){
        Intent intent = new Intent(context, MusicService.class);
        intent.putExtra("position", position);
        intent.putExtra("music", (Serializable) songList);
        intent.setAction(ACTION_PREV);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startForegroundService(intent);
        }else {
            context.startService(intent);
        }
    }

    @Subscribe()
    public void onMessageEvent(SeekEvent event) {
        if(mediaPlayer != null){
            mediaPlayer.seekTo(event.duration);
        }
    }

    @Subscribe()
    public void onMessageEvent(ResumeMusicEvent event) {
        if(mediaPlayer != null){
            mediaPlayer.start();
            Intent intent = new Intent(this, MusicService.class);
            intent.putExtra("position", position);
            intent.putExtra("music", (Serializable) songList);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                this.startForegroundService(intent);
            }else {
                this.startService(intent);
            }
        }
    }

    @Subscribe()
    public void onMessageEvent(PauseMusicEvent event) {
        if(mediaPlayer != null){
            mediaPlayer.pause();
            Intent intent = new Intent(this, MusicService.class);
            intent.putExtra("position", position);
            intent.putExtra("music", (Serializable) songList);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                this.startForegroundService(intent);
            }else {
                this.startService(intent);
            }
        }
    }

    @Subscribe()
    public void onMessageEvent(PlayPauseButtonEvent event) {
        if(mediaPlayer == null && !isMyServiceRunning(MusicService.class,getApplicationContext())){
            startMusic(getApplicationContext(), event.position, event.songList);
        }else{
            EventBus.getDefault().post(new PlayPauseMusicEvent());
        }
    }

    @Subscribe()
    public void onMessageEvent(NextMusicEvent event) {
        flagPlay = true;
        if(position < songList.size() - 1){
            position++;
        }else{
            stopMusic(getApplicationContext(),position,songList);
        }
        MusicService.nextMusic(this, position, songList);
    }
    @Subscribe()
    public void onMessageEvent(PrevMusicEvent event) {
        flagPlay = true;
        if(position > 0){
            position--;
        }else {
            stopMusic(getApplicationContext(),position,songList);
        }
        MusicService.prevMusic(this, position, songList);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
