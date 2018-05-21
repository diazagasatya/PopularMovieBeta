package com.example.android.popularmoviebeta.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by diazagasatya on 5/13/18.
 */
public class MoviesDBHelper extends SQLiteOpenHelper {

    // Name the database that will be created internally in the device
    public static final String DATABASE_NAME = "movies.db";

    // Initiate the version of the database from 1
    public static final int DATABASE_VERSION = 1;

    /**
     * Create the constructor that will call the super class SQLiteOpenHelper
     * Instantiating the database with the name and version
     * @param context       We need the current context of application
     */
    public MoviesDBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    /**
     * Instantiate the two tables in SQL syntax
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_POPULAR_MOVIE_TABLE = "CREATE TABLE "
                + MoviesContract.PopularMovie.TABLE_NAME + " ("
                + MoviesContract.PopularMovie._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MoviesContract.PopularMovie.COL_ORIGINAL_TITLE + " TEXT NOT NULL, "
                + MoviesContract.PopularMovie.COL_MOVIE_POSTER + " TEXT NOT NULL, "
                + MoviesContract.PopularMovie.COL_MOVIE_SYSNOPSIS + " TEXT NOT NULL, "
                + MoviesContract.PopularMovie.COL_RATINGS + " TEXT NOT NULL, "
                + MoviesContract.PopularMovie.COL_RELEASE_DATE + " TEXT NOT NULL"
                + ");";

        final String CREATE_HIGHEST_RATED_MOVIE_TABLE = "CREATE TABLE "
                + MoviesContract.HighestRatedMovie.TABLE_NAME + " ("
                + MoviesContract.HighestRatedMovie._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MoviesContract.HighestRatedMovie.COL_ORIGINAL_TITLE + " TEXT NOT NULL, "
                + MoviesContract.HighestRatedMovie.COL_MOVIE_POSTER + " TEXT NOT NULL, "
                + MoviesContract.HighestRatedMovie.COL_MOVIE_SYSNOPSIS + " TEXT NOT NULL, "
                + MoviesContract.HighestRatedMovie.COL_RATINGS + " TEXT NOT NULL, "
                + MoviesContract.HighestRatedMovie.COL_RELEASE_DATE + " TEXT NOT NULL"
                + ");";

        sqLiteDatabase.execSQL(CREATE_POPULAR_MOVIE_TABLE);
        sqLiteDatabase.execSQL(CREATE_HIGHEST_RATED_MOVIE_TABLE);
    }

    /**
     * Upgrade the current database and tables if version is upgraded
     * @param db            database will be updated
     * @param oldVersion    oldVersion
     * @param newVersion    newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        /**
         * if new version is needed to be created
         */
        if(newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.PopularMovie.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.HighestRatedMovie.TABLE_NAME);
            onCreate(db);
        }

    }
}
