package com.streamthis.youtubeaudio;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.streamthis.TinyDB;
import com.streamthis.YouTubeMetaDataCrawler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.streamthis.R;
import com.streamthis.SavedVideos;
import com.streamthis.YoutubeVideos;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
@SuppressLint("Registered")
public class SearchTube extends AppCompatActivity {
    List<String> getresults = new ArrayList<>();
    RecyclerView shimmer_recycler_view;
    public static List<YoutubeVideos> savedvideos = new ArrayList<>();
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    com.streamthis.youtubeaudio.YoutubeAdapter adapter;
    ListView list_search;
    SearchView   searchView;
    List<YoutubeVideos> youtubeVideosList = new ArrayList<>();
    LottieAnimationView lottieAnimationView;
    @SuppressLint({"CutPasteId", "NewApi", "CommitPrefEdits"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tube);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Dialog d = new Dialog(SearchTube.this, R.style.FullscreenTheme);
            Objects.requireNonNull(d.getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            d.setContentView(R.layout.fragment_saved_videos);
            RecyclerView recyclerView = d.findViewById(R.id.savedvideos);
            if(getSharedPreferences("list",0).getString("list",null)!=null){
                try {
                    List<YoutubeVideos> savedlist = new ArrayList<>();
                    savedlist=new Gson().fromJson(getSharedPreferences("list",0).getString("list","null"), new TypeToken<List<YoutubeVideos>>(){}.getType());
                    com.streamthis.youtubeaudio.SavedVideoAdapter savedvideoadapter = new com.streamthis.youtubeaudio.SavedVideoAdapter(this,savedlist,getApplication(),this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.setAdapter(savedvideoadapter);
                    savedvideoadapter.notifyDataSetChanged();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            d.show();
        });
        getWindow().setStatusBarColor(ContextCompat.getColor(SearchTube.this ,R.color.black));
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#66000000")));
        list_search= findViewById(R.id.list_search);
        lottieAnimationView = findViewById(R.id.loader_lottie);
        lottieAnimationView.animate();
        getSupportActionBar().setTitle("");  // provide compatibility to all the versions
        Toast.makeText(this, "Developed By Vaibhav Kamani", Toast.LENGTH_SHORT).show();
        shimmer_recycler_view = findViewById(R.id.shimmer_recycler_view);
        shimmer_recycler_view.animate();
        @SuppressLint("WrongConstant") LinearLayoutManager manager = new LinearLayoutManager(SearchTube.this, OrientationHelper.VERTICAL, false);
        shimmer_recycler_view.setLayoutManager(manager);
        adapter = new YoutubeAdapter(SearchTube.this, youtubeVideosList, getApplication(), SearchTube.this);
        shimmer_recycler_view.setAdapter(adapter);

    }

    private void setyoutubeadapter(Object data) {

    }

    @SuppressLint("NewApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        list_search= findViewById(R.id.list_search);
        // Retrieve the SearchView and plug it into SearchManager
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setMaxWidth(Integer.MAX_VALUE);//set search menu as full width
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                youtubeVideosList.clear();
                hideKeyboard(SearchTube.this);
                searchyoutube(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                youtubeVideosList.clear();
                adapter.notifyDataSetChanged();
                Thread thread = new Thread(new Runnable() {
                   @Override
                   public void run() {
                       try {
                           getresults = autocompleteResults(newText);
                           SearchTube.this.runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   if(lottieAnimationView.getVisibility()==View.VISIBLE){lottieAnimationView.setVisibility(View.GONE);}
                                   String[] names =getresults.toArray(new String[0]);
                                   ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchTube.this, android.R.layout.simple_list_item_1, android.R.id.text1, names);
                                   adapter.notifyDataSetChanged();
                                   list_search.setAdapter(adapter);
                                   list_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                       @Override
                                       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                           searchyoutube(getresults.get(position));
                                           list_search.setAdapter(null);
                                           hideKeyboard(SearchTube.this);
                                       }
                                   });
                                   list_search.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                       @Override
                                       public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                           hideKeyboard(SearchTube.this);
                                           searchyoutube(getresults.get(position));
                                           searchView.clearFocus();
                                           searchView.setQuery(getresults.get(position),false);
                                           searchView.clearFocus();
                                           list_search.setAdapter(null);
                                       }

                                       @Override
                                       public void onNothingSelected(AdapterView<?> parent) {

                                       }
                                   });

                               }
                           });
                       } catch (IOException e) {
                           e.printStackTrace();
                       }



                   }
               });
                thread.start();
                return false;
            }


        });
        return true;
    }
    public void searchyoutube(String query){
        youtubeVideosList.clear();
        list_search.setAdapter(null);
        try {
            new Thread() {

                public void run() {

                    try {
                        Document doc1 = Jsoup.connect("https://www.youtube.com/results?search_query=" + query)
                                .data("search_query", query)
                                .get();
                        for (Element a : doc1.select(".yt-lockup-title > a[title]")) {
                            String videourl = "https://www.youtube.com/watch?v=" + getYoutubeVideoIdFromUrl(a.attr("href"));
                            Element script = doc1.select("script").first();  //to get the script content
                            Pattern p = Pattern.compile("\"title\":\"(.+?)\""); // Regex for the getting the string: "title":"blah blah blah"
                            Matcher m = p.matcher(script.html());
                            Document doc = Jsoup.connect(videourl).ignoreContentType(false).get();
                            YouTubeMetaDataCrawler ymc = new YouTubeMetaDataCrawler();
                            if(doc.body().hasText()){
                                try {
                                    if(ymc.getTitle(doc)==null){
                                        continue;
                                    }
                                    if(ymc.getTitle(doc).length()==0 ){
                                        continue;
                                    }
                                    YoutubeVideos videos = new YoutubeVideos();
                                    videos.id = ymc.getVideoId(videourl);
                                    videos.image = getYoutubeThumbnailUrlFromVideoUrl(videourl);
                                    videos.info = ymc.getViews(doc);;
                                    videos.views = ymc.getViews(doc);;
                                    videos.title = ymc.getTitle(doc);;
                                    videos.subscribed = String.valueOf(ymc.getPeopleSubscribed(doc));
                                    videos.Url = videourl;

                                    youtubeVideosList.add(videos);
                                    SearchTube.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            lottieAnimationView.setVisibility(View.GONE);
                                            adapter.notifyItemRangeInserted(adapter.getItemCount(), youtubeVideosList.size() - 1);
                                        }
                                    });
                                }
                                catch (NullPointerException e){
                                    e.printStackTrace();
                                }
                            }



                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static ArrayList<String> autocompleteResults(String query)
            throws IOException, UnsupportedEncodingException, PatternSyntaxException {
        String url = "https://clients1.google.com/complete/search?client=youtube&hl=en&gs_rn=64&gs_ri=youtube&ds=yt&cp=10&gs_id=b2&q=";
        String re = "\\[\"(.*?)\",";

        Connection.Response resp = Jsoup.connect(url + URLEncoder.encode(query, "UTF-8")).execute();
        Matcher match = Pattern.compile(re, Pattern.DOTALL).matcher(resp.body());

        ArrayList<String> data = new ArrayList<String>();
        while (match.find()) {
            data.add(match.group(1));
        }
        return data;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public static String extractVideoId(String youtubeUrl) {
        String videoId = null;

        try {
            Document videoPage = Jsoup.connect(youtubeUrl).get();

            Element videoIdMeta = videoPage.select("div[itemtype=http://schema.org/VideoObject] meta[itemprop=videoId]").first();
            if (videoIdMeta == null) {
                throw new IOException("Unable to find videoId in HTML content.");
            }

            videoId = videoIdMeta.attr("content");
        } catch (Exception e) {
            e.printStackTrace(); // alternatively you may log this exception...
        }

        return videoId;
    }
    public static String getYoutubeThumbnailUrlFromVideoUrl(String videoUrl) {
        return "http://img.youtube.com/vi/"+getYoutubeVideoIdFromUrl(videoUrl) + "/0.jpg";
    }

    public static String getYoutubeVideoIdFromUrl(String inUrl) {
        inUrl = inUrl.replace("&feature=youtu.be", "");
        if (inUrl.toLowerCase().contains("youtu.be")) {
            return inUrl.substring(inUrl.lastIndexOf("/") + 1);
        }
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(inUrl);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }


    public static Bitmap retriveVideoFrameFromVideo(String videoPath)throws Throwable
    {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)"+ e.getMessage());
        }
        finally
        {
            if (mediaMetadataRetriever != null)
            {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

}
