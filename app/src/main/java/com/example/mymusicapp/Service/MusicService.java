package com.example.mymusicapp.Service;

import static com.example.mymusicapp.App.App.CHANNEL_ID;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.mymusicapp.API.Song;
import com.example.mymusicapp.Activity.NowPlayingActivity;
import com.example.mymusicapp.EventBus.NextButtonEvent;
import com.example.mymusicapp.EventBus.PlayPauseButtonEvent;
import com.example.mymusicapp.EventBus.PrevButtonEvent;
import com.example.mymusicapp.EventBus.SeekEvent;
import com.example.mymusicapp.EventBus.UpdateUiActivityEvent;
import com.example.mymusicapp.EventBus.UpdateUiCurrentPositionEvent;
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
    Timer timer;
    int position;
    String textTitle, textSinger, albumCover;
    int mediaCurrentPosition, mediaDuration;
    boolean mediaIsPlaying;
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

    private static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mediaPlayer != null && position == intent.getIntExtra("position", -1)){

        }else {
            position = intent.getIntExtra("position", -1);
            songList = (List<Song>) intent.getSerializableExtra("music");
            if(mediaPlayer == null){
                play();
            }else{
                mediaPlayer.release();
                mediaPlayer = null;
                timer.cancel();
                play();
            }
        }
        EventBus.getDefault().post(new UpdateUiActivityEvent(textTitle, textSinger, albumCover, mediaIsPlaying));
        return START_NOT_STICKY;
    }

    public void notification(boolean playOrPause){
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);
        remoteViews.setTextViewText(R.id.title_song_notification, songList.get(position).getTitle());
        remoteViews.setTextViewText(R.id.singer_song_notification, songList.get(position).getSinger());

//        PlayPause Button Notification
        Intent intentPlayPauseMusic = new Intent(this, PlayPauseMusicReceiver.class);
        PendingIntent pendingIntentPlayPauseMusic = PendingIntent.getBroadcast(this,0, intentPlayPauseMusic, 0);
        if(playOrPause) remoteViews.setImageViewResource(R.id.play_button_notification, R.drawable.ic_pause2);
        else remoteViews.setImageViewResource(R.id.play_button_notification, R.drawable.ic_play2);
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
        PendingIntent pendingIntentClick = PendingIntent.getActivity(this,0,clickIntent,0);

        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_android)
                .setCustomBigContentView(remoteViews)
                .setContentIntent(pendingIntentClick)
                .build();

        startForeground(1,notification);
    }



    private void play(){
        mediaPlayer = MediaPlayer.create(this, Uri.parse(songList.get(position).getUrl()));
        textTitle = songList.get(position).getTitle();
        textSinger = songList.get(position).getSinger();
        mediaDuration = mediaPlayer.getDuration();
        albumCover = songList.get(position).getAlbum();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(position < songList.size() - 1){
                    position++;
                }else {
                    position = 0;
                }
                play();
            }
        });
        mediaPlayer.start();
        mediaIsPlaying = mediaPlayer.isPlaying();
        notification(mediaIsPlaying);
        updateCurrentDuration();
    }

    private void updateCurrentDuration(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mediaCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                EventBus.getDefault().post(new UpdateUiCurrentPositionEvent(mediaCurrentPosition, mediaDuration));
                EventBus.getDefault().post(new UpdateUiActivityEvent(textTitle, textSinger, albumCover, mediaIsPlaying));
            }
        },1,1000);
    }

    private void stop(){
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
            timer.cancel();
            mediaIsPlaying = false;
            EventBus.getDefault().post(new UpdateUiActivityEvent(textTitle, textSinger, albumCover, mediaIsPlaying));
        }
    }


    @Subscribe()
    public void onMessageEvent(PlayPauseButtonEvent event) {
        if(!isMyServiceRunning(MusicService.class, this)){
            Intent intent = new Intent(this, MusicService.class);
            intent.putExtra("position",position);
            intent.putExtra("music", (Serializable) songList);
            startService(intent);
        } else if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            timer.cancel();
        } else {
            mediaPlayer.start();
            updateCurrentDuration();
        }
        mediaIsPlaying = mediaPlayer.isPlaying();
        EventBus.getDefault().post(new UpdateUiActivityEvent(textTitle, textSinger, albumCover, mediaIsPlaying));
        notification(mediaIsPlaying);
    }



    @Subscribe()
    public void onMessageEvent(NextButtonEvent event) {
        if(position < songList.size() - 1){
            position++;
        }else{
            position = 0;
        }
        if(!isMyServiceRunning(MusicService.class, this)){
            Intent intent = new Intent(this, MusicService.class);
            intent.putExtra("position",position);
            intent.putExtra("music", (Serializable) songList);
            startService(intent);
        }else if (mediaPlayer != null){
            stop();
            play();
        }
    }

    @Subscribe()
    public void onMessageEvent(PrevButtonEvent event) {
        if(position > 0){
            position--;
        }else{
            position = songList.size() - 1;
        }
        if(!isMyServiceRunning(MusicService.class, this)){
            Intent intent = new Intent(this, MusicService.class);
            intent.putExtra("position",position);
            intent.putExtra("music", (Serializable) songList);
            startService(intent);
        }else if (mediaPlayer != null){
            stop();
            play();
        }
    }

    @Subscribe()
    public void onMessageEvent(SeekEvent event) {
        if(mediaPlayer != null){
            mediaPlayer.seekTo(event.duration);
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
