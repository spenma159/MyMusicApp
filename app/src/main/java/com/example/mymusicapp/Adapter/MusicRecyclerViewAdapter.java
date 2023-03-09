package com.example.mymusicapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusicapp.R;
import com.example.mymusicapp.API.Song;

import java.util.List;

public class MusicRecyclerViewAdapter extends RecyclerView.Adapter<MusicRecyclerViewAdapter.MusicRecyclerViewViewHolder> {
    private Context context;
    private List<Song> songList;
    public MusicRecyclerViewAdapter(Context context, List<Song> dataList){
        this.context = context;
        this.songList = dataList;
    }


    @NonNull
    @Override
    public MusicRecyclerViewAdapter.MusicRecyclerViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_item,parent,false);
        MusicRecyclerViewViewHolder holder = new MusicRecyclerViewViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MusicRecyclerViewAdapter.MusicRecyclerViewViewHolder holder, int position) {
        final Song song = songList.get(position);
        String title = song.getTitle();
        String singer = song.getSinger();
        holder.musicTitle.setText(title);
        holder.musicSinger.setText(singer);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class MusicRecyclerViewViewHolder extends RecyclerView.ViewHolder{
        TextView musicTitle, musicSinger;
        public MusicRecyclerViewViewHolder(@NonNull View itemView) {
            super(itemView);
            musicTitle = itemView.findViewById(R.id.text_title);
            musicSinger = itemView.findViewById(R.id.text_singer);
        }
    }
}
