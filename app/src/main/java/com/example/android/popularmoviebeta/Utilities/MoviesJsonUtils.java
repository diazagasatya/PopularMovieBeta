package com.example.android.popularmoviebeta.Utilities;

import android.content.ContentValues;
import android.util.Log;

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

    // Table category distinguisher
    public static final int ID_POPULAR_TABLE = 100;
    public static final int ID_TOP_RATED_TABLE = 200;

    /*
     * The key's needed to retrieve expected information for the user.
     * This will be the keys for the values
     */
    public static final String ORIGINAL_TITLE =  "original_title";
    public static final String IMAGE_PATH = "backdrop_path";
    public static final String MOVIE_SYNOPSIS = "overview";
    public static final String USER_RATING = "vote_average";
    public static final String RELEASE_DATE = "release_date";
    public static final String MOVIE_ID = "id";
    public static final String TRAILER_SITE = "site";
    public static final String TRAILER_KEY = "key";
    public static final String YOUTUBE = "YouTube";

    // Use this static string as a confirmation of http request
    public static final String HTTP_RESPONSE_CONFIRMATION = "cod";

    // Use this static string to build IMAGE URL
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    public static final String IMAGE_SIZE = "w185/";

    // Use this static string to build the youtube URL
    public static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";


    /**
     * This method will return a content values for the trailers
     * @param jsonStringResponse            Http Response as String
     * @param tableId                       Table identification
     * @return
     */
    public static ContentValues getTrailersFromJson(String jsonStringResponse, int tableId)
            throws JSONException {

        // Instantiate the string response as a JSONObject
        JSONObject trailerList = new JSONObject(jsonStringResponse);

        // Make sure that thee http request is successful
        if(trailerList.has(HTTP_RESPONSE_CONFIRMATION)) {
            int errorCode = trailerList.getInt(HTTP_RESPONSE_CONFIRMATION);

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

        // Grab the results as a JSONArray
        JSONArray resultArray = trailerList.getJSONArray(RESULTS);
        int resultArrayLength = resultArray.length();

        // Instantiate a Content Value Object
        ContentValues movieTrailersValue = new ContentValues();

        // This String will hold all of the trailers
        String trailerLinks = "";

        /*
         * Start looping for every object inside the JSONArray and parse it to
         * put inside CV, seperate the youtube keys with a comma
         */
        for(int i = 0; i < resultArrayLength; i++) {

            String youtubeKey;

            // Grab the first object inside the array
            JSONObject trailerObject = resultArray.getJSONObject(i);

            // Only grab the trailers from Youtube
            String youtubeConfirmation = trailerObject.getString(TRAILER_SITE);

            // Make sure the key is for a Youtube URL
            if(YOUTUBE.equals(youtubeConfirmation)) {

                youtubeKey = BASE_YOUTUBE_URL + trailerObject.getString(TRAILER_KEY);

                trailerLinks = trailerLinks + youtubeKey;

                // Log the youtube key for debugging purposes
                Log.v("Content Value", youtubeKey);

            } else {
                continue;
            }

            // Add commas to seperate the keys
            if(i < resultArrayLength - 1) {

                trailerLinks = trailerLinks + ",";
            }
        }

        // Print out the whole youtube key for debugging purposes
        Log.v("YOUTUBE LINKS", trailerLinks);

        // Put the trailerLinks inside the content values
        switch (tableId) {

            case ID_POPULAR_TABLE:
                movieTrailersValue.put(MoviesContract.PopularMovie.COL_TRAILERS, trailerLinks);
                break;
            case ID_TOP_RATED_TABLE:
                movieTrailersValue.put(MoviesContract.HighestRatedMovie.COL_TRAILERS, trailerLinks);
        }

        // Return the movie trailer content value to update
        return movieTrailersValue;
    }

    /**
     * This method will return the reviews of particular movie
     * @param jsonStringResponse
     * @param tableId
     * @return
     * @throws JSONException
     */
    public static ContentValues getReviewsFromJson(String jsonStringResponse, int tableId)
        throws JSONException {

        // TODO(3) IMPLEMENT REVIEWS ON DETAIL ACTIVITY
        // TODO(4) PARSE THE RESULTS API

        // Instantiate the String Response as A JSON Object
        JSONObject reviewLists = new JSONObject(jsonStringResponse);

        // Make sure that thee http request is successful
        if(reviewLists.has(HTTP_RESPONSE_CONFIRMATION)) {
            int errorCode = reviewLists.getInt(HTTP_RESPONSE_CONFIRMATION);

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

        // Grab the results as JSONArray
        JSONArray reviewResults = reviewLists.getJSONArray(RESULTS);


        return null;

    }

    /**
     * This method will return an array of content values from JSON http response
     * @param jsonStringResponse        JSON String response from API
     * @return
     * @throws JSONException
     */
    public static ContentValues[] getPopularMoviesContentValuesFromJson(String jsonStringResponse)
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

            String originalTitle, imagePath, movieSynopsis, userRating, releaseDate, movieId;

            // Get the object reference of the movie list JSONArray
            JSONObject movieObject = jsonMovieResults.getJSONObject(i);

            // Instantiate a Content Value Object that holds the values
            ContentValues movieValues = new ContentValues();

            // Grab the JSON values in the JSONobject
            originalTitle = movieObject.getString(ORIGINAL_TITLE);
            imagePath = BASE_IMAGE_URL + IMAGE_SIZE + movieObject.getString(IMAGE_PATH);
            movieSynopsis = movieObject.getString(MOVIE_SYNOPSIS);
            userRating = movieObject.getString(USER_RATING);
            releaseDate = movieObject.getString(RELEASE_DATE);
            movieId = movieObject.getString(MOVIE_ID);

            Log.v("Content Values POPULARITY: ",
                    originalTitle + " " + imagePath + " "
                            + movieSynopsis + " " + userRating + " "
                            + releaseDate + " " + movieId);

            // Put the string values inside the Table
            movieValues.put(MoviesContract.PopularMovie.COL_ORIGINAL_TITLE, originalTitle);
            movieValues.put(MoviesContract.PopularMovie.COL_MOVIE_POSTER, imagePath);
            movieValues.put(MoviesContract.PopularMovie.COL_MOVIE_SYNOPSIS, movieSynopsis);
            movieValues.put(MoviesContract.PopularMovie.COL_RATINGS, userRating);
            movieValues.put(MoviesContract.PopularMovie.COL_RELEASE_DATE, releaseDate);
            movieValues.put(MoviesContract.PopularMovie.COL_MOVIE_ID, movieId);

            // Insert the content values inside the CVArray
            moviesContentValues[i] = movieValues;
        }

        return moviesContentValues;
    }

    /**
     * This method will return an array of content values from JSON http response
     * @param jsonStringResponse        JSON String response from API
     * @return
     * @throws JSONException
     */
    public static ContentValues[] getTopRatedMoviesContentValuesFromJson(String jsonStringResponse)
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

            String originalTitle, imagePath, movieSynopsis, userRating, releaseDate, movieId;

            // Get the object reference of the movie list JSONArray
            JSONObject movieObject = jsonMovieResults.getJSONObject(i);

            // Instantiate a Content Value Object that holds the values
            ContentValues movieValues = new ContentValues();

            // Grab the JSON values in the JSONobject
            originalTitle = movieObject.getString(ORIGINAL_TITLE);
            imagePath = BASE_IMAGE_URL + IMAGE_SIZE + movieObject.getString(IMAGE_PATH);
            movieSynopsis = movieObject.getString(MOVIE_SYNOPSIS);
            userRating = movieObject.getString(USER_RATING);
            releaseDate = movieObject.getString(RELEASE_DATE);
            movieId = movieObject.getString(MOVIE_ID);

            Log.v("Content Values TOP RATED: ", originalTitle + " " + imagePath + " " + movieSynopsis + " " + userRating + " " + releaseDate + " " + movieId);

            // Put the string values inside the Table
            movieValues.put(MoviesContract.HighestRatedMovie.COL_ORIGINAL_TITLE, originalTitle);
            movieValues.put(MoviesContract.HighestRatedMovie.COL_MOVIE_POSTER, imagePath);
            movieValues.put(MoviesContract.HighestRatedMovie.COL_MOVIE_SYNOPSIS, movieSynopsis);
            movieValues.put(MoviesContract.HighestRatedMovie.COL_RATINGS, userRating);
            movieValues.put(MoviesContract.HighestRatedMovie.COL_RELEASE_DATE, releaseDate);
            movieValues.put(MoviesContract.HighestRatedMovie.COL_MOVIE_ID, movieId);

            // Insert the content values inside the CVArray
            moviesContentValues[i] = movieValues;
        }

        return moviesContentValues;
    }

}
