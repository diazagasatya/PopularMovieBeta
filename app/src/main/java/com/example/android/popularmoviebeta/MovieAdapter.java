package com.example.android.popularmoviebeta;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviebeta.Data.MoviesContract;
import com.squareup.picasso.Picasso;

/**
 * Created by diazagasatya on 5/14/18.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    // The current application context
    private final Context mContext;
    private static final int POPULAR_TABLE = 100;
    private static final int TOP_RATED_TABLE = 200;
    private static final int FAVORITE_TABLE = 300;
    private int tableIdentification;

    // Bind data with the cursor returned from query
    private Cursor mCursor;

    // Create a reference to a click handler for the adapter
    private final MovieAdapterOnClickHandler mClickHandler;

    /**
     * Build an interface that will receive onclick movie
     */
    public interface MovieAdapterOnClickHandler {
        void clickedMovie(int idNumber);
    }

    /**
     * Populate the number of items needed to be created for inflating to layout
     * @param context           application current context
     */
    public MovieAdapter(Context context, MovieAdapterOnClickHandler onClickHandler) {
        mContext = context;
        mClickHandler = onClickHandler;
    }

    /**
     * Swap the old cursor with a new cursor of data
     * @param newCursor
     */
    public void swapCursor(Cursor newCursor, int tableId) {
        // Swap the old cursor with new data
        mCursor = newCursor;

        // Distinguish which table to bind data from
        tableIdentification = tableId;

        // Notify the data set is changed
        notifyDataSetChanged();
    }

    /**
     * Will create the views and group it into a view group then inflate it to the layout
     * @param viewGroup         Group of items needed to be inflated in one layout
     * @param viewType          View Type
     * @return viewHolder
     */
    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        // Use this object to inflate the view group of items
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Get the int reference of the layout
        int layoutForMoviePosters = R.layout.movie_grid_item;

        // Make sure to not attach the view group to parent immediately
        boolean shouldNotAttachImmediately = false;

        // Inflate the layout
        View view = inflater.inflate(layoutForMoviePosters, viewGroup, shouldNotAttachImmediately);

        // Initialize the view holder with the items needed to be referenced to
        MovieAdapterViewHolder viewHolder = new MovieAdapterViewHolder(view);

        // Return the view holder
        return viewHolder;
    }

    /**
     * Bind the information to the corresponding views
     * @param holder            view holders
     * @param position          position of the cursor
     */
    @Override
    public void onBindViewHolder(@NonNull MovieAdapterViewHolder holder, int position) {

        // Initiate variable
        String imageUrl = "N/A";
        String movieTitle = "N/A";

        // If the cursor approached the end or it's null
        if(!mCursor.moveToPosition(position)) {
            return;
        }

        switch(tableIdentification) {
            case POPULAR_TABLE:

                // Grab the image url and movie title
                imageUrl = mCursor.getString(mCursor
                        .getColumnIndex(MoviesContract.PopularMovie.COL_MOVIE_POSTER));

                movieTitle = mCursor.getString(mCursor
                        .getColumnIndex(MoviesContract.PopularMovie.COL_ORIGINAL_TITLE));

                break;

            case TOP_RATED_TABLE:

                // Grab the image url and movie title
                imageUrl = mCursor.getString(mCursor
                        .getColumnIndex(MoviesContract.HighestRatedMovie.COL_MOVIE_POSTER));

                movieTitle = mCursor.getString(mCursor
                        .getColumnIndex(MoviesContract.HighestRatedMovie.COL_ORIGINAL_TITLE));

                break;

            case FAVORITE_TABLE:

                // Grab the image url and movie title
                imageUrl = mCursor.getString(mCursor
                        .getColumnIndex(MoviesContract.FavoriteMovies.COL_MOVIE_POSTER));

                movieTitle = mCursor.getString(mCursor
                        .getColumnIndex(MoviesContract.FavoriteMovies.COL_ORIGINAL_TITLE));

                break;
        }

        // Bind the image url and the title
        holder.bind(imageUrl,movieTitle);
    }

    /**
     * Will return the number of items inserted to the view holder to cache
     * @return the number of items cached in view holder
     */
    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Add a reference of the image view and text view from movie grid item layout
        ImageView nPosterImageUrl;
        TextView nMovieTitle;

        /**
         * Will create reference to the items inside the layout to be binded
         * @param viewItem
         */
        public MovieAdapterViewHolder(View viewItem) {

            // Call the super class to initialize the viewItem
            super(viewItem);

            // Reference the image view and the text view from layout
            nPosterImageUrl = itemView.findViewById(R.id.iv_movie_poster);
            nMovieTitle = itemView.findViewById(R.id.tv_movie_title);

            // Set the listener to this listener
            viewItem.setOnClickListener(this);

        }

        /**
         * Bind the image view with an image from api url, & movie title
         * @param imageUrl          url of the image referenced to
         * @param movieTitle        title of the movie
         */
        void bind(String imageUrl, String movieTitle) {
            // Bind the image using Picasso
            Picasso.with(mContext).load(imageUrl).into(nPosterImageUrl);
            // Set the movie title
            nMovieTitle.setText(movieTitle);
        }

        /**
         * Grab the position of the cursor that was clicked to get the movie id
         * @param v     The View item that was clicked
         */
        @Override
        public void onClick(View v) {
            // Get the position of the adapter that was clicked and pass in the position
            int movieIdentification = getAdapterPosition();
            mClickHandler.clickedMovie(movieIdentification);
        }
    }
}
