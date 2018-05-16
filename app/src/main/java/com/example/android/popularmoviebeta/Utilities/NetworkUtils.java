package com.example.android.popularmoviebeta.Utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by diazagasatya on 5/5/18.
 */
public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    // Please insert your MovieDB API KEY here before running
    // This API Key can be obtained by registering to https://developers.themoviedb.org
    private static final String API_KEY = "546ac5863d9083b1cf9d85daabc5446a";

    /* API URL FORMATTING */
    private static final String VERSION = "3"; // Current API version available
    private static final String DISCOVER_PATH = "discover"; // Movie path
    private static final String MOVIE_PATH = "movie"; // Shows now playing
    private static final String API_KEY_PARAM = "api_key";
    private static final String LANGUAGE_PARAM = "language";
    private static final String LANGUAGE = "en-US"; // Show in English
    private static final String SORT_BY = "sort_by";
    private static final String PAGE_PARAM = "page";
    private static final String PAGE_NUMBER = "1"; // Number of page shows
    private static final String RELEASE_YEAR_PARAM = "primary_release_year";
    private static final String RELEASE_YEAR = "2018"; // Show recent movies played
    private static final String SAME_DAY_RELEASE = "primary_release_date";
    private static final String TODAYS_DATE = java.time.LocalDate.now().toString(); // Restrict upcoming movies

    // Base URL for movieDB API
    private static final String BASE_URL_MOVIE = "https://api.themoviedb.org";

    /**
     * Build the URL needed to grab JSON data from movieDB API.
     * @return the newly built URL
     */
    public static URL buildURL(String sortBy) {

        // Build URI for http request
        Uri uriBuilt = Uri.parse(BASE_URL_MOVIE).buildUpon()
                .appendPath(VERSION)
                .appendPath(DISCOVER_PATH)
                .appendPath(MOVIE_PATH)
                .appendQueryParameter(API_KEY_PARAM,API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE)
                .appendQueryParameter(SORT_BY, sortBy)
                .appendQueryParameter(PAGE_PARAM, PAGE_NUMBER)
                .appendQueryParameter(RELEASE_YEAR_PARAM,RELEASE_YEAR)
                .appendQueryParameter(SAME_DAY_RELEASE,TODAYS_DATE)
                .build();

        // Build URL from URI
        URL url = null;

        try {
            url = new URL(uriBuilt.toString());

            // Print the Url for debugging purposes
            Log.v(TAG, url.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This function will get response from HTTP URL & return the entire result
     * @param url Http URL after build URI
     * @return the entire result of http as a String
     * @throws IOException
     */
    public static String getResponseFromHttpURL(URL url) throws IOException {

        // open connection using the url passed
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        // Grab all of the string values from http connection
        try {
            InputStream in = urlConnection.getInputStream();

            // Tokenize String
            Scanner scanner = new Scanner(in);

            // Force the scanner to read the entire input stream
            scanner.useDelimiter("\\A");

            boolean hasInputs = scanner.hasNext();

            if(hasInputs) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {

            // Disconnect from the connection
            urlConnection.disconnect();
        }
    }

}
