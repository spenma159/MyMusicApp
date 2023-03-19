package com.example.mymusicapp.Activity;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mymusicapp.EventBus.NextButtonEvent;
import com.example.mymusicapp.EventBus.PrevButtonEvent;
import com.example.mymusicapp.EventBus.SeekEvent;
//import com.example.mymusicapp.EventBus.StartMusicEvent;
import com.example.mymusicapp.EventBus.UpdateUiActivityEvent;
import com.example.mymusicapp.EventBus.UpdateUiCurrentPositionEvent;
import com.example.mymusicapp.EventBus.PlayPauseButtonEvent;
import com.example.mymusicapp.R;
import com.example.mymusicapp.Service.MusicService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

//import wseemann.media.FFmpegMediaMetadataRetriever;

public class NowPlayingActivity extends AppCompatActivity {
    private ImageView btnBack, albumCover, btnPlayPause, btnPlayNext, btnPlayPrev, btnStop;
    private TextView txtCurrDuration, txtEndDuration, txtTitle, txtSinger;
    private SeekBar seekBarSong;
    private Handler handler = new Handler();

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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        initialize();
        buttonClick();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    private void buttonClick() {
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new PlayPauseButtonEvent());
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MusicService.class);
                stopService(intent);
            }
        });

        btnPlayNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new NextButtonEvent());
            }
        });

        btnPlayPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new PrevButtonEvent());
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
        EventBus.getDefault().unregister(this);
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
    public void onMessageEvent(UpdateUiCurrentPositionEvent event) {
        int totalDuration = event.mediaDuration / 1000;
        seekBarSong.setMax(totalDuration);
        seekBarSong.setProgress(event.mediaCurrentPosition + 1);
        seekBar();
        txtEndDuration.setText(formattedTime(totalDuration));
        txtCurrDuration.setText(formattedTime(event.mediaCurrentPosition));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateUiActivityEvent event) {
        if(event.mediaIsPlaying) btnPlayPause.setImageResource(R.drawable.ic_pause);
        else btnPlayPause.setImageResource(R.drawable.ic_play);
        txtTitle.setText(event.textTitle);
        txtSinger.setText(event.textSinger);
//        Glide.with(this)
//                .load(event.albumCover)
//                .apply(new RequestOptions().fitCenter())
//                .into(albumCover);
    }




}