package com.example.mymusicapp.Activity;

//import static com.example.mymusicapp.App.App.mediaPlayer;
//import static com.example.mymusicapp.App.App.mediaPlayer;
//import static com.example.mymusicapp.App.App.position;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mymusicapp.API.Song;
import com.example.mymusicapp.EventBus.CurrentlyPlayingEvent;
import com.example.mymusicapp.EventBus.MusicStartEvent;
import com.example.mymusicapp.EventBus.NextMusicEvent;
import com.example.mymusicapp.EventBus.PauseMusicEvent;
import com.example.mymusicapp.EventBus.PlayPauseButtonEvent;
import com.example.mymusicapp.EventBus.PlayPauseMusicEvent;
import com.example.mymusicapp.EventBus.PrevMusicEvent;
import com.example.mymusicapp.EventBus.ResumeMusicEvent;
import com.example.mymusicapp.EventBus.SeekEvent;
//import com.example.mymusicapp.EventBus.StartMusicEvent;
import com.example.mymusicapp.EventBus.SyncNotificationLayoutEvent;
import com.example.mymusicapp.R;
import com.example.mymusicapp.Service.MusicService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.List;

//import wseemann.media.FFmpegMediaMetadataRetriever;

public class NowPlayingActivity extends AppCompatActivity {
    private static final String TAG = "message";
    private ImageView btnBack, albumCover, btnPlayPause, btnPlayNext, btnPlayPrev, btnStop;
    private TextView txtCurrDuration, txtEndDuration, txtTitle, txtSinger;
    private SeekBar seekBarSong;
    List<Song> songList;
    int position;
    public static boolean flagPlay;
    private Handler handler = new Handler();
    MusicService musicService;
    private void initialize (){
        btnBack = findViewById(R.id.btn_back_now_playing);
        albumCover = findViewById(R.id.cover_album);
        btnPlayPause = findViewById(R.id.play_button_now_playing);
        btnPlayNext = findViewById(R.id.next_button_now_playing);
        btnPlayPrev = findViewById(R.id.prev_button_now_playing);
        btnStop = findViewById(R.id.stop_button_now_playing);
        txtCurrDuration = findViewById(R.id.curr_duration);
        txtEndDuration = findViewById(R.id.end_duration);
        txtTitle = findViewById(R.id.text_title_now_playing);
        txtSinger = findViewById(R.id.text_singer_now_playing);
        seekBarSong = findViewById(R.id.seekbar_now_paying);
        musicService = new MusicService();
        flagPlay = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        Log.i(TAG, "onCreate: ");
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        initialize();
        Intent intent = this.getIntent();
        songList = (List<Song>) intent.getSerializableExtra("music");
        position = intent.getIntExtra("position", -1);
        txtTitle.setText(songList.get(position).getTitle());
        txtSinger.setText(songList.get(position).getSinger());
        Glide.with(this)
                .load(songList.get(position).getAlbum())
                .apply(new RequestOptions().fitCenter())
                .into(albumCover);

        MusicService.startMusic(this, position, songList);
//        seekBar();
        buttonClick();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        seekBar();
    }

    private void buttonClick() {
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPlayPause.setImageResource(R.drawable.ic_pause);
                EventBus.getDefault().post(new PlayPauseButtonEvent(position, songList));
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPlayPause.setImageResource(R.drawable.ic_play);
                flagPlay = false;
                MusicService.stopMusic(getApplicationContext(), position, songList);
            }
        });

        btnPlayNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flagPlay = true;
                EventBus.getDefault().post(new NextMusicEvent());
                btnPlayPause.setImageResource(R.drawable.ic_pause);
            }
        });

        btnPlayPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flagPlay = true;
                btnPlayPause.setImageResource(R.drawable.ic_pause);
                EventBus.getDefault().post(new PrevMusicEvent());
            }
        });
    }

    private void seekBar(){
        seekBarSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                EventBus.getDefault().post(new SeekEvent(i * 1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }


    private String formattedTime(int currentPosition) {
        String totalOut = "";
        String totalNow = "";
        String seconds = String.valueOf(currentPosition % 60);
        String minutes = String.valueOf(currentPosition / 60);
        totalOut = minutes + ":" + seconds;
        totalNow = minutes + ":" + "0" + seconds;
        if(seconds.length() == 1){
            return totalNow;
        }else{
            return totalOut;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CurrentlyPlayingEvent event) {
        txtTitle.setText(event.songList.get(event.position).getTitle());
        txtSinger.setText(event.songList.get(event.position).getSinger());
        int totalDuration = event.duration / 1000;
        seekBarSong.setProgress(event.currentPosition + 1);
        seekBarSong.setMax(totalDuration);
        txtEndDuration.setText(formattedTime(totalDuration));
        txtCurrDuration.setText(formattedTime(event.currentPosition));
//        Glide.with(this)
//                .load(songList.get(position).getAlbum())
//                .apply(new RequestOptions().fitCenter())
//                .into(albumCover);
        Log.i(TAG, "current duration " + event.currentPosition);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MusicStartEvent event) {
        songList = MusicStartEvent.songList;
        position = MusicStartEvent.position;
        txtTitle.setText(songList.get(position).getTitle());
        txtSinger.setText(songList.get(position).getSinger());
        Glide.with(this)
                .load(songList.get(position).getAlbum())
                .apply(new RequestOptions().fitCenter())
                .into(albumCover);
        seekBar();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PlayPauseMusicEvent event) {
        if(!flagPlay){
            EventBus.getDefault().post(new ResumeMusicEvent());
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            flagPlay = true;
        }else {
            EventBus.getDefault().post(new PauseMusicEvent());
            btnPlayPause.setImageResource(R.drawable.ic_play);
            flagPlay = false;
        }
    }
}