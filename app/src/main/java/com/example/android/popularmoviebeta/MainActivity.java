package com.example.android.popularmoviebeta;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.example.android.popularmoviebeta.Utilities.NetworkUtils;
import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
