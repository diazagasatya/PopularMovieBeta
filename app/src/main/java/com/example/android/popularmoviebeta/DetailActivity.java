package com.example.android.popularmoviebeta;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviebeta.Data.MoviesContract;
import com.example.android.popularmoviebeta.Data.MoviesDBHelper;
import com.example.android.popularmoviebeta.Utilities.NetworkUtils;
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
            MoviesContract.PopularMovie.COL_MOVIE_ID,
            MoviesContract.PopularMovie.COL_TRAILERS,
            MoviesContract.PopularMovie.COL_REVIEW
    };
    public static final String[] HIGHEST_RATED_PROJECTION = {
            MoviesContract.HighestRatedMovie.COL_ORIGINAL_TITLE,
            MoviesContract.HighestRatedMovie.COL_MOVIE_POSTER,
            MoviesContract.HighestRatedMovie.COL_MOVIE_SYNOPSIS,
            MoviesContract.HighestRatedMovie.COL_RATINGS,
            MoviesContract.HighestRatedMovie.COL_RELEASE_DATE,
            MoviesContract.HighestRatedMovie.COL_MOVIE_ID,
            MoviesContract.HighestRatedMovie.COL_TRAILERS,
            MoviesContract.HighestRatedMovie.COL_REVIEW
    };
    public static final String[] FAVORITE_PROJECTION = {
            MoviesContract.FavoriteMovies.COL_ORIGINAL_TITLE,
            MoviesContract.FavoriteMovies.COL_MOVIE_POSTER,
            MoviesContract.FavoriteMovies.COL_MOVIE_SYNOPSIS,
            MoviesContract.FavoriteMovies.COL_RATINGS,
            MoviesContract.FavoriteMovies.COL_RELEASE_DATE,
            MoviesContract.FavoriteMovies.COL_MOVIE_ID,
            MoviesContract.FavoriteMovies.COL_TRAILERS,
            MoviesContract.FavoriteMovies.COL_REVIEW
    };

    // This indices should match the projection above to retrieve data
    public static final int INDEX_ORIGINAL_TITLE = 0;
    public static final int INDEX_MOVIE_POSTER = 1;
    public static final int INDEX_MOVIE_SYNOPSIS = 2;
    public static final int INDEX_RATINGS = 3;
    public static final int INDEX_RELEASE_DATE = 4;
    public static final int INDEX_MOVIE_ID = 5;
    public static final int INDEX_TRAILERS = 6;
    public static final int INDEX_REVIEWS = 7;

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
    public static final int ID_LOADER_DETAIL_FAVORITE_INFORMATION = 300;

    // Table identification for detail activity
    public static final String TABLE_IDENTIFICATION = "table_identification";
    public static final String TABLE_KEY = "table_id";
    public static final String SCROLL_POSITION_KEY = "scroll_position";

    // Trailers Adapter
    private TrailersAdapter mTrailerAdapter;
    private ReviewsAdapter mReviewAdapter;

    // Recycler view of the Linear Layout Trailers
    private RecyclerView mTrailerist;
    private RecyclerView mReviewList;

    // Set initial position to -1
    private int mPositionTrailer = RecyclerView.NO_POSITION;
    private int mPositionReview = RecyclerView.NO_POSITION;

    // Use a progress bar for displaying process of retrieving videos
    private ProgressBar mSpinner;

    // Cursor with the data of this movie
    private Cursor nMovieData;

    // Table identification
    private int tableIdentification;

    // Get reference of current scroll view
    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        /*
         * Recycler View for the Trailers
         */
        // Get a reference of the recycler view in activity_detail.xml
        mTrailerist = findViewById(R.id.rv_trailers);

        // Initialize the layout needed to make the recycler view a LinearLayout
        LinearLayoutManager linearLayoutManager = new
                LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        // Set the layout of the recycler view to the linear layout
        mTrailerist.setLayoutManager(linearLayoutManager);

        // Will not change the child layout in the RecyclerView & Initiate adapter in LoadFinish
        mTrailerist.setHasFixedSize(true);

        /*
         * Recycler View for the reviews
         */
        // Get a reference to the recycler view in activity_details.xml
        mReviewList = findViewById(R.id.rv_reviews);

        // Initialize the layout needed to make the recycler view a LinearLayout
        LinearLayoutManager linearLayoutManagerReview = new
                LinearLayoutManager(this
                ,LinearLayoutManager.VERTICAL,false);

        // Use the previously made linear layout manager settings
        mReviewList.setLayoutManager(linearLayoutManagerReview);

        // Will not change the child layout in the RecyclerView & Initiate adapter in LoadFinish
        mReviewList.setHasFixedSize(true);

        /*
         * Essential information for the detail activity UI
         */
        // Reference the text views with the views in activity_detail
        // Get reference of the scroll view
        mScrollView = findViewById(R.id.scroll_view);
        mOriginalTitle = findViewById(R.id.tv_movie_title);
        mMoviePoster = findViewById(R.id.iv_movie_poster);
        mMovieSynopsis = findViewById(R.id.tv_movie_synopsis);
        mRatings = findViewById(R.id.tv_ratings);
        mReleaseDate = findViewById(R.id.tv_release_date);
        mSpinner = findViewById(R.id.pg_spinner);

        // Retrieve the URI build from intent
        mUri = getIntent().getData();

        // Grab the appropriate table id if temporarily destroyed
        if(savedInstanceState != null) {
            tableIdentification = savedInstanceState.getInt(TABLE_KEY);
        } else {
            tableIdentification = getIntent().getIntExtra(TABLE_IDENTIFICATION, 0);
        }

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
            case ID_LOADER_DETAIL_FAVORITE_INFORMATION:
                getSupportLoaderManager().initLoader(ID_LOADER_DETAIL_FAVORITE_INFORMATION, null,
                        this);
                break;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Grab the scroll position
        final int[] scrollPosition = savedInstanceState
                .getIntArray(SCROLL_POSITION_KEY);

        // Check whether scroll position is not null
        if(scrollPosition != null) {
            mScrollView.scrollTo(scrollPosition[0],scrollPosition[1]);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(TABLE_KEY, tableIdentification);

        // Save the X and Y value of current scroll view position
        outState.putIntArray(SCROLL_POSITION_KEY,
                new int[] {mScrollView.getScrollX()
                        , mScrollView.getScrollY()});

        super.onSaveInstanceState(outState);
    }

    /**
     * Loader will load all of the data of a single row
     * @param id             Table Identification
     * @param args           Arguments for query
     * @return CursorLoader  Cursor with all of the data
     */
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

            case ID_LOADER_DETAIL_FAVORITE_INFORMATION:
                return new CursorLoader(this,
                        mUri,
                        FAVORITE_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader not implemented: " + id);
        }
    }

    /**
     * Will run the adapters & cursor data needed to fill in information in the UI
     * @param loader            Loader
     * @param data              CursorLoader Data
     */
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
            // Save the data to mMovieData if needed for adding to favorites
            nMovieData = data;
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
            if(mPositionTrailer == RecyclerView.NO_POSITION) {
                mPositionTrailer = 0;
            }

            // Set the recycler view to scroll smoothly
            mTrailerist.smoothScrollToPosition(mPositionTrailer);

            // Update the movieData
            nMovieData = data;
        }

        /**********************
         *   Movie Reviews   *
         **********************/
        String movieReviews = data.getString(INDEX_REVIEWS);

        // Loaders may take some time, therefore this will run when loader is finished
        if(movieReviews != null) {

            // Split the strings separated by "##"
            String[] reviews = movieReviews.split("\\##");

            for(String review : reviews) {
                System.out.println("TESTING : " + review);
            }

            // Initialize the adapter to fill out the item with the array
            mReviewAdapter = new ReviewsAdapter(this, reviews);

            // Connect the recycler view with the adapter
            mReviewList.setAdapter(mReviewAdapter);

            // Initialize the position to 0
            if(mPositionReview == RecyclerView.NO_POSITION) {
                mPositionReview = 0;
            }

            // Set the recycler view to scroll smoothly
            mReviewList.smoothScrollToPosition(mPositionReview);

            // Update the movieData
            nMovieData = data;
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
    public void clickedTrailer(int idNumber, String youtubeLink) {

        // Parse the id for application intent
        String[] youtubeId = youtubeLink.split("=");
        final int youtubeIdIndex = 1;

        // Code safely by having the web browser as Plan B if youtube app is unavailable
        Intent webBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink));

        // Here try if application is available, if not use regular web browser
        try {
            // App intent and also Web intent if youtube is unavailable in the device
            Intent applicationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"
                    + youtubeId[youtubeIdIndex]));

            // Start applicationIntent
            startActivity(applicationIntent);

        } catch (ActivityNotFoundException exception) {

            // Start web browser intent
            startActivity(webBrowserIntent);
        }
    }

    /**
     * This method is linked with the button add to favorites.
     * This will insert the movie clicked to the favorites table
     * This will delete the movie clicked if already added to favorite table
     * @param view
     */
    public void addToFavorites(View view) {

        String movieId = "N/A";
        Toast toastValidation;

        // Check first if data is completely loaded
        if(nMovieData.getCount() == 0) {
            return;
        }

        // Get the Movie ID from favorite table
        final String[] STRING_PROJECTION = {
                MoviesContract.FavoriteMovies.COL_MOVIE_ID
        };

        // Try grabbing the movie ID
        try {

            String selectionArgs = nMovieData.getString(INDEX_MOVIE_ID);

            // Check if the Data is already present in table, if so delete if not add
            ContentResolver contentResolver = getContentResolver();
            Cursor cursorCheck = contentResolver.query(
                    MoviesContract.FavoriteMovies.CONTENT_URI
                    ,STRING_PROJECTION
                    , MoviesContract.FavoriteMovies.COL_MOVIE_ID + "=?"
                    ,new String[] {selectionArgs}
                    ,null);

            cursorCheck.moveToFirst();
            movieId = cursorCheck.getString(0);
            cursorCheck.close();

        } catch (Exception e) {
            Log.v("CURSOR: ", "Cursor is empty");
        }

        // Check if the Movie already in table
        if(movieId.equals(nMovieData.getString(INDEX_MOVIE_ID))) {

            // delete movie in the favorite table
            deleteMovie(movieId);
            // Show a toast that insertion to favorite table is successful
            toastValidation = Toast.makeText(this,
                    "Deleted from the Favorite List!",Toast.LENGTH_LONG);
            toastValidation.show();

        } else {

            // Add movie if movie is not in favorite table
            addMovie();
            // Show a toast that insertion to favorite table is successful
            toastValidation = Toast.makeText(this,
                    "Added to Favorite List!",Toast.LENGTH_LONG);
            toastValidation.show();
        }
    }


    /**
     * This function will insert the current showing movie to the favorite table
     */
    public void addMovie() {

        // Create a ContentValues instance to pass the values onto the insert query
        ContentValues movieData = new ContentValues();

        // Call put to insert values inside the contentValue object
        movieData.put(MoviesContract.FavoriteMovies.COL_ORIGINAL_TITLE,
                nMovieData.getString(INDEX_ORIGINAL_TITLE));
        movieData.put(MoviesContract.FavoriteMovies.COL_MOVIE_POSTER,
                nMovieData.getString(INDEX_MOVIE_POSTER));
        movieData.put(MoviesContract.FavoriteMovies.COL_MOVIE_SYNOPSIS,
                nMovieData.getString(INDEX_MOVIE_SYNOPSIS));
        movieData.put(MoviesContract.FavoriteMovies.COL_RATINGS,
                nMovieData.getString(INDEX_RATINGS));
        movieData.put(MoviesContract.FavoriteMovies.COL_RELEASE_DATE,
                nMovieData.getString(INDEX_RELEASE_DATE));
        movieData.put(MoviesContract.FavoriteMovies.COL_MOVIE_ID,
                nMovieData.getString(INDEX_MOVIE_ID));
        movieData.put(MoviesContract.FavoriteMovies.COL_TRAILERS,
                nMovieData.getString(INDEX_TRAILERS));
        movieData.put(MoviesContract.FavoriteMovies.COL_REVIEW,
                nMovieData.getString(INDEX_REVIEWS));

        // Get reference to content resolver for insertion
        ContentResolver favoriteContentResolver = this.getContentResolver();

        // Returned Uri with newly appended ID
        Uri movieAddedUriWithId = favoriteContentResolver
                .insert(MoviesContract.FavoriteMovies.CONTENT_URI, movieData);

        // Print out the uri for debugging purposes
        Log.v("Insertion Confirmation", movieAddedUriWithId.toString());
    }

    /**
     * This function will delete the movie clicked in the favorite list
     */
    public void deleteMovie(String movieIdentification) {

        // Get the context content resolver
        ContentResolver contentResolverDelete = getContentResolver();

        // Delete the movie in the favorite table
        int deletedRow = contentResolverDelete.delete(MoviesContract.FavoriteMovies.CONTENT_URI
                ,MoviesContract.FavoriteMovies.COL_MOVIE_ID + "=?"
                ,new String[] {movieIdentification});

        // Print out the number of deletion for debugging purposes
        Log.v("Deletion Confirmation ", Integer.toString(deletedRow));
    }
}
