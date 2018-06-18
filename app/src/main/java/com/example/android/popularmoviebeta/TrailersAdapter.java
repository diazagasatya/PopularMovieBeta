package com.example.android.popularmoviebeta;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerAdapterViewHolder> {

    private final Context mContext;
    // Instead of using cursor, we will populate the adapter with the number of links
    private String[] youtubeLinks;

    // Create a reference to a click handler for the adapter
    final private TrailerOnClickHandler mClickHandler;

    /**
     * Build an interface that will receive onclick trailer
     */
    public interface TrailerOnClickHandler {
        void clickedTrailer(int idNumber, String youtubeId);
    }

    /**
     * Initiate this adapter with the context and the onclickhandler
     * @param context               context of the application
     */
    public TrailersAdapter(Context context, String[] videoLinks, TrailerOnClickHandler onClickHandler) {
        mContext = context;
        youtubeLinks = videoLinks;
        mClickHandler = onClickHandler;

        // Notify data set changed when there are video links
        notifyDataSetChanged();
    }

    /**
     * Will create the views for each items in a view holder and return each item view
     * @param viewGroup         Group of view items
     * @param viewType          View Type
     * @return viewHolder
     */
    @NonNull
    @Override
    public TrailersAdapter.TrailerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        // Use layout inflater to inflate linear layout recycler view
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Get the int reference of the layout for each item
        int layoutForTrailers = R.layout.linear_layout_item;

        // Make sure to not attach the view group to parent immediately (only when info is available)
        boolean shouldAttachImmediately = false;

        // Inflate the layout here
        View view = inflater.inflate(layoutForTrailers,viewGroup, shouldAttachImmediately);

        // Initialize the view holder to hold all of the items
        TrailerAdapterViewHolder viewHolder = new TrailerAdapterViewHolder(view);

        // Return the view that holds all of the items
        return viewHolder;

    }

    /**
     * Bind the information to the corresponding views
     * @param holder            view hodlers
     * @param position          position of the cursor
     */
    @Override
    public void onBindViewHolder(@NonNull TrailersAdapter.TrailerAdapterViewHolder holder, int position) {

        // Initiate variable
        String trailerString = "Trailer ";

        // If cursor reached the end return
        if(youtubeLinks.length == 0) {
            return;
        }

        // Reference to the position of the links
        String youtubeUrlString = youtubeLinks[position];
        trailerString += (position + 1);

        // Bind the string variables from the data in the cursor
        holder.bind(trailerString, youtubeUrlString);
    }

    @Override
    public int getItemCount() {
        return youtubeLinks.length;
    }

    class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Add references to the line seperator, play image & youtube url
        TextView nYoutubeUrl, nTrailerText;
        ImageView nPlayButton, nLineSeparator;

        /**
         * Will initiate each item with corresponding id label in linear_layout_item.xml
         * @param viewItem              View item
         */
        public TrailerAdapterViewHolder(View viewItem) {

            // Call the super class to initialize the viewItem
            super(viewItem);

            // Create references to the textViews and imageViews
            nYoutubeUrl = itemView.findViewById(R.id.tv_youtube_url);
            nTrailerText = itemView.findViewById(R.id.tv_trailer_string);
            nPlayButton = itemView.findViewById(R.id.iv_play_image);
            nLineSeparator = itemView.findViewById(R.id.line_separator);

            // Set listener for on click items
            viewItem.setOnClickListener(this);
        }

        /**
         * Bind the trailer string and the youtube url to each item
         * @param trailerString             "Trailer" string and the corresponding integer
         * @param youtubeUrlString          Hidden youtube url for running intent later on
         */
        void bind(String trailerString, String youtubeUrlString) {
            // Bind the texts
            nYoutubeUrl.setText(youtubeUrlString);
            nTrailerText.setText(trailerString);
        }


        /**
         * Will grab the position of the item that was clicked
         * @param v
         */
        @Override
        public void onClick(View v) {
            // Get the position of the adapter that was clicked and pass the position
            int trailerIdentification = getAdapterPosition();
            // Grab the link and send it to clickedTrailer method as a paramater to call YT intent
            String youtubeLink = youtubeLinks[trailerIdentification];
            mClickHandler.clickedTrailer(trailerIdentification, youtubeLink);

        }
    }


}
