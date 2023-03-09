package com.example.mymusicapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
        String imageUrl = "https://disk.mediaindonesia.com/files/news/2022/12/30/WhatsApp%20Image%202022-12-22%20at%2017.07.10%20(1).jpg";
        holder.musicTitle.setText(title);
        holder.musicSinger.setText(singer);
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .apply(new RequestOptions().fitCenter())
                .into(holder.albumImage);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class MusicRecyclerViewViewHolder extends RecyclerView.ViewHolder{
        TextView musicTitle, musicSinger;
        ImageView albumImage;
        public MusicRecyclerViewViewHolder(@NonNull View itemView) {
            super(itemView);
            musicTitle = itemView.findViewById(R.id.text_title);
            musicSinger = itemView.findViewById(R.id.text_singer);
            albumImage = itemView.findViewById(R.id.album_image);
        }
    }
}
