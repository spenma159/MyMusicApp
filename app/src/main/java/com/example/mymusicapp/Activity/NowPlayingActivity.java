package com.example.mymusicapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mymusicapp.API.Song;
import com.example.mymusicapp.Adapter.MusicRecyclerViewAdapter;
import com.example.mymusicapp.R;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.List;

import retrofit2.Call;

public class NowPlayingActivity extends AppCompatActivity {
    private static final String TAG = "message";
    private ImageView btnBack, albumCover, btnPlayPause, btnPlayNext, btnPlayPrev, btnStop;
    private TextView txtCurrDuration, txtEndDuration, txtTitle, txtSinger;
    private SeekBar seekBarSong;
    private MediaPlayer mediaPlayer;
    static URI uri;
    private boolean flagPlay;
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
        flagPlay = false;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        initialize();
        getIntentMethod();
        seekBarSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(mediaPlayer != null && b){
                    mediaPlayer.seekTo(i * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        NowPlayingActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBarSong.setProgress(currentPosition);
                    txtCurrDuration.setText(formatedTime(currentPosition));
                }
                handler.postDelayed(this,1000);
            }
        });





        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NowPlayingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private String formatedTime(int currentPosition) {
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

    private void getIntentMethod(){
        Intent intent = this.getIntent();
        Song song = (Song) intent.getSerializableExtra("music");
        Log.i(TAG, "title : " + song.getTitle());
        Log.i(TAG, "singer : " + song.getSinger());
        Log.i(TAG, "url : " + song.getUrl());
        Log.i(TAG, "media player : " + mediaPlayer);

        Glide.with(NowPlayingActivity.this)
                .load(song.getAlbum())
                .apply(new RequestOptions().fitCenter())
                .into(albumCover);
        txtTitle.setText(song.getTitle());
        txtSinger.setText(song.getSinger());

        btnPlayPause.setImageResource(R.drawable.ic_pause);
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this,Uri.parse(song.getUrl()));
        Log.i(TAG, "media player : " + mediaPlayer);
        play();
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flagPlay == true){
                    btnPlayPause.setImageResource(R.drawable.ic_play);
                    pause();
                }else {
                    btnPlayPause.setImageResource(R.drawable.ic_pause);
                    play();
                }
            }
        });
    }
    private void play(){
        mediaPlayer.start();
        flagPlay = true;
    }
    private  void pause(){
        mediaPlayer.pause();
        flagPlay = false;
    }

}