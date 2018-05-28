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
    // TODO (1) PLEASE ENTER YOUR MOVIE DB API_KEY HERE
    private static final String API_KEY = "";

    /* API URL FORMATTING */
    private static final String VERSION = "3"; // Current API version available
    private static final String POPULAR_PATH = "popular"; // Popular Path
    private static final String MOVIE_PATH = "movie"; // Shows Movie Path
    private static final String HIGHEST_RATED_PATH = "top_rated"; // Top Rated Path
    private static final String API_KEY_PARAM = "api_key";
    private static final String LANGUAGE_PARAM = "language";
    private static final String LANGUAGE = "en-US"; // Show in English
    private static final String PAGE_PARAM = "page";
    private static final String PAGE_NUMBER = "1"; // Number of page shows

    // Base URL for movieDB API
    private static final String BASE_URL_MOVIE = "https://api.themoviedb.org";

    /**
     * Build the URL needed to grab popular movies JSON data from movieDB API.
     * @return the newly built URL
     */
    public static URL buildUrlPopular() {

        // Build URI for http request
        Uri uriBuilt = Uri.parse(BASE_URL_MOVIE).buildUpon()
                .appendPath(VERSION)
                .appendPath(MOVIE_PATH)
                .appendPath(POPULAR_PATH)
                .appendQueryParameter(API_KEY_PARAM,API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE)
                .appendQueryParameter(PAGE_PARAM, PAGE_NUMBER)
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
     * Build the URL needed to grab highest rated movies JSON data from movieDB API.
     * @return the newly built URL
     */
    public static URL buildUrlHighestRated() {

        // Build URI for http request
        Uri uriBuilt = Uri.parse(BASE_URL_MOVIE).buildUpon()
                .appendPath(VERSION)
                .appendPath(MOVIE_PATH)
                .appendPath(HIGHEST_RATED_PATH)
                .appendQueryParameter(API_KEY_PARAM,API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE)
                .appendQueryParameter(PAGE_PARAM, PAGE_NUMBER)
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
