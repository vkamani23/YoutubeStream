package com.streamthis;

public class YoutubeVideos {
    public String title;
    public String image;
    public String views;
    public String Duration;
    public String info;
    public String subscribed;
    public String Url;
    public String id;

    public YoutubeVideos(String title, String image, String views, String duration, String info, String subscribed, String url, String id) {
        this.title = title;
        this.image = image;
        this.views = views;
        Duration = duration;
        this.info = info;
        this.subscribed = subscribed;
        Url = url;
        this.id = id;
    }

    public YoutubeVideos() {
    }
}
