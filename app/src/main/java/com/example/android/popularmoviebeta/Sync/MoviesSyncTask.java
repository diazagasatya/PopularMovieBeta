package com.example.android.popularmoviebeta.Sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import com.example.android.popularmoviebeta.Data.MoviesContract;
import com.example.android.popularmoviebeta.Utilities.MoviesJsonUtils;
import com.example.android.popularmoviebeta.Utilities.NetworkUtils;

import java.net.URL;

public class MoviesSyncTask {

    /**
     * Sync database to today's data for popular and highest rated movies
     * @param context
     */
    public static void syncMovies(@NonNull final Context context) {
        // Run the Networking HTTPs on the background thread using AsyncTask
        MoviesAsyncTask asyncTask = new MoviesAsyncTask(context);

        // Number of URL needed to be run in this project
        final int NUMBER_OF_API_URL_IMPLEMENTED = 2;

        // Insert all of the API urls to run http request
        URL[] urls = new URL[NUMBER_OF_API_URL_IMPLEMENTED];
        urls[0] = NetworkUtils.buildUrlPopular();
        urls[1] = NetworkUtils.buildUrlHighestRated();

        asyncTask.execute(urls);
    }

    /**
     * This inner class will perform AsyncTask in the background for HTTP Request to API
     */
    public static class MoviesAsyncTask extends AsyncTask<URL, Void, Void> {

        // Set current context
        private Context mContext;

        /**
         * Constructor that will initiated the context
         * @param context
         */
        MoviesAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(URL... urls) {

            // Grab the popular url
            URL popularUrl = urls[0];
            URL topRatedUrl = urls[1];

            // Try getting response from HTTP and insert data to appropriate table
            try {

                // Sync the popular movies database with themoviedb API
                String httpPopularResults = NetworkUtils.getResponseFromHttpURL(popularUrl);
                Log.v("Popular Movie HTTP Response: ", httpPopularResults);

                System.out.println(httpPopularResults);

                // Grab the content values after JSON Parsing
                ContentValues[] popularMovieValues = MoviesJsonUtils
                        .getPopularMoviesContentValuesFromJson(httpPopularResults);

                // Bulk insert the content values to the database
                // Make sure that the movieValues are not empty
                if(popularMovieValues.length != 0 && popularMovieValues != null) {

                    // Get a handle to the content resolver to insert values
                    ContentResolver moviesContentResolver = mContext.getContentResolver();

                    /*
                     * Delete old data because every day movies popularity/rating changes
                     */
                    moviesContentResolver.delete(MoviesContract.PopularMovie.CONTENT_URI,
                            null,
                            null);

                    /*
                     * Insert new movie data based upon current movies popularity/rating
                     */
                    moviesContentResolver.bulkInsert(MoviesContract.PopularMovie.CONTENT_URI,
                            popularMovieValues);
                }

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
                    ContentResolver moviesContentResolver = mContext.getContentResolver();

                    /*
                     * Delete old data because every day movies popularity/rating changes
                     */
                    moviesContentResolver.delete(MoviesContract.HighestRatedMovie.CONTENT_URI,
                            null,
                            null);

                    /*
                     * Insert new movie data based upon current movies popularity/rating
                     */
                    moviesContentResolver.bulkInsert(MoviesContract.HighestRatedMovie.CONTENT_URI,
                            popularMovieValues);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}
