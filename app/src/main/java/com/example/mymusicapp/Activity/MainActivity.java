package com.example.mymusicapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.telecom.Call;
import android.widget.Toast;

import com.example.mymusicapp.Adapter.MusicRecyclerViewAdapter;
import com.example.mymusicapp.R;
import com.example.mymusicapp.Retrofit.Get;
import com.example.mymusicapp.API.Song;

import java.util.List;



import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerViewMusic;
    MusicRecyclerViewAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    Get getRetrofit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerViewMusic = findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewMusic.setLayoutManager(linearLayoutManager);

        getRetrofit = new Get();
        retrofit2.Call<List<Song>> call = getRetrofit.getMusic();
        call.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Song>> call, Response<List<Song>> response) {
                if(response.isSuccessful()){
                    List<Song> songs = response.body();
                    adapter = new MusicRecyclerViewAdapter(MainActivity.this,songs);
                    recyclerViewMusic.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Song>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Message" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}