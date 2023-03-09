package com.example.mymusicapp.Retrofit;

import com.example.mymusicapp.Interface.JsonPlaceHolderApi;
import com.example.mymusicapp.API.Song;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Get {

    public Call<List<Song>> getMusic(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://6152fa45c465200017d1a8e3.mockapi.io/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Song>> call = jsonPlaceHolderApi.getSongs();
        return call;
    }

}
