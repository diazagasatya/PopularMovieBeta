package com.example.android.popularmoviebeta;

import android.content.ContentResolver;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.android.popularmoviebeta.Data.MoviesContract;
import com.example.android.popularmoviebeta.Data.MoviesProvider;
import com.example.android.popularmoviebeta.Sync.MoviesSyncTask;



public class MainActivity extends AppCompatActivity
implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener,
        MovieAdapter.MovieAdapterOnClickHandler{

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

    // Table identification for detail activity
    public static final String TABLE_IDENTIFICATION = "table_identification";

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
        mMovieAdapter = new MovieAdapter(this, this);

        // Connect the recycler view with the adapter
        mMovieList.setAdapter(mMovieAdapter);

        // Get the default Shared Preference to initialize the appropriate loader
        setupSharedPreference();

        // Sync the movie database
        MoviesSyncTask.syncMovies(this);
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

        if(loaderId == ID_POPULAR_MOVIE_LOADER) {

            // Get the popular movie content URI from contract
            Uri popularMovieUri = MoviesContract.PopularMovie.CONTENT_URI;

            return new CursorLoader(this,
                    popularMovieUri,
                    POPULAR_MOVIE_PROJECTION,
                    null,
                    null,
                    null);

        } else if (loaderId == ID_TOP_RATED_MOVIE_LOADER) {

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

    /**
     * Change in sorting preferences setting
     * @param sharedPreferences         Shared preferences
     * @param key                       Sorting key
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        String sortByPopularity = getResources().getStringArray(R.array.sort_by_values)[0];
        String sortByRating = getResources().getStringArray(R.array.sort_by_values)[1];


        // Check which preference have changed from the String value
        if (key.equals(getString(R.string.sort_by_key))) {

            /*
             * Get the string value of the changed sort by preference
             * The default value is sorting by popularity
             */
            String clickedSortingPreference = sharedPreferences.getString(key,
                    sortByPopularity);


            // Load the appropriate loader ID based upon preference changed
            if (clickedSortingPreference.equals(sortByPopularity)) {

                // Set the popular table id to notify adapter
                tableId = ID_POPULAR_MOVIE_LOADER;

                // Set the top rated table to notify adapter
                // MoviesSyncTask.syncPopularMovies(this);

                getSupportLoaderManager()
                        .restartLoader(ID_POPULAR_MOVIE_LOADER, null, this);


            } else if (clickedSortingPreference.equals(sortByRating)) {

                // Set the top rated loader ID based upon preference changed
                tableId = ID_TOP_RATED_MOVIE_LOADER;

                // Set the top rated table to notify adapter
                // MoviesSyncTask.syncTopRatedMovies(this);

                getSupportLoaderManager()
                        .restartLoader(ID_TOP_RATED_MOVIE_LOADER, null, this);
            }
        } else {
            System.out.println("not equal");
        }
    }

    /**
     * This will initiate the intent to start the DetailActivity
     * @param idNumber
     */
    @Override
    public void clickedMovie(int idNumber) {

        // Initiate the intent to detail activity
        Intent movieDetailIntent = new Intent(
                MainActivity.this, DetailActivity.class);

        System.out.println(" ITEM CLICKED: " + idNumber);

        /*
         * Build the URI for creating a query for the clicked movie,
         * based upon current displayed TABLE.
         */
        switch(tableId) {

            case ID_POPULAR_MOVIE_LOADER:

                // Grab Movie ID from table
                Uri popularMovieUri = MoviesContract.PopularMovie.CONTENT_URI;
                String movieId = grabMovieId(popularMovieUri, tableId, idNumber);

                // Initiate MovieSyncTask to sync Review and Trailer
                MoviesSyncTask.syncTrailersAndReview(this, movieId, tableId);

                // Grab the specific movie and set the data of the intent
                Uri moviePopularDetailInformation = MoviesContract
                        .PopularMovie.buildUriWithIdPopular(idNumber);

                // Adding the URI to the intent
                movieDetailIntent.setData(moviePopularDetailInformation);
                movieDetailIntent.putExtra(TABLE_IDENTIFICATION, tableId);

                // Start the activity
                startActivity(movieDetailIntent);

                break;

            case ID_TOP_RATED_MOVIE_LOADER:

                // Grab Movies ID from table
                Uri highestRatedMovieUri = MoviesContract.HighestRatedMovie.CONTENT_URI;
                String movieIdTop = grabMovieId(highestRatedMovieUri, tableId, idNumber);

                // Initiate MovieSyncTask to sync Review & Trailer to add to table
                MoviesSyncTask.syncTrailersAndReview(this, movieIdTop, tableId);

                // Grab the specific movie and set the data of the intent
                Uri movieHighestRatedDetailInformation = MoviesContract
                        .HighestRatedMovie.buildUriWithIdHighestRated(idNumber);

                // Adding the URI to the intent
                movieDetailIntent.setData(movieHighestRatedDetailInformation);
                movieDetailIntent.putExtra(TABLE_IDENTIFICATION, tableId);

                // Start the activity
                startActivity(movieDetailIntent);

                break;

            default:
                Toast.makeText(this,
                        "There's no detail content of the movie",
                        Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This helper method will query the database for the movieId
     * @param uri   BASE CONTENT URI
     * @return Movie Identification
     */
    public String grabMovieId(Uri uri, int tableId, int rowId) {

        switch(tableId) {
            case ID_POPULAR_MOVIE_LOADER:

                // Get the Movie ID from popular table
                String[] STRING_PROJECTION = {
                        MoviesContract.PopularMovie.COL_MOVIE_ID
                };

                // Grab the movie Id using Query method in Content Resolver
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver.
                        query(uri,STRING_PROJECTION,null, null,null);

                String movieId;

                // Grab the Movie Id
                try {

                    // Move the cursor to the clicked movie
                    cursor.moveToPosition(rowId);

                    movieId = cursor.getString(cursor.
                            getColumnIndex(MoviesContract.PopularMovie.COL_MOVIE_ID));

                    cursor.close();

                } catch (Exception e) {

                    Log.v("Movie Id", "Column Index is not available / cursor is null");

                    throw new NullPointerException(e.getMessage());
                }

                return movieId;

            case ID_TOP_RATED_MOVIE_LOADER:

                // Get the Movie ID from highest rated table
                String[] STRING_TOP_PROJECTION = {
                        MoviesContract.HighestRatedMovie.COL_MOVIE_ID
                };

                // Grab the movie Id using Query method in Content Resolver
                ContentResolver contentTopResolver = getContentResolver();
                Cursor cursorTopRated = contentTopResolver.
                        query(uri,STRING_TOP_PROJECTION,null,null,null);

                String movieIdTopRated;

                // Grab the Movie Id
                try {

                    // Move the cursor to the clicked movie
                    cursorTopRated.moveToPosition(rowId);

                    movieIdTopRated = cursorTopRated.getString(cursorTopRated.
                            getColumnIndex(MoviesContract.HighestRatedMovie.COL_MOVIE_ID));

                    cursorTopRated.close();

                } catch (Exception e) {

                    Log.v("Movie Id", "Column Index is not available / cursor is null");

                    throw new NullPointerException(e.getMessage());
                }
                return movieIdTopRated;

            default:

                throw new NullPointerException("table id is incorrect");
        }
    }
}
