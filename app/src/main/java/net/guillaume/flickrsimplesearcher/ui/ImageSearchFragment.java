package net.guillaume.flickrsimplesearcher.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SearchView;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import net.guillaume.flickrsimplesearcher.LocationAwareBaseFragment;
import net.guillaume.flickrsimplesearcher.LocationListener;
import net.guillaume.flickrsimplesearcher.R;
import net.guillaume.flickrsimplesearcher.data.ImageBasicData;
import net.guillaume.flickrsimplesearcher.data.LocationData;
import net.guillaume.flickrsimplesearcher.inject.ForActivity;
import net.guillaume.flickrsimplesearcher.inject.ForApplication;
import net.guillaume.flickrsimplesearcher.rest.ImageSearchController;

import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ImageSearchFragment extends LocationAwareBaseFragment {

    private static final String LOG_TAG                            = ImageSearchFragment.class.getSimpleName();
    private static final String FRAGMENT_TAG_RESULT                = "ImageSearchFragment.fragment_result";
    private static final String FRAGMENT_BACKSTACK_TAG_SHOW_DETAIL = "ImageSearchFragment.fragment_backstack_show_detail";

    @Inject @ForApplication Resources             mApplicationResources;
    @Inject                 ImageSearchController mImageSearchController;
    @Inject @ForActivity    Bus                   mActivityBus;
    @Inject @ForApplication Bus                   mApplicationBus;
    @Inject                 FragmentManager       mFragmentManager;

    private Optional<LocationData> mLastLocation = Optional.absent();

    public ImageSearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_search, container, false);
    }

    @Override public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Preconditions.checkNotNull(mActivityBus, "Injection didn't occur yet");
        mActivityBus.register(this);
        mApplicationBus.register(this);

        final View rootView = getView();
        Preconditions.checkNotNull(rootView, "Activity root view not set");

        // initialize search view
        final SearchView searchView = (SearchView) rootView.findViewById(R.id.image_search_view);
        Preconditions.checkNotNull(searchView, "Didn't find search view");
        final CheckBox includeLocationView = (CheckBox) rootView.findViewById(R.id.location_check);
        Preconditions.checkNotNull(includeLocationView, "Didn't find location check view");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(final String query) {
                searchView.clearFocus();

                //TODO handle case with HW keyboards: https://code.google.com/p/android/issues/detail?id=24599
                final boolean includeLocation = includeLocationView.isChecked();
                mImageSearchController
                        .searchImages(query, includeLocation ? mLastLocation.orNull() : null)
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
                                mActivityBus.post(new ImageSearchActivityEvents.ImageSearchNewResultEvent(query, imageBasicData));
                            }
                        });

                return true;
            }

            @Override public boolean onQueryTextChange(final String query) {
                return false;
            }
        });

        if (savedInstanceState == null) {
            // first start of the app, show an indication message:
            mActivityBus.post(new ImageSearchActivityEvents.ShowMessageEvent(
                    MessageType.INFO,
                    mApplicationResources.getString(R.string.start_search_info)));
        }

        updateIncludeLocationCheckboxStatus();
    }

    @Subscribe public void onImageSearchNewResult(final ImageSearchActivityEvents.ImageSearchNewResultEvent newResultEvent) {
        final List<ImageBasicData> results = newResultEvent.getResults();

        if (results.size() > 0) {  // there are results ; go back to the result fragment and update it, or create one
            mFragmentManager.popBackStack(FRAGMENT_BACKSTACK_TAG_SHOW_DETAIL, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            ImageSearchResultFragment searchResultFragment = (ImageSearchResultFragment)mFragmentManager.findFragmentByTag(FRAGMENT_TAG_RESULT);
            if (searchResultFragment == null) {
                searchResultFragment = ImageSearchResultFragment.create(results);

                final FragmentTransaction showSearchResultFragmentTransaction = mFragmentManager.beginTransaction();
                showSearchResultFragmentTransaction.replace(R.id.background_fragment, searchResultFragment, FRAGMENT_TAG_RESULT);
                showSearchResultFragmentTransaction.commit();
            } else {
                searchResultFragment.updateResultData(results);
            }
        } else {  // no results ; show a message
            mActivityBus.post(new ImageSearchActivityEvents.ShowMessageEvent(
                    MessageType.FAILURE,
                    mApplicationResources.getString(R.string.no_result_message, newResultEvent.getQueryString())));
        }
    }

    @Subscribe public void onImageSearchFailed(final ImageSearchActivityEvents.ImageSearchFailedEvent imageSearchFailedEvent) {
        final Optional<Throwable> exception = imageSearchFailedEvent.getException();

        mActivityBus.post(new ImageSearchActivityEvents.ShowMessageEvent(
                MessageType.ERROR,
                exception.isPresent() ? exception.get().getLocalizedMessage() : mApplicationResources.getString(R.string.unknown_error_occurred)));
    }

    @Subscribe public void onShowMessage(final ImageSearchActivityEvents.ShowMessageEvent showMessageEvent) {
        // first go back to the root of the backstack
        mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // then show the message fragment:
        final MessageFragment newMessageFragment = MessageFragment.create(showMessageEvent.getMessageType(), showMessageEvent.getMessage());
        mFragmentManager.beginTransaction()
                        .replace(R.id.background_fragment, newMessageFragment)
                        .commit();
    }

    @Subscribe public void onShowImageDetail(final ImageSearchActivityEvents.ImageDetailShowEvent imageDetailShowEvent) {
        final View rootView = getView();
        if (rootView != null) {
            final Fragment imageDetailFragment = ImageSearchDetailFragment.create(imageDetailShowEvent.getImageToShow());
            final FragmentTransaction showDetailFragmentTransaction = mFragmentManager.beginTransaction();
            showDetailFragmentTransaction.replace(R.id.background_fragment, imageDetailFragment, null);
            showDetailFragmentTransaction.addToBackStack(FRAGMENT_BACKSTACK_TAG_SHOW_DETAIL);
            showDetailFragmentTransaction.commit();
        }
    }

    @Subscribe public void onLocationChange(final LocationListener.LocationChangeEvent locationChangeEvent) {
        mLastLocation = locationChangeEvent.getNewLocation();
        updateIncludeLocationCheckboxStatus();
    }

    @Override public void onDetach() {
        mActivityBus.unregister(this);
        mApplicationBus.unregister(this);

        super.onDetach();
    }

    private void updateIncludeLocationCheckboxStatus() {
        final View rootView = getView();
        if (rootView != null) {
            final CheckBox includeLocationView = (CheckBox) rootView.findViewById(R.id.location_check);
            Preconditions.checkNotNull(includeLocationView, "Didn't find the location view");
            includeLocationView.setEnabled(mLastLocation.isPresent());
        }
    }

}
