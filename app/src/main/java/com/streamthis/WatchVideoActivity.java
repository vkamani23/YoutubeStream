package com.streamthis;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.Environment;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Rational;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker;

import org.jsoup.nodes.Document;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import jp.wasabeef.recyclerview.animators.FadeInDownAnimator;

public class WatchVideoActivity extends AppCompatActivity {
    com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView youTubePlayerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video);
          youTubePlayerView = findViewById(R.id.player);
          String list =getIntent().getStringExtra("list");
          List<YoutubeVideos> videos = new Gson().fromJson(list, new TypeToken<List<YoutubeVideos>>(){}.getType());
          youTubePlayerView.enableBackgroundPlayback(true);
          youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
              @Override
              public void onApiChange(YouTubePlayer youTubePlayer) {
                  super.onApiChange(youTubePlayer);
              }

              @Override
              public void onCurrentSecond(YouTubePlayer youTubePlayer, float second) {
                  if(new YouTubePlayerTracker().getVideoDuration()==second-1){
                      videos.remove(0);
                      if(videos.size()>0){
                          youTubePlayer.loadVideo(videos.get(0).id,0);
                      }

                  }
              }

              @Override
              public void onError(YouTubePlayer youTubePlayer, PlayerConstants.PlayerError error) {
                  super.onError(youTubePlayer, error);
              }

              @Override
              public void onPlaybackQualityChange(YouTubePlayer youTubePlayer, PlayerConstants.PlaybackQuality playbackQuality) {
                  super.onPlaybackQualityChange(youTubePlayer, playbackQuality);
              }

              @Override
              public void onPlaybackRateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlaybackRate playbackRate) {
                  super.onPlaybackRateChange(youTubePlayer, playbackRate);
              }

              @Override
              public void onReady(YouTubePlayer youTubePlayer) {
                  super.onReady(youTubePlayer);
                  String videoId = getIntent().getStringExtra("videoid");
                  youTubePlayer.loadVideo(videoId, 0);

              }

              @Override
              public void onStateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlayerState state) {
                  super.onStateChange(youTubePlayer, state);
              }

              @Override
              public void onVideoDuration(YouTubePlayer youTubePlayer, float duration) {
                  super.onVideoDuration(youTubePlayer, duration);
              }

              @Override
              public void onVideoId(YouTubePlayer youTubePlayer, String videoId) {
                  super.onVideoId(youTubePlayer, videoId);
              }

              @Override
              public void onVideoLoadedFraction(YouTubePlayer youTubePlayer, float loadedFraction) {
                  super.onVideoLoadedFraction(youTubePlayer, loadedFraction);
              }
          });

    }

    @Override
    protected void onPause() {
        super.onPause();}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
