package com.example.android.popularmoviebeta;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmoviebeta.Data.MoviesContract;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>,
        TrailersAdapter.TrailerOnClickHandler {

    // The SELECT level for query popular movie & highest rated table
    public static final String[] POPULAR_MOVIE_PROJECTION = {
            MoviesContract.PopularMovie.COL_ORIGINAL_TITLE,
            MoviesContract.PopularMovie.COL_MOVIE_POSTER,
            MoviesContract.PopularMovie.COL_MOVIE_SYNOPSIS,
            MoviesContract.PopularMovie.COL_RATINGS,
            MoviesContract.PopularMovie.COL_RELEASE_DATE,
            MoviesContract.PopularMovie.COL_TRAILERS
    };
    public static final String[] HIGHEST_RATED_PROJECTION = {
            MoviesContract.HighestRatedMovie.COL_ORIGINAL_TITLE,
            MoviesContract.HighestRatedMovie.COL_MOVIE_POSTER,
            MoviesContract.HighestRatedMovie.COL_MOVIE_SYNOPSIS,
            MoviesContract.HighestRatedMovie.COL_RATINGS,
            MoviesContract.HighestRatedMovie.COL_RELEASE_DATE,
            MoviesContract.HighestRatedMovie.COL_TRAILERS
    };

    // This indices should match the projection above to retrieve data
    public static final int INDEX_ORIGINAL_TITLE = 0;
    public static final int INDEX_MOVIE_POSTER = 1;
    public static final int INDEX_MOVIE_SYNOPSIS = 2;
    public static final int INDEX_RATINGS = 3;
    public static final int INDEX_RELEASE_DATE = 4;
    public static final int INDEX_TRAILERS = 5;

    // Initialize the text views
    TextView mOriginalTitle;
    ImageView mMoviePoster;
    TextView mMovieSynopsis;
    TextView mRatings;
    TextView mReleaseDate;

    // Current detail activity URI address
    private Uri mUri;

    // Create an ID for loader
    public static final int ID_LOADER_DETAIL_POPULAR_INFORMATION = 100;
    public static final int ID_LOADER_DETAIL_HIGHEST_RATED_INFORMATION = 200;

    // Table identification for detail activity
    public static final String TABLE_IDENTIFICATION = "table_identification";

    // Trailers Adapter
    private TrailersAdapter mTrailerAdapter;

    // Recycler view of the Linear Layout
    private RecyclerView mTrailerist;
    // Set initial position to -1
    private int mPosition = RecyclerView.NO_POSITION;

    // Use a progress bar for displaying process of retrieving videos
    private ProgressBar mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Get a reference of the recycler view in activity_detail.xml
        mTrailerist = findViewById(R.id.rv_trailers);

        // Initialize the layout needed to make the recycler view a LinearLayout
        LinearLayoutManager linearLayoutManager = new
                LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        // Set the layout of the recycler view to the linear layout
        mTrailerist.setLayoutManager(linearLayoutManager);

        // Will not change the child layout in the RecyclerView
        mTrailerist.setHasFixedSize(true);

        // Initiate the adapter in the load finished

        // Reference the text views with the views in activity_detail
        mOriginalTitle = findViewById(R.id.tv_movie_title);
        mMoviePoster = findViewById(R.id.iv_movie_poster);
        mMovieSynopsis = findViewById(R.id.tv_movie_synopsis);
        mRatings = findViewById(R.id.tv_ratings);
        mReleaseDate = findViewById(R.id.tv_release_date);
        mSpinner = findViewById(R.id.pg_spinner);

        // Retrieve the URI build from intent
        mUri = getIntent().getData();
        int tableIdentification = getIntent().getIntExtra(TABLE_IDENTIFICATION, 0);

        // URI can't be null in detail activity
        if (mUri == null || tableIdentification == 0)
            throw new NullPointerException("URI for DetailActivity cannot be null " +
                    "or table identification can't be 0");

        // Initialize loader for the detail
        switch (tableIdentification) {
            case ID_LOADER_DETAIL_POPULAR_INFORMATION:
                getSupportLoaderManager().initLoader(ID_LOADER_DETAIL_POPULAR_INFORMATION,null,
                       this);
                break;
            case ID_LOADER_DETAIL_HIGHEST_RATED_INFORMATION:
                getSupportLoaderManager().initLoader(ID_LOADER_DETAIL_HIGHEST_RATED_INFORMATION, null,
                        this);
                break;
        }
    }

    @NonNull
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        switch(id) {

            case ID_LOADER_DETAIL_POPULAR_INFORMATION:
                return new CursorLoader(this,
                        mUri,
                        POPULAR_MOVIE_PROJECTION,
                        null,
                        null,
                        null);

            case ID_LOADER_DETAIL_HIGHEST_RATED_INFORMATION:
                return new CursorLoader(this,
                        mUri,
                        HIGHEST_RATED_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader not implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<Cursor> loader, Cursor data) {

        // Grab the position of the movie at the last path segment of the URI
        int dataPosition = Integer.parseInt(mUri.getLastPathSegment());

        if (data.getCount() == 0) {
            /* No data to display, simply return and do nothing */
            return;
        } else {
            // Cursor Loader v4 returns the full data of the cursor, move into the correct position
            data.moveToPosition(dataPosition);
        }

        /******************
         * Original Title *
         ******************/
        String movieOriginalTitle = data.getString(INDEX_ORIGINAL_TITLE);
        mOriginalTitle.setText(movieOriginalTitle);

        /****************
         * Movie Poster *
         ****************/
        String moviePosterUrl = data.getString(INDEX_MOVIE_POSTER);
        Picasso.with(this).load(moviePosterUrl).into(mMoviePoster);

        /******************
         * Movie Synopsis *
         ******************/
        String movieSynopsis = data.getString(INDEX_MOVIE_SYNOPSIS);
        mMovieSynopsis.setText(movieSynopsis);

        /*****************
         * Movie Ratings *
         *****************/
        String movieRatings = data.getString(INDEX_RATINGS) + "/10";
        mRatings.setText(movieRatings);

        /**********************
         * Movie Release Date *
         **********************/
        String movieReleaseDate = data.getString(INDEX_RELEASE_DATE);
        mReleaseDate.setText(movieReleaseDate);

        /**********************
         *   Movie Trailers   *
         **********************/
        String movieTrailers = data.getString(INDEX_TRAILERS);

        mSpinner.setVisibility(View.VISIBLE);

        // Loaders may take some time, therefore this will run when loader is finished
        if(movieTrailers != null) {
            // Remove the spinner
            mSpinner.setVisibility(View.GONE);

            // Split the strings seperated by commas
            String[] movieTrailerArray = movieTrailers.split(",");

            // Initialize the adapter to fill out the data with links
            mTrailerAdapter = new TrailersAdapter(this, movieTrailerArray,this);

            // Connect the recycler view with the adapter
            mTrailerist.setAdapter(mTrailerAdapter);

            // Initialize the position to 0
            if(mPosition == RecyclerView.NO_POSITION) {
                mPosition = 0;
            }

            // Set the recycler view to scroll smoothly
            mTrailerist.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<Cursor> loader) {
        Log.v("DETAIL_ACTIVITY","NOT IMPLEMENTING RESET");
    }

    /**
     * This will initiate the intent to start the Youtube link
     * @param idNumber          trailer ID youtube in string
     */
    @Override
    public void clickedTrailer(int idNumber, String youtubeId) {
        // TODO (2) IMPLEMENT YOUTUBE INTENT HERE
    }
}
