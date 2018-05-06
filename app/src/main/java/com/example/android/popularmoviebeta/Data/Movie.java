package com.example.android.popularmoviebeta.Data;

import android.provider.BaseColumns;

/**
 * Created by diazagasatya on 5/5/18.
 */

public class Movie {

    /* Initiate private variables for every movie */
    private String title;
    private String originalTitle;
    private String imagePath;
    private String plotSynopsis;
    private String rating;
    private String releaseDate;
    private final static String BASE_URL_IMAGE = "http://image.tmdb.org/t/p/w185/";


    /**
     * Constructor taking full parameters at JSONParsings
     * @param mvTitle       Movie Title
     * @param mvOriTitle    Original Title
     * @param imgPath       Image Path with BASE URL
     * @param overview      Movie Plot Synopsis
     * @param rate          Movie Average Rating
     * @param date          Movie Released Date
     */
    public Movie(String mvTitle, String mvOriTitle, String imgPath,
                 String overview, String rate, String date) {
        setTitle(mvTitle);
        setOriginalTitle(mvOriTitle);
        setImagePath(imgPath);
        setPlotSynopsis(overview);
        setRating(rate);
        setReleaseDate(date);
    }


    /* ========= GETTERS AND SETTERS STARTS HERE =========== */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = BASE_URL_IMAGE + imagePath;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        this.plotSynopsis = plotSynopsis;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
