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
    // Increment the version for every changes
    public static final int DATABASE_VERSION = 3;

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
                + MoviesContract.PopularMovie.COL_MOVIE_SYNOPSIS + " TEXT NOT NULL, "
                + MoviesContract.PopularMovie.COL_RATINGS + " TEXT NOT NULL, "
                + MoviesContract.PopularMovie.COL_RELEASE_DATE + " TEXT NOT NULL, "
                + MoviesContract.PopularMovie.COL_MOVIE_ID + " TEXT NOT NULL, "
                + MoviesContract.PopularMovie.COL_TRAILERS + " TEXT , " // NULLABLE
                + MoviesContract.PopularMovie.COL_REVIEW + " TEXT " // NULLABLE
                + ");";

        final String CREATE_HIGHEST_RATED_MOVIE_TABLE = "CREATE TABLE "
                + MoviesContract.HighestRatedMovie.TABLE_NAME + " ("
                + MoviesContract.HighestRatedMovie._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MoviesContract.HighestRatedMovie.COL_ORIGINAL_TITLE + " TEXT NOT NULL, "
                + MoviesContract.HighestRatedMovie.COL_MOVIE_POSTER + " TEXT NOT NULL, "
                + MoviesContract.HighestRatedMovie.COL_MOVIE_SYNOPSIS + " TEXT NOT NULL, "
                + MoviesContract.HighestRatedMovie.COL_RATINGS + " TEXT NOT NULL, "
                + MoviesContract.HighestRatedMovie.COL_RELEASE_DATE + " TEXT NOT NULL,  "
                + MoviesContract.HighestRatedMovie.COL_MOVIE_ID + " TEXT NOT NULL, "
                + MoviesContract.HighestRatedMovie.COL_TRAILERS + " TEXT ,  " // NULLABLE
                + MoviesContract.HighestRatedMovie.COL_REVIEW + " TEXT " // NULLABLE
                + ");";

        // All of it should be Nullable since it should be empty at first
        final String CREATE_USER_FAVORITE_MOVIE_TABLE = "CREATE TABLE "
                + MoviesContract.FavoriteMovies.TABLE_NAME + " ("
                + MoviesContract.FavoriteMovies._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MoviesContract.FavoriteMovies.COL_ORIGINAL_TITLE + " TEXT , "
                + MoviesContract.FavoriteMovies.COL_MOVIE_POSTER + " TEXT , "
                + MoviesContract.FavoriteMovies.COL_MOVIE_SYNOPSIS + " TEXT , "
                + MoviesContract.FavoriteMovies.COL_RATINGS + " TEXT , "
                + MoviesContract.FavoriteMovies.COL_RELEASE_DATE + " TEXT ,  "
                + MoviesContract.FavoriteMovies.COL_MOVIE_ID + " TEXT , "
                + MoviesContract.FavoriteMovies.COL_TRAILERS + " TEXT ,  "
                + MoviesContract.FavoriteMovies.COL_REVIEW + " TEXT "
                + ");";

        // Create the tables here
        sqLiteDatabase.execSQL(CREATE_POPULAR_MOVIE_TABLE);
        sqLiteDatabase.execSQL(CREATE_HIGHEST_RATED_MOVIE_TABLE);
        sqLiteDatabase.execSQL(CREATE_USER_FAVORITE_MOVIE_TABLE);
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
            db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.FavoriteMovies.TABLE_NAME);
            onCreate(db);
        }

    }
}
