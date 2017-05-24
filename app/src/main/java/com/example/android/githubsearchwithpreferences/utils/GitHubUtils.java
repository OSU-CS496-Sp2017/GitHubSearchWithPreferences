package com.example.android.githubsearchwithpreferences.utils;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hessro on 4/25/17.
 */

public class GitHubUtils {

    private final static String GITHUB_SEARCH_BASE_URL = "https://api.github.com/search/repositories";
    private final static String GITHUB_SEARCH_QUERY_PARAM = "q";
    private final static String GITHUB_SEARCH_SORT_PARAM = "sort";
    private final static String GITHUB_SEARCH_LANGUAGE_PARAM = "language";
    private final static String GITHUB_SEARCH_USER_PARAM = "user";
    private final static String GITHUB_SEARCH_IN_PARAM = "in";
    private final static String GITHUB_SEARCH_IN_NAME = "name";
    private final static String GITHUB_SEARCH_IN_DESCRIPTION = "description";
    private final static String GITHUB_SEARCH_IN_README = "readme";

    public static class SearchResult implements Serializable {
        public static final String EXTRA_SEARCH_RESULT = "GitHubUtils.SearchResult";
        public String fullName;
        public String description;
        public String htmlURL;
        public int stars;
    }

    public static String buildGitHubSearchURL(String searchQuery, String sort, String language,
                                              String user, boolean searchInName,
                                              boolean searchInDescription, boolean searchInReadme) {

        Uri.Builder builder = Uri.parse(GITHUB_SEARCH_BASE_URL).buildUpon();

        if (!sort.equals("")) {
            builder.appendQueryParameter(GITHUB_SEARCH_SORT_PARAM, sort);
        }

        String queryValue = searchQuery;
        if (!language.equals("")) {
            queryValue += " " + GITHUB_SEARCH_LANGUAGE_PARAM + ":" + language;
        }

        if (!user.equals("")) {
            queryValue += " " + GITHUB_SEARCH_USER_PARAM + ":" + user;
        }

        /*
         * The code below is a slightly complicated little block that does two things:
         *   1. If all of searchInName, searchInDescription, and searchInReadme are false, specifies
         *      that the name and description should be searched.
         *   2. If any of those values are specified, builds up a comma-separated list of the
         *      corresponding strings to specify what to search in.
         */
        queryValue += " " + GITHUB_SEARCH_IN_PARAM + ":";
        if (!searchInName && !searchInDescription && !searchInReadme) {
            queryValue += GITHUB_SEARCH_IN_NAME + "," + GITHUB_SEARCH_IN_DESCRIPTION;
        } else {
            String concatStr = "";
            if (searchInName) {
                queryValue += concatStr + GITHUB_SEARCH_IN_NAME;
                concatStr = ",";
            }
            if (searchInDescription) {
                queryValue += concatStr + GITHUB_SEARCH_IN_DESCRIPTION;
                concatStr = ",";
            }
            if (searchInReadme) {
                queryValue += concatStr + GITHUB_SEARCH_IN_README;
            }
        }

        builder.appendQueryParameter(GITHUB_SEARCH_QUERY_PARAM, queryValue);

        return builder.build().toString();
    }

    public static ArrayList<SearchResult> parseGitHubSearchResultsJSON(String searchResultsJSON) {
        try {
            JSONObject searchResultsObj = new JSONObject(searchResultsJSON);
            JSONArray searchResultsItems = searchResultsObj.getJSONArray("items");

            ArrayList<SearchResult> searchResultsList = new ArrayList<SearchResult>();
            for (int i = 0; i < searchResultsItems.length(); i++) {
                SearchResult searchResult = new SearchResult();
                JSONObject searchResultItem = searchResultsItems.getJSONObject(i);
                searchResult.fullName = searchResultItem.getString("full_name");
                searchResult.description = searchResultItem.getString("description");
                searchResult.htmlURL = searchResultItem.getString("html_url");
                searchResult.stars = searchResultItem.getInt("stargazers_count");
                searchResultsList.add(searchResult);
            }
            return searchResultsList;
        } catch (JSONException e) {
            return null;
        }
    }
}
