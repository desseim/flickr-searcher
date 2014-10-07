package net.guillaume.flickrsimplesearcher.model;

import android.content.SearchRecentSuggestionsProvider;

public class ImageSearchSuggestionProvider extends SearchRecentSuggestionsProvider {

    public final static String IMAGE_SEARCH_SUGGESTION_AUTHORITY = "net.guillaume.flickrsimplesearcher.ImageSearchSuggestionProvider";
    public final static int IMAGE_SEARCH_SUGGESTION_MODE = DATABASE_MODE_QUERIES;

    public ImageSearchSuggestionProvider() {
        setupSuggestions(IMAGE_SEARCH_SUGGESTION_AUTHORITY, IMAGE_SEARCH_SUGGESTION_MODE);
    }

}
