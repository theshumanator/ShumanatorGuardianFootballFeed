package com.example.fatoumeh.shumanatorguardianfootballfeed;

import android.content.Context;
import android.text.TextUtils;
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

/**
 * Created by fatoumeh on 01/03/2018.
 */

public class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static int CONNECT_TIMEOUT;
    private static int READ_TIMEOUT;
    private static String REQUEST_METHOD;

    private static String JSON_RESPONSE_TAG;
    private static String JSON_RESULTS_TAG;
    private static String JSON_TAGS_TAG;
    private static String JSON_SECTION_TAG;
    private static String JSON_TITLE_TAG;
    private static String JSON_DATE_TAG;
    private static String JSON_URL_TAG;
    private static String JSON_TYPE_TAG;
    private static String JSON_CONTRIBUTOR_TAG;
    private static String AUTHOR_UNAVAILABLE;

    private QueryUtils(){
    }

    public static ArrayList<FootballFeed> fetchNewsFromURL(String url, Context context) {
        setUpStrings(context);
        URL requestURL=createURL(url);
        String jsonResponse=null;
        try {
            jsonResponse=makeHttpRequest(requestURL);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        ArrayList<FootballFeed> footballFeedArrayList=extractFootballFeedInfo(jsonResponse);
        return footballFeedArrayList;
    }

    private static void setUpStrings(Context context) {
        JSON_RESPONSE_TAG=context.getString(R.string.json_response_tag);
        JSON_RESULTS_TAG=context.getString(R.string.json_results_tag);
        JSON_TAGS_TAG=context.getString(R.string.json_tags_tag);
        JSON_SECTION_TAG=context.getString(R.string.json_section_tag);
        JSON_TITLE_TAG=context.getString(R.string.json_webTitle_tag);
        JSON_DATE_TAG=context.getString(R.string.json_webPublicationDate_tag);
        JSON_URL_TAG=context.getString(R.string.json_webUrl_tag);
        JSON_TYPE_TAG=context.getString(R.string.json_type_tag);
        JSON_CONTRIBUTOR_TAG=context.getString(R.string.json_contributor_tag);
        AUTHOR_UNAVAILABLE=context.getString(R.string.author_unavailable);
        CONNECT_TIMEOUT=Integer.parseInt(context.getString(R.string.connect_timeout));
        READ_TIMEOUT=Integer.parseInt(context.getString(R.string.read_timeout));
        REQUEST_METHOD=context.getString(R.string.get_request_method);
    }

    //generated the URL from the string
    private static URL createURL(String url) {
        URL requestURL=null;
        try {
            requestURL=new URL(url);
        } catch (MalformedURLException e){
            Log.e(LOG_TAG, "Error creating URL ", e);
        }
        return requestURL;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT) ;
            urlConnection.setRequestMethod(REQUEST_METHOD);
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the football JSON results.", e);
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
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static ArrayList<FootballFeed> extractFootballFeedInfo(String jsonResponse) {

        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        ArrayList<FootballFeed> footballFeeds = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONObject responseObject = baseJsonResponse.getJSONObject(JSON_RESPONSE_TAG);
            JSONArray resultsArray = responseObject.getJSONArray(JSON_RESULTS_TAG);

            if (resultsArray.length()>0) {

                for (int i=0; i<resultsArray.length(); i++) {

                    JSONObject headlineInfo = resultsArray.getJSONObject(i);
                    JSONArray tagsArray = headlineInfo.getJSONArray(JSON_TAGS_TAG);
                    //drill into the objects extracted above
                    String headlineSection=headlineInfo.getString(JSON_SECTION_TAG);
                    String headline=headlineInfo.getString(JSON_TITLE_TAG);
                    String timeFromJson = headlineInfo.getString(JSON_DATE_TAG);
                    String urlFromJson = headlineInfo.getString(JSON_URL_TAG);
                    String author=getAuthor(tagsArray);
                    footballFeeds.add(new FootballFeed(headlineSection, headline, author, timeFromJson, urlFromJson));
                }

            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the football feed JSON results", e);
        }
        return footballFeeds;
    }

    private static String getAuthor(JSONArray tagsArray) {
        //if author is unavailable we return this default string
        String author=AUTHOR_UNAVAILABLE;
        /*we need to count the number of contributors. if > 1 then we append
        first author name with et al*/
        int contributors=0;

        if (tagsArray.length()>0) {
            try {
                for (int a=0; a<tagsArray.length(); a++){
                    JSONObject tagItem=tagsArray.getJSONObject(a);

                    //we need to check if the type is contributor
                    String type=tagItem.getString(JSON_TYPE_TAG);
                    if (type.equalsIgnoreCase(JSON_CONTRIBUTOR_TAG)) {

                        String authorFromJson=tagItem.getString(JSON_TITLE_TAG);

                        if (!TextUtils.isEmpty(authorFromJson)) {
                            contributors++;
                            /*f the author is currently set to default so set it to
                            what the json has*/
                            if (author.equalsIgnoreCase(AUTHOR_UNAVAILABLE)) {
                                author=authorFromJson;
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the football feed JSON results", e);
            }
        }
        /*if there are more contributors, we will oonly save the first one and put et al
        for the rest*/
        if (contributors>1) {
            author=author+" et al";
        }
        return author;
    }
}
