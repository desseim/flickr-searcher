package net.guillaume.flickrsimplesearcher.ui;

import android.provider.SearchRecentSuggestions;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.SearchView;

import com.google.common.base.Strings;
import com.squareup.otto.Bus;

import net.guillaume.flickrsimplesearcher.control.ImageSearchController;
import net.guillaume.flickrsimplesearcher.data.ImageBasicData;
import net.guillaume.flickrsimplesearcher.data.LocationData;
import net.guillaume.flickrsimplesearcher.inject.ForActivity;
import net.guillaume.flickrsimplesearcher.inject.InjectionNames;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/*package*/ class ImageSearchViewController {

    private static final String LOG_TAG = ImageSearchViewController.class.getSimpleName();

    @Inject                                           ImageSearchController   mImageSearchController;
    @Inject @ForActivity                              Bus                     mActivityBus;
    @Inject @Named(InjectionNames.SEARCH_INFO_IMAGES) SearchRecentSuggestions mImageSearchRecentSuggestions;

    /**
     * @param query the query to search ; if null will be taken from the searchView, and if still null or empty no search will be initiated
     */
    public void onImageSearch(final SearchView searchView, final CompoundButton includeLocationView, final @Nullable String query, final @Nullable LocationData lastLocation) {
        searchView.clearFocus();

        final String searchQuery = query != null ? query : searchView.getQuery().toString();
        if (!Strings.isNullOrEmpty(searchQuery)) {
            final boolean includeLocation = includeLocationView.isChecked();

            // initiate the search to the API
            //TODO handle case with HW keyboards: https://code.google.com/p/android/issues/detail?id=24599
            mImageSearchController
                    .searchImages(searchQuery, includeLocation, lastLocation)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<ImageBasicData>>() {
                        @Override public void onCompleted() {
                            // nothing to do here
                        }

                        @Override public void onError(final Throwable exception) {
                            Log.w(LOG_TAG, "Exception on trying to search for images", exception);
                            mActivityBus.post(new ImageSearchActivityEvents.ImageSearchFailedEvent(exception));
                        }

                        @Override public void onNext(final List<ImageBasicData> imageBasicData) {
                            // this one isn't a UI event, so just use the event bus directly:
                            mActivityBus.post(new ImageSearchActivityEvents.ImageSearchNewResultEvent(searchQuery, imageBasicData));
                        }
                    });

            // record the query in the search history NOW, even if the query fails to make it easier for the user to retry
            mImageSearchRecentSuggestions.saveRecentQuery(searchQuery, null);
        }
    }

}
