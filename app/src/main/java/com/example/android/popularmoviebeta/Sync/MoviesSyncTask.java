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

import javax.net.ssl.HttpsURLConnection;

public class MoviesSyncTask {

    /**
     * Sync database to today's data for popular and highest rated movies
     * @param context       Application context
     */
    public static void syncTopRatedMovies(@NonNull final Context context) {
        // Run the Networking HTTPs on the background thread using AsyncTask
        TopRatedMoviesAsyncTask asyncTask = new TopRatedMoviesAsyncTask(context);

        // Insert all of the API urls to run http request
        URL topRatedUrl = NetworkUtils.buildUrlHighestRated();

        // Run Asynctask For HTTP Request
        asyncTask.execute(topRatedUrl);
    }

    /**
     * Sync database to today's data for popular movies
     * @param context       Application context
     */
    public static void syncPopularMovies(@NonNull final Context context) {

        // Run the popular movie API using asynctask
        PopularMovieAsyncTask popularMovieAsyncTask = new PopularMovieAsyncTask(context);

        // Build popular API URL
        URL popularUrl = NetworkUtils.buildUrlPopular();

        // Run Asynctask For HTTP Request
        popularMovieAsyncTask.execute(popularUrl);

    }

    /**
     * This inner class will perform AsyncTask in the background for Popular API Request
     */
    public static class PopularMovieAsyncTask extends AsyncTask<URL, Void, Void> {

        private Context mContext;

        /**
         * Constructor that will initiated the context
         * @param context
         */
        public PopularMovieAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(URL... urls) {

            // Grab the popular url
            URL popularUrl = NetworkUtils.buildUrlPopular();

            // Try getting response from HTTP and insert data to appropriate table
            try {

                // Sync the top rated movies database with themoviedb API
                String httpPopularResults = NetworkUtils.getResponseFromHttpURL(popularUrl);
                Log.v("Top Rated Movie HTTP Response: ", httpPopularResults);

                // Grab the content values after JSON Parsing
                ContentValues[] popularMovieValues = MoviesJsonUtils
                        .getTopRatedMoviesContentValuesFromJson(httpPopularResults);

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
                    System.out.println("INSERT HERE: " + MoviesContract.PopularMovie.CONTENT_URI);

                    popularMoviesContentResolver.bulkInsert(MoviesContract.PopularMovie.CONTENT_URI,
                            popularMovieValues);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * This inner class will perform AsyncTask in the background for TOP RATED API Request
     */
    public static class TopRatedMoviesAsyncTask extends AsyncTask<URL, Void, Void> {

        // Set current context
        private Context mContext;

        /**
         * Constructor that will initiated the context
         * @param context
         */
        TopRatedMoviesAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(URL... urls) {

            // Grab the popular url
            URL topRatedUrl = urls[0];

            // Try getting response from HTTP and insert data to appropriate table
            try {

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
                    System.out.println("INSERT HERE: " + MoviesContract.HighestRatedMovie.CONTENT_URI);

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
