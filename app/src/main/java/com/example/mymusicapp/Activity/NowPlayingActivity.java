package com.example.mymusicapp.Activity;

//import static com.example.mymusicapp.App.App.mediaPlayer;
import static com.example.mymusicapp.App.App.mediaPlayer;
import static com.example.mymusicapp.App.App.position;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mymusicapp.API.Song;
import com.example.mymusicapp.EventBus.MusicStartEvent;
import com.example.mymusicapp.EventBus.PlayPauseMusicEvent;
import com.example.mymusicapp.R;
import com.example.mymusicapp.Receiver.PlayPauseReceiver;
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
//        int totalDuration = Integer.parseInt(Uri.parse(songList.get(position).getUrl()))
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
        EventBus.getDefault().register(this);
        Intent intent1 = new Intent(this, MusicService.class);
        intent1.putExtra("position", position);
        intent1.putExtra("music", (Serializable) songList);
        startService(intent1);
        MusicService.startMusic(this, position, songList);
        seekBar();
        buttonClick(songList);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void buttonClick(List<Song> songList) {
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer == null){
                    MusicService.startMusic(getApplicationContext(), position, songList);
                    btnPlayPause.setImageResource(R.drawable.ic_pause);
                    flagPlay = true;
                }else if(!flagPlay){
                    MusicService.resumeMusic(getApplicationContext(), position, songList);
                    btnPlayPause.setImageResource(R.drawable.ic_pause);
                    flagPlay = true;
                }else {
                    MusicService.pauseMusic(getApplicationContext(), position, songList);
                    btnPlayPause.setImageResource(R.drawable.ic_play);
                    flagPlay = false;
                }
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
                if(position < songList.size() - 1){
                    position++;
                }else{
                    position = 0;
                }
                MusicService.nextMusic(getApplicationContext(), position, songList);

            }
        });

        btnPlayPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position > 0){
                    position--;
                }else {
                    position = songList.size() - 1;
                }
                MusicService.prevMusic(getApplicationContext(), position, songList);
            }
        });
    }

    private void seekBar(){
//        FFmpegMediaMetadataRetriever mFFmpegMediaMetadataRetriever = new FFmpegMediaMetadataRetriever();
//        mFFmpegMediaMetadataRetriever.setDataSource(songList.get(position).getUrl());
//        String mVideoDuration =  mFFmpegMediaMetadataRetriever .extractMetadata(FFmpegMediaMetadataRetriever .METADATA_KEY_DURATION);
//        long mTimeInMilliseconds= Long.parseLong(mVideoDuration);
//        Log.i(TAG, "duration: " + mTimeInMilliseconds);
        seekBarSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(mediaPlayer != null){
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
                if(mediaPlayer != null){
                    int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBarSong.setProgress(currentPosition);
                    txtCurrDuration.setText(formattedTime(currentPosition));
                }
                handler.postDelayed(this,100);
            }
        });
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
        songList = MusicStartEvent.songList;
        position = MusicStartEvent.position;
        txtTitle.setText(songList.get(position).getTitle());
        txtSinger.setText(songList.get(position).getSinger());
        Glide.with(this)
                .load(songList.get(position).getAlbum())
                .apply(new RequestOptions().fitCenter())
                .into(albumCover);
        seekBar();
        if(mediaPlayer == null){
            MusicService.startMusic(getApplicationContext(), position, PlayPauseMusicEvent.songList);
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            flagPlay = true;
        }else if(!flagPlay){
            MusicService.resumeMusic(getApplicationContext(), position, PlayPauseMusicEvent.songList);
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            flagPlay = true;
        }else {
            MusicService.pauseMusic(getApplicationContext(), position, PlayPauseMusicEvent.songList);
            btnPlayPause.setImageResource(R.drawable.ic_play);
            flagPlay = false;
        }
//        if(!flagPlay){
//            btnPlayPause.setImageResource(R.drawable.ic_pause);
//            flagPlay = true;
//            MusicService.resumeMusic(getApplicationContext(),PlayPauseMusicEvent.position, PlayPauseMusicEvent.songList);
//        }else{
//            btnPlayPause.setImageResource(R.drawable.ic_play);
//            flagPlay = false;
//            MusicService.pauseMusic(getApplicationContext(),PlayPauseMusicEvent.position, PlayPauseMusicEvent.songList);
//        }
    }







}