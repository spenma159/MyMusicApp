package com.example.mymusicapp.Service;


import static com.example.mymusicapp.Activity.NowPlayingActivity.flagPlay;
//import static com.example.mymusicapp.Activity.NowPlayingActivity.position;
import static com.example.mymusicapp.App.App.CHANNEL_ID;
import static com.example.mymusicapp.App.App.mediaPlayer;
import static com.example.mymusicapp.App.App.position;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.mymusicapp.API.Song;
import com.example.mymusicapp.Activity.NowPlayingActivity;
import com.example.mymusicapp.EventBus.MusicStartEvent;
import com.example.mymusicapp.R;
import com.example.mymusicapp.Receiver.NextMusicReceiver;
import com.example.mymusicapp.Receiver.PlayPauseMusicReceiver;
import com.example.mymusicapp.Receiver.PrevMusicReceiver;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.List;

public class MusicService extends Service {
    public static final String ACTION_START = "action_start";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_STOP = "action_stop";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREV = "action_prev";
    private static final String ACTION_RESUME = "action_resume";

//    int position;

    List<Song> songList;
//    MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

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
        intentPlayPauseMusic.putExtra("music", (Serializable) songList);
        PendingIntent pendingIntentPlayPauseMusic = PendingIntent.getBroadcast(this,0, intentPlayPauseMusic, 0);
        remoteViews.setOnClickPendingIntent(R.id.play_button_notification, pendingIntentPlayPauseMusic);

//        Next Button Notification
        Intent intentNextMusic = new Intent(this, NextMusicReceiver.class);
        intentNextMusic.putExtra("music", (Serializable) songList);
        PendingIntent pendingIntentNextMusic = PendingIntent.getBroadcast(this,0, intentNextMusic, 0);
        remoteViews.setOnClickPendingIntent(R.id.next_button_notification, pendingIntentNextMusic);

//        Prev Button Notification
        Intent intentPrevMusic = new Intent(this, PrevMusicReceiver.class);
        intentPrevMusic.putExtra("music", (Serializable) songList);
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
                    play(songList);
                    break;
                case ACTION_RESUME:
                    resume();
                    break;
                case ACTION_PAUSE:
                    pause();
                    break;
                case ACTION_STOP:
                    stopService(intent);
                    break;
                default:break;
            }
        }

        EventBus.getDefault().post(new MusicStartEvent(songList, position));

        return START_NOT_STICKY;
    }



    public static void startMusic(Context context, int position, List<Song> songList){
        Intent intent = new Intent(context, MusicService.class);
        intent.putExtra("position", position);
        intent.putExtra("music", (Serializable) songList);
        intent.setAction(ACTION_START);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startForegroundService(intent);
        }else {
            context.startService(intent);
        }
    }

    public void play(List<Song> songList){
        if(mediaPlayer == null){
            Log.i("TAG", "song list in position: " + songList.get(position).getUrl());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(songList.get(position).getUrl()));
//            Toast.makeText(getApplicationContext(), "media player get duration : " + mediaPlayer.getDuration(), Toast.LENGTH_SHORT).show();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if(position + 1 <= songList.size()){
                        EventBus.getDefault().post(new MusicStartEvent(songList, position++));
                    }else {
                        stopMusic(getApplicationContext(),position,songList);
                    }
                }
            });
        }else{
            stop();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(songList.get(position).getUrl()));
        }
        mediaPlayer.start();
    }

    public static void pauseMusic(Context context, int position, List<Song> songList){
        Intent intent = new Intent(context, MusicService.class);
        intent.putExtra("position", position);
        intent.putExtra("music", (Serializable) songList);
        intent.setAction(ACTION_PAUSE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startForegroundService(intent);
        }else {
            context.startService(intent);
        }

    }

    public void pause(){
        if(mediaPlayer != null){
            mediaPlayer.pause();
        }
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
        stopPlayer();
    }

    public void stopPlayer(){
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
//            Toast.makeText(this, "Media player released", Toast.LENGTH_SHORT).show();
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


    public static void resumeMusic(Context context, int position, List<Song> songList){
        Intent intent = new Intent(context, MusicService.class);
        intent.putExtra("position", position);
        intent.putExtra("music", (Serializable) songList);
        intent.setAction(ACTION_RESUME);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startForegroundService(intent);
        }else {
            context.startService(intent);
        }
    }

    private void resume() {
        if(mediaPlayer != null){
            mediaPlayer.start();
        }
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
