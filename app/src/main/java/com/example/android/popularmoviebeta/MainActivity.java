package com.example.android.popularmoviebeta;

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

import com.example.android.popularmoviebeta.Data.MoviesContract;
import com.example.android.popularmoviebeta.Sync.MoviesSyncTask;


public class MainActivity extends AppCompatActivity
implements LoaderManager.LoaderCallbacks<Cursor>{

    // Number of span in grid
    private static final int NUM_OF_SPAN = 2;

    // Movie Adapter
    private MovieAdapter mMovieAdapter;

    // Recycler View of the Grid Layout
    private RecyclerView mMovieList;
    // Set initial position to -1
    private int mPosition = RecyclerView.NO_POSITION;

    // Use a integer id for loading Movie data from API
    private static final int ID_POPULAR_MOVIE_LOADER = 22;
    private static final int ID_TOP_RATED_MOVIE_LOADER = 33;

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

        getSupportLoaderManager().initLoader(ID_POPULAR_MOVIE_LOADER, null, this);

        MoviesSyncTask.syncMovies(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @NonNull
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        // Query the appropriate setting of movies (popular/top_rated)
        switch(loaderId) {

            // Case of popular movie requested
            case ID_POPULAR_MOVIE_LOADER:

                // Get the popular movie content URI from contract
                Uri popularMovieUri = MoviesContract.PopularMovie.CONTENT_URI;

                return new CursorLoader(this,
                        popularMovieUri,
                        POPULAR_MOVIE_PROJECTION,
                        null,
                        null,
                        null);

            case ID_TOP_RATED_MOVIE_LOADER:

                // Get the popular movie content URI from contract
                Uri topRatedMovieUri = MoviesContract.HighestRatedMovie.CONTENT_URI;

                return new CursorLoader(this,
                        topRatedMovieUri,
                        TOP_RATED_MOVIE_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader not implemented " + loaderId);

        }
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<Cursor> loader, Cursor data) {

        // Pass in the new cursor after loading query
        mMovieAdapter.swapCursor(data);

        // Initialize position to 0
        if(mPosition == RecyclerView.NO_POSITION) {
            mPosition = 0;
        }

        // Set the recycler view to scroll smoothly
        mMovieList.smoothScrollToPosition(mPosition);
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<Cursor> loader) {
        // Reset the cursor to null
        mMovieAdapter.swapCursor(null);
    }

}
