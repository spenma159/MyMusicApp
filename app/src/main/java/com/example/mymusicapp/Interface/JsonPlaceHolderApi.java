package com.example.mymusicapp.Interface;

import com.example.mymusicapp.API.Song;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JsonPlaceHolderApi {

    @GET("song")
    Call<List<Song>> getSongs();
}
