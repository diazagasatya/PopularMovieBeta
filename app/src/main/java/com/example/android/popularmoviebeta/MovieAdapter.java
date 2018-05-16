package com.example.android.popularmoviebeta;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by diazagasatya on 5/14/18.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private int mNumberOfItemsCount;
    private Context mContext;

    /**
     * Populate the number of items needed to be created for inflating to layout
     * @param numberOfItems     number of items will be needed to be created
     */
    public MovieAdapter(int numberOfItems, Context context) {
        mContext = context;
        mNumberOfItemsCount = numberOfItems;
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

    @Override
    public void onBindViewHolder(@NonNull MovieAdapterViewHolder holder, int position) {

    }

    /**
     * Will return the number of items inserted to the view holder to cache
     * @return the number of items cached in view holder
     */
    @Override
    public int getItemCount() {
        return mNumberOfItemsCount;
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder {

        // Add a reference of the image view and text view from movie grid item layout
        ImageView nPosterImageUrl;
        TextView nMovieTitle;

        /**
         * Will create reference to the items inside the layour to be binded
         * @param viewItem
         */
        public MovieAdapterViewHolder(View viewItem) {

            // Call the super class to initialize the viewItem
            super(viewItem);

            // Reference the image view and the text view from layour
            nPosterImageUrl = itemView.findViewById(R.id.iv_movie_poster);
            nMovieTitle = itemView.findViewById(R.id.tv_movie_title);

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

    }
}