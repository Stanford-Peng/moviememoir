package com.example.moviememoir.model;

import android.graphics.Bitmap;

public class SearchedMovie {
    private String movie;
    private Bitmap image;
    private String link;

    public SearchedMovie(String movie, Bitmap image, String link) {
        this.movie = movie;
        this.image = image;
        this.link = link;
    }

    public String getMovie() {
        return movie;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getLink() {
        return link;
    }
}
