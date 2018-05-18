package com.example.android.popularmoviebeta.Utilities;

import android.content.ContentValues;

import com.example.android.popularmoviebeta.Data.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by diazagasatya on 5/17/18.
 */
public final class MoviesJsonUtils {

    /*
     * The list of movies listed from the API http request.
     * This will later be in the form of JSON-ARRAY
     */
    public static final String RESULTS = "results";

    /*
     * The key's needed to retrieve expected information for the user.
     * This will be the keys for the values
     */
    public static final String ORIGINAL_TITLE =  "original_title";
    public static final String IMAGE_PATH = "backdrop_path";
    public static final String MOVIE_SYSNOPSIS = "overview";
    public static final String USER_RATING = "vote_average";
    public static final String RELEASE_DATE = "release_date";


    // Use this static string as a confirmation of http request
    public static final String HTTP_RESPONSE_CONFIRMATION = "cod";
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    public static final String IMAGE_SIZE = "w185/";

    /**
     * This method will return an array of content values from JSON http response
     * @param jsonStringResponse        JSON String response from API
     * @return
     * @throws JSONException
     */
    public static ContentValues[] getMoviesContentValuesFromJson(String jsonStringResponse)
        throws JSONException {

        // Instantiate the string response as a JSONObject to parse
        JSONObject moviesList = new JSONObject(jsonStringResponse);

        // Make sure that thee http request is successful
        if(moviesList.has(HTTP_RESPONSE_CONFIRMATION)) {
            int errorCode = moviesList.getInt(HTTP_RESPONSE_CONFIRMATION);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    // In this case the http response is not found
                    return null;
                default:
                    // In this case the server might be down
                    return null;
            }
        }

        // Grab the total results from the JSON Object
        JSONArray jsonMovieResults = moviesList.getJSONArray(RESULTS);
        int numberOfMovies = jsonMovieResults.length();

        // Instantiate the CV Array
        ContentValues[] moviesContentValues = new ContentValues[numberOfMovies];

        /*
         * Start looping for every object inside the results and grab the information
         * needed and store it in a Content Value object and store it in a Content Value
         * Array to return back to the Database for Bulk Insertion
         */
        for(int i = 0; i < numberOfMovies; i++) {

            String originalTitle, imagePath, movieSynopsis, userRating, releaseDate;

            // Get the object reference of the movie list JSONArray
            JSONObject movieObject = jsonMovieResults.getJSONObject(i);

            // Instantiate a Content Value Object that holds the values
            ContentValues movieValues = new ContentValues();

            // Grab the JSON values in the JSONobject
            originalTitle = movieObject.getString(ORIGINAL_TITLE);
            imagePath = BASE_IMAGE_URL + IMAGE_SIZE + movieObject.getString(IMAGE_PATH);
            movieSynopsis = movieObject.getString(MOVIE_SYSNOPSIS);
            userRating = movieObject.getString(USER_RATING);
            releaseDate = movieObject.getString(RELEASE_DATE);

            // Put the string values inside the Table
            movieValues.put(MoviesContract.PopularMovie.COL_ORIGINAL_TITLE, originalTitle);
            movieValues.put(MoviesContract.PopularMovie.COL_MOVIE_POSTER, imagePath);
            movieValues.put(MoviesContract.PopularMovie.COL_MOVIE_SYSNOPSIS, movieSynopsis);
            movieValues.put(MoviesContract.PopularMovie.COL_RATINGS, userRating);
            movieValues.put(MoviesContract.PopularMovie.COL_RELEASE_DATE, releaseDate);

            // Insert the content values inside the CVArray
            moviesContentValues[i] = movieValues;
        }

        return moviesContentValues;

    }

}
