package com.example.android.popularmoviebeta.Data;

import android.provider.BaseColumns;

/**
 * Created by diazagasatya on 5/6/18.
 */
public class MoviesContract {

    /**
     * Prevent accidental call to create a contract
     */
    private MoviesContract(){}

    /**
     * Table for the most popular movie in 2018 and released "Today".
     * This table should be refreshed every day in order to keep it up-to-date.
     */
    public static final class PopularMovie implements BaseColumns {

        // Table names and database Column names
        public static final String TABLE_NAME = "popular_movie";

        // Column names starts here
        public static final String COL_ORIGINAL_TITLE = "original_title";
        public static final String COL_MOVIE_POSTER = "image";
        public static final String COL_MOVIE_SYSNOPSIS = "overview";
        public static final String COL_RATINGS = "ratings";
        public static final String COL_RELEASE_DATE = "release_date";

    }

    /**
     * Table for the highest rated movie in 2018 and released "Today".
     * This table should be refreshed every day in order to keep it up-to-date.
     */
    public static final class HighestRatedMovie implements BaseColumns {

        // Table names and database Column names
        public static final String TABLE_NAME = "highest_rated_movie";

        // Column names starts here
        public static final String COL_ORIGINAL_TITLE = "original_title";
        public static final String COL_MOVIE_POSTER = "image";
        public static final String COL_MOVIE_SYSNOPSIS = "overview";
        public static final String COL_RATINGS = "ratings";
        public static final String COL_RELEASE_DATE = "release_date";

    }
}
