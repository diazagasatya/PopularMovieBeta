package com.example.android.popularmoviebeta.Sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.android.popularmoviebeta.Data.MoviesContract;
import com.example.android.popularmoviebeta.Utilities.MoviesJsonUtils;
import com.example.android.popularmoviebeta.Utilities.NetworkUtils;

import java.net.URL;

public class MoviesSyncTask {

    /**
     * Sync database to today's data for popular and highest rated movies
     * @param context       Application context
     */
    public static void syncMovies(@NonNull final Context context) {

        // Check INTERNET Access first
        if(!checkInternetConnection(context)) {
            System.out.println("NO INTERNET CONNECTION!");
            Toast.makeText(context,
                    "No Internet Connection",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Run the Asynctask that will request the HTTP API from moviedb
        MoviesAsyncTask asyncTask = new MoviesAsyncTask(context);

        // Build the url for API request to moviedb
        URL popularMovieUrl = NetworkUtils.buildUrlPopular();
        URL topRatedUrl = NetworkUtils.buildUrlHighestRated();

        final int NUMBER_OF_API_REQUESTS = 2;

        // Insert the API url into an array of urls passed in to the asynctask
        URL[] urls = new URL[NUMBER_OF_API_REQUESTS];

        urls[0] = popularMovieUrl;
        urls[1] = topRatedUrl;

        asyncTask.execute(urls);
    }

    /**
     * This function will check internet connection first before asking
     * for a HTTP Request, and will return a boolean.
     * @return boolean
     */
    public static boolean checkInternetConnection(Context context) {

        boolean internetConnection = false;

        // Get connectivity service
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get Network info
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // Return the boolean value of the connection
        if(networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            internetConnection = true;
        }

        return internetConnection;
    }

    /**
     * Sync the database with the current popular and highest rated movie from moviedb API
     */
    public static class MoviesAsyncTask extends AsyncTask<URL, Void, Void> {

        private Context mContext;

        public MoviesAsyncTask(@NonNull final Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(URL... urls) {

            // Grab the urls for http request
            URL popularUrl = urls[0];
            URL topRatedUrl = urls[1];


            // Try getting response from HTTP and insert data to appropriate table
            try {

                // Sync the Popular movies database with themoviedb API
                String httpPopularResults = NetworkUtils.getResponseFromHttpURL(popularUrl);
                Log.v("Popular Movie HTTP Response: ", httpPopularResults);

                // Grab the content values after JSON Parsing
                ContentValues[] popularMovieValues = MoviesJsonUtils
                        .getPopularMoviesContentValuesFromJson(httpPopularResults);

                // Bulk insert the content values to the database
                // Make sure that the movieValues are not empty
                if(popularMovieValues.length != 0 && popularMovieValues != null) {

                    // Get a handle to the content resolver to insert values
                    ContentResolver popularMoviesContentResolver = mContext.getContentResolver();


                    /*
                     * Delete old data because every day movies popularity/rating changes
                     */
                    popularMoviesContentResolver.delete(MoviesContract.PopularMovie.CONTENT_URI,
                            null,
                            null);

                    /*
                     * Insert new movie data based upon current movies popularity/rating
                     */
                    popularMoviesContentResolver.bulkInsert(MoviesContract.PopularMovie.CONTENT_URI,
                            popularMovieValues);

                }

                /*
                 * STARTS SYNC FOR TOP RATED MOVIEs
                 */
                // Sync the top rated movies database with themoviedb API
                String httpTopRatedResults = NetworkUtils.getResponseFromHttpURL(topRatedUrl);
                Log.v("Top Rated Movie HTTP Response: ", httpTopRatedResults);

                // Grab the content values after JSON Parsing
                ContentValues[] topRatedMovieValues = MoviesJsonUtils
                        .getTopRatedMoviesContentValuesFromJson(httpTopRatedResults);

                // Bulk insert the content values to the database
                // Make sure that the movieValues are not empty
                if(topRatedMovieValues.length != 0 && topRatedMovieValues != null) {

                    // Get a handle to the content resolver to insert values
                    ContentResolver topRatedMoviesContentResolver = mContext.getContentResolver();

                    /*
                     * Delete old data because every day movies popularity/rating changes
                     */
                    topRatedMoviesContentResolver.delete(MoviesContract.HighestRatedMovie.CONTENT_URI,
                            null,
                            null);

                    /*
                     * Insert new movie data based upon current movies popularity/rating
                     */
                    topRatedMoviesContentResolver.bulkInsert(MoviesContract.HighestRatedMovie.CONTENT_URI,
                            topRatedMovieValues);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
