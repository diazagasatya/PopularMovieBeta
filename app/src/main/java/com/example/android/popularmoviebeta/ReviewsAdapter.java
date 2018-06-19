package com.example.android.popularmoviebeta;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewAdapterViewHolder> {

    private final Context mContext;
    // Instead of using cursor we are going to get an array of reviews
    private String[] reviews;

    /**
     * Initiate the context and the array given from detailactivity
     * @param context           Context of the application
     * @param reviewArray       Reviews
     */
    public ReviewsAdapter(Context context, String[] reviewArray) {
        mContext = context;
        reviews = reviewArray;

        notifyDataSetChanged();
    }

    /**
     * Will create the views for each items in a view holder and return each item view
     * @param viewGroup         Group of view items
     * @param viewType          View type
     * @return viewHolder
     */
    @NonNull
    @Override
    public ReviewsAdapter.ReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        // Use layout manager to inflate the linear layout recycler view
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        // Get the integer reference for each item
        int layoutForReview = R.layout.review_linear_item;

        // Make sure to not attach the view group to parent immediately
        boolean shouldAttachImmediately = false;

        // Inflate the layout here
        View view = layoutInflater.inflate(layoutForReview, viewGroup, shouldAttachImmediately);

        // Initialize the viewgroup holder to hold all of the items
        ReviewAdapterViewHolder viewHolder = new ReviewAdapterViewHolder(view);

        return viewHolder;
    }

    /**
     * Bind the text views with the corresponding information
     * @param holder        View Holders
     * @param position      Position of the item
     */
    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapter.ReviewAdapterViewHolder holder, int position) {

        // If if there's no review no need to bind
        if(reviews.length == 1) {
            return;
        }

        // Reference
        final int AUTHOR = 0, CONTENT = 1;

        // Author is separated by "<>"
        String[] authorAndContent = reviews[position].split("\\<>");


        holder.bind(authorAndContent[AUTHOR], authorAndContent[CONTENT]);
    }

    /**
     * This will count the number of the item needed to be created
     * @return reviewslength
     */
    @Override
    public int getItemCount() {
        return reviews.length;
    }

    class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {

        // Add reference to each item
        TextView authorReview, contentReview;

        /**
         * Will initiate each item (Review)
         * @param viewItem      View Item
         */
        public ReviewAdapterViewHolder(View viewItem) {

            // Call super class to initialize each item
            super(viewItem);

            // Create reference to the textviews
            authorReview = itemView.findViewById(R.id.tv_author);
            contentReview = itemView.findViewById(R.id.tv_content);
        }

        /**
         * Bind the review with the correspoinding author and content
         * @param author            Review author
         * @param content           Content Review
         */
        void bind(String author, String content) {
            // Bind strings with the corresponding textviews
            authorReview.setText(author);
            contentReview.setText(content);
        }

    }

}
