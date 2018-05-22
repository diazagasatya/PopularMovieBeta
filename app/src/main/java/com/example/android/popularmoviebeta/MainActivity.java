package com.example.android.popularmoviebeta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.example.android.popularmoviebeta.Data.MoviesContract;
import com.example.android.popularmoviebeta.Sync.MoviesSyncTask;


public class MainActivity extends AppCompatActivity
implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener{

    // Number of span in grid
    private static final int NUM_OF_SPAN = 2;

    // Movie Adapter
    private MovieAdapter mMovieAdapter;

    // Recycler View of the Grid Layout
    private RecyclerView mMovieList;
    // Set initial position to -1
    private int mPosition = RecyclerView.NO_POSITION;

    // Use a integer id for loading Movie data from API
    private static final int ID_POPULAR_MOVIE_LOADER = 100;
    private static final int ID_TOP_RATED_MOVIE_LOADER = 200;

    // Use for reference in adapter
    public static int tableId = ID_POPULAR_MOVIE_LOADER;

    // Projection of columns that will be needed to retrieve in loader
    public static final String[] POPULAR_MOVIE_PROJECTION = {
            MoviesContract.PopularMovie.COL_MOVIE_POSTER,
            MoviesContract.PopularMovie.COL_ORIGINAL_TITLE
    };
    public static final String[] TOP_RATED_MOVIE_PROJECTION = {
            MoviesContract.HighestRatedMovie.COL_MOVIE_POSTER,
            MoviesContract.HighestRatedMovie.COL_ORIGINAL_TITLE
    };

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

        // Will not change the child layout in the RecyclerView
        mMovieList.setHasFixedSize(true);

        // Instantiate a new adapter to fill out data from SQLite database
        mMovieAdapter = new MovieAdapter(this);

        // Connect the recycler view with the adapter
        mMovieList.setAdapter(mMovieAdapter);

        // Get the default Shared Preference to initialize the appropriate loader
        setupSharedPreference();

        // Sync the movie database
        MoviesSyncTask.syncPopularMovies(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister the shared preference to start from default after termination
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * This will initialize the default preference of the main activity & react to changes
     */
    public void setupSharedPreference() {

        // Get the default shared preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // The default loader will be loading the popular movie from database
        getSupportLoaderManager().initLoader(ID_POPULAR_MOVIE_LOADER, null, this);

        // Register the shared preference listener to change Loader id if necessary
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * When loader is initiated, it will load the appropriate cursor from content resolver
     * For this project, main activity will only have to load either Popular Movie Table
     * or the Top Rated Table
     * @param loaderId          The id of the loader
     * @param args              Bundle that is saved in the lifecycle
     * @return CursorLoader
     */
    @NonNull
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        System.out.println("CALL HERE WOOHOO LOADER ID : " + loaderId);

        if(loaderId == ID_POPULAR_MOVIE_LOADER) {

            System.out.println("Popular Movie Loader");

            // Get the popular movie content URI from contract
            Uri popularMovieUri = MoviesContract.PopularMovie.CONTENT_URI;

            return new CursorLoader(this,
                    popularMovieUri,
                    POPULAR_MOVIE_PROJECTION,
                    null,
                    null,
                    null);

        } else if (loaderId == ID_TOP_RATED_MOVIE_LOADER) {

            System.out.println("TOP RATED LOADER");

            // Get the popular movie content URI from contract
            Uri topRatedMovieUri = MoviesContract.HighestRatedMovie.CONTENT_URI;

            return new CursorLoader(this,
                    topRatedMovieUri,
                    TOP_RATED_MOVIE_PROJECTION,
                    null,
                    null,
                    null);
        } else {
            throw new RuntimeException("Loader not implemented " + loaderId);
        }
    }

    /**
     * After loading CursorLoader, swap the old Cursor to the new Cursor with data from
     * Content Resolver, this way the new updated data will be inflated to UI
     * @param loader            Loader object
     * @param data              Data from Content Resolver
     */
    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<Cursor> loader, Cursor data) {

        // Pass in the new cursor after loading query
        mMovieAdapter.swapCursor(data, tableId);

        // Initialize position to 0
        if(mPosition == RecyclerView.NO_POSITION) {
            mPosition = 0;
        }

        // Set the recycler view to scroll smoothly
        mMovieList.smoothScrollToPosition(mPosition);

        // For starting the application with default sorting movies
        if(data.getCount() == 0) {
            getSupportLoaderManager().restartLoader(ID_POPULAR_MOVIE_LOADER, null,this);
        }
    }

    /**
     * If loader is reset, erase the data in cursor and load new data.
     * @param loader        Loader object
     */
    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<Cursor> loader) {
        // Reset the cursor to null and clears the data in the cursor
        mMovieAdapter.swapCursor(null, tableId);
    }


    /**
     * This will inflate the sort_by.xml layout to change movie sorting
     * @param menu      Menu layout
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Use menu inflater to inflate the menu
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.setting_menu,menu);
        return true;

    }

    /**
     * This will link the item clicked on the options menu to the appropriate
     * activity
     * @param item      item that was clicked in options menu
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Get the id of the item that was clicked
        int itemClicked = item.getItemId();

        // Use an if statement for now as it only offers one item
        if(itemClicked == R.id.action_settings) {
            // Initiate the intent to go to settings activity
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        String sortByPopularity = getResources().getStringArray(R.array.sort_by_values)[0];
        String sortByRating = getResources().getStringArray(R.array.sort_by_values)[1];

        System.out.println("HERE1 : " + sortByPopularity + " " + sortByRating);
        System.out.println("HERE2 : " + key);
        System.out.println("here3 : " + sharedPreferences.getString(key,
                sortByPopularity));
        System.out.println("key: " + R.string.sort_by_key);

        // Check which preference have changed from the String value
        if (key.equals(getString(R.string.sort_by_key))) {

            /*
             * Get the string value of the changed sort by preference
             * The default value is sorting by popularity
             */
            String clickedSortingPreference = sharedPreferences.getString(key,
                    sortByPopularity);

            System.out.println("CHANGE TO : " + clickedSortingPreference);

            // Load the appropriate loader ID based upon preference changed
            if (clickedSortingPreference.equals(sortByPopularity)) {

                // Set the popular table id to notify adapter
                tableId = ID_POPULAR_MOVIE_LOADER;

                // Set the top rated table to notify adapter
                MoviesSyncTask.syncPopularMovies(this);

                getSupportLoaderManager()
                        .initLoader(ID_POPULAR_MOVIE_LOADER, null, this);

                System.out.println("popular chosen");

            } else if (clickedSortingPreference.equals(sortByRating)) {

                System.out.println("REACH PREFERENCE");

                // Set the top rated loader ID based upon preference changed
                tableId = ID_TOP_RATED_MOVIE_LOADER;

                // Set the top rated table to notify adapter
                MoviesSyncTask.syncTopRatedMovies(this);

                getSupportLoaderManager()
                        .initLoader(ID_TOP_RATED_MOVIE_LOADER, null, this);

                System.out.println("highest rated chosen");
            }
        } else {
            System.out.println("not equal");
        }
    }
}
