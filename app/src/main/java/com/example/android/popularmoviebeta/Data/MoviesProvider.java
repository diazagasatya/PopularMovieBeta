package com.example.android.popularmoviebeta.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by diazagasatya on 5/18/18.
 */
public class MoviesProvider extends ContentProvider {

    // Create an instance of MoviesDBHelper to reference a database
    private MoviesDBHelper mMoviesDatabase;

    // Create an integer reference to different URI table match
    private static final int POPULAR_PATH = 100;
    private static final int POPULAR_PATH_WITH_ID = 101;
    private static final int HIGHEST_RATED_PATH = 200;
    private static final int HIGHEST_RATED_WITH_ID = 201;

    // Create a global variable of uriMatcher
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
     * This is a helper method that will create a UriMatcher and match URIs to retrieve
     * information from the database.
     * @return uriMatcher
     */
    public static final UriMatcher buildUriMatcher() {

        // Instantiate a new blank uri matcher
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Add the URIs that matches the tables in the movie contract
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY,
                MoviesContract.PATH_POPULARITY, POPULAR_PATH);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY,
                MoviesContract.PATH_POPULARITY + "/#", POPULAR_PATH_WITH_ID);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY,
                MoviesContract.PATH_HIGHEST_RATED, HIGHEST_RATED_PATH);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY,
                MoviesContract.PATH_HIGHEST_RATED + "/#", HIGHEST_RATED_WITH_ID);

        return uriMatcher;
    }


    /**
     * Will initialize the content provider on startup. This will create the database
     * on the main thread on the start of the application.
     * @return boolean  return True if content provider is instantiated successfully
     */
    @Override
    public boolean onCreate() {

        // Instantiate the Database that will be used in this provider
        Context context = getContext();
        mMoviesDatabase = new MoviesDBHelper(context);

        return true;
    }

    /**
     * Will build a SQL Query to the database and retrieve the appropriate data as a cursor
     * to populate into the UI
     * @param uri           The URI of the Query
     * @param projection    Columns that wanted to be returned
     * @param selection     Rows that wanted to be returned
     * @param selectionArgs Specific rows that wanted to be returned
     * @param sortOrder     The order of the data
     * @return resultCursor
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Get the reference to the movie database
        final SQLiteDatabase db = mMoviesDatabase.getReadableDatabase();

        Cursor resultCursor;

        // Use switch statement to return the cursor from the SQL query
        switch (sUriMatcher.match(uri)) {

            // CASE 1 FOR ALL ROWS IN POPULAR TABLE
            case POPULAR_PATH:

                // Retrieve the full Popular Movie table
                resultCursor = db.query(MoviesContract.PopularMovie.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            // CASE 2 FOR ALL ROWS IN HIGHEST RATED TABLE
            case HIGHEST_RATED_PATH:

                // Retrieve the full Highest Rated Movie table
                resultCursor = db.query(MoviesContract.HighestRatedMovie.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            // CASE 3 FOR A SINGLE MOVIE IN POPULAR TABLE
            case POPULAR_PATH_WITH_ID:

                // Retrieve the full Popular Movie table
                resultCursor = db.query(MoviesContract.PopularMovie.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            // CASE 3 FOR A SINGLE MOVIE IN POPULAR TABLE
            case HIGHEST_RATED_WITH_ID:

                // Retrieve the full Popular Movie table
                resultCursor = db.query(MoviesContract.HighestRatedMovie.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }

        // Notify the changes to content resolver
        resultCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return resultCursor;
    }

    /**
     * Will insert values in bulk to the appropriate table targeted from the URI
     * @param uri       Target URI of table insertion
     * @param values    The values that will be inserted to the database
     * @return numberOfRowsInserted
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        // Create a reference to the MoviesDBHelper database to bulk insert
        final SQLiteDatabase db = mMoviesDatabase.getWritableDatabase();

        // Bulk insert to appropriate table
        int tablePath = sUriMatcher.match(uri);

        // Create SQL Query from URI to the appropriate Table
        if(tablePath == POPULAR_PATH) {

            // begin transaction before insertion to avoid leak
            db.beginTransaction();
            int numberOfRowsInserted = 0;

            // Surround the bulk insert in try block for safety
            try {
                // loop to all content values and insert to table
                for(ContentValues cv : values) {

                    // insert method will return 1/-1
                    long _id = db.insert(MoviesContract.PopularMovie.TABLE_NAME,
                            null, cv);

                    // increment the number of successful row inserted
                    if(_id != -1) {
                        numberOfRowsInserted++;
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                // End the transaction to avoid leaks
                db.endTransaction();
            }

            // Notify change to the content resolver
            if(numberOfRowsInserted > 0) {
                getContext().getContentResolver().notifyChange(uri,null);
            }

            // return the number of rows inserted
            return numberOfRowsInserted;

        }
        else if(tablePath == HIGHEST_RATED_PATH) {

            db.beginTransaction();
            int numberOfRowsInserted = 0;

            // Surround the bulk insert in try block for safety
            try {
                // loop to all content values and insert to table
                for(ContentValues cv : values) {

                    // insert method will return 1/-1
                    long _id = db.insert(MoviesContract.HighestRatedMovie.TABLE_NAME,
                            null, cv);

                    // increment the number of successful row inserted
                    if(_id != -1) {
                        numberOfRowsInserted++;
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                // End the transaction to avoid leaks
                db.endTransaction();
            }

            // Notify change to the content resolver
            if(numberOfRowsInserted > 0) {
                getContext().getContentResolver().notifyChange(uri,null);
            }

            // return the number of rows inserted
            return numberOfRowsInserted;
        }
        else {
            return super.bulkInsert(uri, values);
        }
    }

    /**
     * This method will match the URI
     * @param uri               The full URI to query of the table
     * @param selection         Which rows needed to be deleted
     * @param selectionArgs     Specific columns arguments to restrict deletion
     * @return numberOfRowsDeleted
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int numberOfRowsDeleted;

        /*
         * This way, the query will delete all rows and return the number of rows
         * deleted.
         */
        if(selection == null) selection = "1";

        // Use switch statement to run the appropriate SQL query to the database
        switch(sUriMatcher.match(uri)) {
            case POPULAR_PATH:
                numberOfRowsDeleted = mMoviesDatabase.getWritableDatabase()
                        .delete(MoviesContract.PopularMovie.TABLE_NAME,
                                selection,
                                selectionArgs);
                break;
            case HIGHEST_RATED_PATH:
                numberOfRowsDeleted = mMoviesDatabase.getWritableDatabase()
                        .delete(MoviesContract.HighestRatedMovie.TABLE_NAME,
                                selection,
                                selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }

        // Notify change to content resolver that query was handled success to the particular URI
        if(numberOfRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return numberOfRowsDeleted;
    }

    /**
     * Not implementing update
     * @param uri           null
     * @param values        null
     * @param selection     null
     * @param selectionArgs null
     * @return
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new RuntimeException("Not implementing update in this project");
    }

    /**
     * Not implementing getType
     * @param uri           null
     * @return              null
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("Not implementing getType in this project");
    }

    /**
     * Not implementing insert, we are implementing bulkInsert instead
     * @param uri           null
     * @param values        null
     * @return              null
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        throw new RuntimeException("Not implementing insert in this project");
    }
}
