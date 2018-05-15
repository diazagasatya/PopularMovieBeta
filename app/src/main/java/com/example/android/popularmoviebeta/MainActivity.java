package com.example.android.popularmoviebeta;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;

import com.example.android.popularmoviebeta.Utilities.NetworkUtils;
import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final int NUM_OF_MOVIES = 20;
    private static final int NUM_OF_SPAN = 4;
    private MovieAdapter mMovieAdapter;
    private RecyclerView mMovieList;

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
           Starts the url request to populate database
         */
        // Grab the url needed
        URL url = NetworkUtils.buildURL(getString(R.string.popularity_desc));

        // Run the Networking HTTPs on the background thread using AsyncTask
        MoviesAsyncTask asyncTask = new MoviesAsyncTask();
        asyncTask.execute(url);

    }

    /**
     * This inner class will perform AsyncTask in the background for HTTP Request to API
     */
    public static class MoviesAsyncTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {

            // Grab the url
            URL url = urls[0];

            // Try getting response from HTTP and return result as ArrayList<Movies>
            try {
                String httpEntireResults = NetworkUtils.getResponseFromHttpURL(url);
                Log.v("testing",httpEntireResults);

                return httpEntireResults;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
