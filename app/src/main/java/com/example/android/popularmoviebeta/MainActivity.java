package com.example.android.popularmoviebeta;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;

import com.example.android.popularmoviebeta.Data.MoviesContract;
import com.example.android.popularmoviebeta.Utilities.MoviesJsonUtils;
import com.example.android.popularmoviebeta.Utilities.NetworkUtils;
import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    // Number of movies that will be inserted to the view holder
    private static final int NUM_OF_MOVIES = 25;

    // Number of span in grid
    private static final int NUM_OF_SPAN = 4;

    // Movie Adapter
    private MovieAdapter mMovieAdapter;

    // Recycler View of the Grid Layout
    private RecyclerView mMovieList;

    // Database that will be used to grab data
    SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get a reference to the recycler view in activity_main.xml
        mMovieList = findViewById(R.id.rv_movies);

        // Initialize the layout needed to be for the main activity to Grid layout
        GridLayoutManager gridLayoutManager = new
                GridLayoutManager(this, NUM_OF_SPAN,
                LinearLayoutManager.VERTICAL, false);

        // Set the layout manger of recycler view to grid
        mMovieList.setLayoutManager(gridLayoutManager);

        // Instantiate a new adapter to fill out data from SQLite database
        mMovieAdapter = new MovieAdapter(NUM_OF_MOVIES, this);

        // Connect the recycler view with the adapter
        mMovieList.setAdapter(mMovieAdapter);

        /*
         * Starts the url request to populate database
         */
        // Grab the url needed
        URL url = NetworkUtils.buildUrlPopular();

        // Run the Networking HTTPs on the background thread using AsyncTask
        MoviesAsyncTask asyncTask = new MoviesAsyncTask(this);
        asyncTask.execute(url);

    }

    /**
     * This inner class will perform AsyncTask in the background for HTTP Request to API
     */
    public static class MoviesAsyncTask extends AsyncTask<URL, Void, String> {

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
        protected String doInBackground(URL... urls) {

            // Grab the url
            URL url = urls[0];

            // Try getting response from HTTP and return result as ArrayList<Movies>
            try {
                String httpEntireResults = NetworkUtils.getResponseFromHttpURL(url);
                Log.v("testing",httpEntireResults);

                // Grab the content values after JSON Parsing
                ContentValues[] movieValues = MoviesJsonUtils
                        .getMoviesContentValuesFromJson(httpEntireResults);

                // Bulk insert the content values to the database
                // Make sure that the movieValues are not empty
                if(movieValues.length != 0 && movieValues != null) {

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
                            movieValues);

                }

                return httpEntireResults;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
