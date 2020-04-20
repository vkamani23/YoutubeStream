package com.streamthis.youtubeaudio;

import  android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.streamthis.TinyDB;
import com.google.android.material.button.MaterialButton;
import com.streamthis.R;
import com.streamthis.WatchVideoActivity;
import com.streamthis.YoutubeVideos;
import com.streamthis.listofvideos;
import java.util.ArrayList;
import java.util.List;
public class SavedVideoAdapter extends RecyclerView.Adapter<SavedVideoAdapter.myviewholder> {
    Context context;
    List<YoutubeVideos> list = new ArrayList<>();
    Application application;
    AppCompatActivity appCompatActivity;
    public SavedVideoAdapter(Context context, List<YoutubeVideos> list, Application application, AppCompatActivity activity) {
        this.context = context;
        this.list = list;
        this.application = application;
        this.appCompatActivity=activity;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.youtuberow, parent, false);
        myviewholder vh = new myviewholder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, final int position) {
        try {
            Glide.with(context)
                    .load(list.get(position).image)
                    .centerCrop()
                    .into(holder.yt_thumbnail)

            ;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WatchVideoActivity.class);
            intent.putExtra("videoid",list.get(position).id);
            intent.putExtra("videoname",list.get(position).title);
            intent.putExtra("duration",list.get(position).Duration);
            String listofvids = new Gson().toJson(list);
            intent.putExtra("list",listofvids);
            context.startActivity(intent);
        });
        holder.info.setText(list.get(position).info);
        holder.views.setText(list.get(position).views);
        holder.video_title.setText(list.get(position).title);
        holder.swipereveallayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, WatchVideoActivity.class);
            intent.putExtra("videoid",list.get(position).id);
            intent.putExtra("videoname",list.get(position).title);
            intent.putExtra("duration",list.get(position).Duration);
            String listofvids = new Gson().toJson(list);
            intent.putExtra("list",listofvids);
            context.startActivity(intent);

        });
        holder.save.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class myviewholder extends RecyclerView.ViewHolder{
        MaterialButton watch,save;
        CardView swipereveallayout;
        ImageView yt_thumbnail;
        TextView video_title,views,info;
        public myviewholder(@NonNull View itemView) {
            super(itemView);
            yt_thumbnail= itemView.findViewById(R.id.yt_thumbnail);
            watch=itemView.findViewById(R.id.watch);
            swipereveallayout= itemView.findViewById(R.id.card);
            save= itemView.findViewById(R.id.save);
            video_title= itemView.findViewById(R.id.video_title);
            views=itemView.findViewById(R.id.views);
            info= itemView.findViewById(R.id.info);
        }
    }
}
