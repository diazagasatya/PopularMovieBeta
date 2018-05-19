package com.example.android.popularmoviebeta.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by diazagasatya on 5/18/18.
 */
public class MoviesProvider extends ContentProvider {

    // TODO (3) FINISH THE PROVIDER

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
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

        int numberOfRowsDeleted = 0;

        /*
         * This way, the query will delete all rows and return the number of rows
         * deleted.
         */
        if(selection == null) selection = "1";

        // TODO (4) MAKE THE URI MATCHER


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
        return 0;
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
