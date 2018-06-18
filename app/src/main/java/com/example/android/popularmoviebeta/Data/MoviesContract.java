package com.example.android.popularmoviebeta.Data;

import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Created by diazagasatya on 5/6/18.
 */
public class MoviesContract {

    /**
     * Prevent accidental call to create a contract
     */
    private MoviesContract(){}

    /*
     * Name the content provider for this application as CONTENT AUTHORITY
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmoviebeta";

    /*
     * Create the base of the content provider URI that will be use to communicate data
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
     * Create the paths for the table's needed to be populated to the UI
     */
    public static final String PATH_POPULARITY = "popular_movie";
    public static final String PATH_HIGHEST_RATED = "highest_rated_movie";

    /**
     * Table for the most popular movie in 2018 and released "Today".
     * This table should be refreshed every day in order to keep it up-to-date.
     */
    public static final class PopularMovie implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_POPULARITY).build();

        // Table names and database Column names
        public static final String TABLE_NAME = "popular_movie";

        // Column names starts here
        public static final String COL_ORIGINAL_TITLE = "original_title";
        public static final String COL_MOVIE_POSTER = "image";
        public static final String COL_MOVIE_SYNOPSIS = "overview";
        public static final String COL_RATINGS = "ratings";
        public static final String COL_RELEASE_DATE = "release_date";
        public static final String COL_MOVIE_ID = "movie_id";
        public static final String COL_TRAILERS = "trailers";
        public static final String COL_REVIEW = "review";

        /**
         * Create URI to grab the detail information of the movie
         * @param movieId           The _ID of the movie in the table
         * @return URI
         */
        public static Uri buildUriWithIdPopular(int movieId) {

            // The URI of the movie on the table
            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(movieId)).build();
        }

    }

    /**
     * Table for the highest rated movie in 2018 and released "Today".
     * This table should be refreshed every day in order to keep it up-to-date.
     */
    public static final class HighestRatedMovie implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_HIGHEST_RATED).build();

        // Table names and database Column names
        public static final String TABLE_NAME = "highest_rated_movie";

        // Column names starts here
        public static final String COL_ORIGINAL_TITLE = "original_title";
        public static final String COL_MOVIE_POSTER = "image";
        public static final String COL_MOVIE_SYNOPSIS = "overview";
        public static final String COL_RATINGS = "ratings";
        public static final String COL_RELEASE_DATE = "release_date";
        public static final String COL_MOVIE_ID = "movie_id";
        public static final String COL_TRAILERS = "trailers";
        public static final String COL_REVIEW = "review";

        /**
         * Create URI to grab the detail information of the movie
         * @param movieId           The _ID of the movie in the table
         * @return URI
         */
        public static Uri buildUriWithIdHighestRated(int movieId) {

            // The URI of the movie on the table
            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(movieId)).build();
        }

    }

}
