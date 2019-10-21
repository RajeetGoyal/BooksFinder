package com.example.rajeetgoyal.booksfinder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    static List<Book> fetchBooksData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        Log.i(LOG_TAG,"Http request was successful or catched successfully.");
        return extractBooksListFromJSON(jsonResponse);
    }

    private static URL createUrl(String stringURL) {
        URL url = null;
        try {
            url = new URL(stringURL);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL", e);
        }
        return url;
    }

    private static Bitmap fetchThumbnail(String thumbnailUrl) {
        URL url = createUrl(thumbnailUrl);
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        }catch (IOException e){
            Log.e(LOG_TAG, "Problem parsing the thumbnail from the url");
        }
        return bmp;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = " ";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    private static List<Book> extractBooksListFromJSON(String jsonResponse) {

        List<Book> books = new ArrayList<>();
        Bitmap bitmap = null;
        try {
            JSONObject baseJSONResponse = new JSONObject(jsonResponse);
            int totalItems = baseJSONResponse.optInt("totalItems");
            if (totalItems != 0) {
                JSONArray itemsArray = baseJSONResponse.optJSONArray("items");
                for (int i = 0; i < Objects.requireNonNull(itemsArray).length(); i++) {
                    JSONObject currentItem = itemsArray.optJSONObject(i);
                    JSONObject volumeInfo = currentItem.optJSONObject("volumeInfo");
                    // Getting Thumbnail
                    JSONObject imageLinks = Objects.requireNonNull(volumeInfo).optJSONObject("imageLinks");
                    if (imageLinks != null) {
                        String thumbnailLink = imageLinks.optString("smallThumbnail");
                        bitmap = fetchThumbnail(thumbnailLink);
                    }
                    // Getting titles
                    String title = volumeInfo.optString("title");
                    // Getting authors
                    JSONArray authorsArray = volumeInfo.optJSONArray("authors");
                    StringBuilder authors = new StringBuilder(" ");
                    for (int j = 0; j < Objects.requireNonNull(authorsArray).length(); j++) {
                        if(j == 0) authors = new StringBuilder(authorsArray.optString(j));
                        else {
                            authors.append(", ").append(authorsArray.optString(j));
                        }
                    }
                    // Getting publishers
                    String publisher = volumeInfo.optString("publisher");
                    // Getting infoLink
                    String infoLink = volumeInfo.optString("infoLink");
                    // Adding parsed result in List<Books> for a book.
                    books.add(new Book(bitmap, title, authors.toString(), publisher, infoLink));
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the books JSON results", e);
            return null;
        }
        return books;
    }
}


