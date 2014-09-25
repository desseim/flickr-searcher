package net.guillaume.flickrsimplesearcher.ui;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import net.guillaume.flickrsimplesearcher.BaseFragment;
import net.guillaume.flickrsimplesearcher.R;
import net.guillaume.flickrsimplesearcher.data.ImageData;
import net.guillaume.flickrsimplesearcher.inject.ForActivity;
import net.guillaume.flickrsimplesearcher.inject.ForApplication;
import net.guillaume.flickrsimplesearcher.rest.ImageSearchController;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ImageSearchFragment extends BaseFragment {

    private static final String LOG_TAG = ImageSearchFragment.class.getSimpleName();

    @Inject @ForApplication Resources             mApplicationResources;
    @Inject                 ImageSearchController mImageSearchController;
    @Inject @ForActivity    Bus                   mActivityBus;
    @Inject                 Picasso               mPicasso;

    @Inject ImageSearchResultAdapter           mImageSearchResultAdapter;
    @Inject ImageSearchActivityUiActionHandler mUiActionHandler;

    private final MessageViewHelper                                       mMessageViewHelper    = new MessageViewHelper();
    private final ImageSearchActivityUiActionHandler.UiActionEventVisitor mUiActionEventVisitor = new UiActionEventDispatcher();

    private final ImmutableSet<Integer> mBackgroundViewResourceIds = ImmutableSet.of(
            R.id.grid_view,
            R.id.message,
            R.id.image_detail
    );

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

        if (savedInstanceState != null) mImageSearchResultAdapter.setDataFromBundle(savedInstanceState);

        final Activity activity = getActivity();
        final View rootView = getView();
        Preconditions.checkNotNull(rootView, "Activity root view not set");

        // initialize search view
        final SearchView searchView = (SearchView) rootView.findViewById(R.id.image_search_view);
        Preconditions.checkNotNull(searchView, "Didn't find search view");

        final SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
        final SearchableInfo searchableInfo = searchManager.getSearchableInfo(activity.getComponentName());
        //TODO preconditions on not null otherwise activity isn't searchable

//FIXME remove        searchView.setSearchableInfo(searchableInfo);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(final String query) {
                searchView.clearFocus();

                //TODO handle case with HW keyboards: https://code.google.com/p/android/issues/detail?id=24599
                mImageSearchController
                        .searchImages(query)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<List<ImageData>>() {
                            @Override public void onCompleted() {
                                // nothing to do here
                            }

                            @Override public void onError(final Throwable exception) {
                                Log.w(LOG_TAG, "Exception on trying to search for images", exception);
                                mUiActionHandler.handleUiActionEvent(new ImageSearchActivityUiActionHandler.ImageSearchFailedEvent(exception));
                            }

                            @Override public void onNext(final List<ImageData> imageData) {
                                // this one isn't a UI event, so just use the event bus directly:
                                mActivityBus.post(new ImageSearchActivityUiActionHandler.ImageSearchNewResultEvent(query, imageData));
                            }
                        });

                return true;
            }

            @Override public boolean onQueryTextChange(final String query) {
                return false;
            }
        });

        // initialize grid view
        final GridView gridView = (GridView)rootView.findViewById(R.id.grid_view);
        Preconditions.checkNotNull(gridView, "Didn't find grid view");

        gridView.setAdapter(mImageSearchResultAdapter);

        final View messageView = rootView.findViewById(R.id.message);
        Preconditions.checkNotNull(messageView, "Didn't find message view");
        if (savedInstanceState == null) {
            // first start of the app, show an indication message:
            mUiActionHandler.handleUiActionEvent(new ImageSearchActivityUiActionHandler.ShowMessageEvent(
                    MessageViewHelper.MessageType.INFO,
                    mApplicationResources.getString(R.string.start_search_info)));
        }
    }

    /*package*/ boolean onBackPress() {
        final View rootView = getView();
        if (rootView != null) {
            // only if we're showing an image details, go back to the search results:
            if (isBackgroundViewShowing(rootView, R.id.image_detail)) {
                mUiActionHandler.handleUiActionEvent(new ImageSearchActivityUiActionHandler.ImageSearchResultShowEvent());
                return true;
            }
        }
        return false;
    }

    @Override public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        mImageSearchResultAdapter.saveDataToBundle(outState);
        mMessageViewHelper.saveMessageViewState(outState);
    }

    @Subscribe public void onImageSearchNewResult(final ImageSearchActivityUiActionHandler.ImageSearchNewResultEvent newResultEvent) {
        final List<ImageData> results = newResultEvent.getResults();
        mImageSearchResultAdapter.setData(results);

        if (results.size() > 0) {
            // then just show the last results:
            mUiActionHandler.handleUiActionEvent(new ImageSearchActivityUiActionHandler.ImageSearchResultShowEvent());
        } else {
            mUiActionHandler.handleUiActionEvent(new ImageSearchActivityUiActionHandler.ShowMessageEvent(
                    MessageViewHelper.MessageType.FAILURE,
                    mApplicationResources.getString(R.string.no_result_message, newResultEvent.getQueryString())));
        }
    }

    //XXX unfortunately we have to do this for the @Produce method to be called upon this object registration to the event bus
    @Subscribe public void onUiActionEvent(final ImageSearchActivityUiActionHandler.UiActionEvent event) {
        //XXX so we end up having to double dispatch... in the future handling the different event types "manually" rather than with polymorphism may be a good idea
        event.accept(mUiActionEventVisitor);
    }

    private void onShowImageSearchResult(final ImageSearchActivityUiActionHandler.ImageSearchResultShowEvent imageSearchResultShowEvent) {
        final View rootView = getView();
        if (rootView != null) {
            switchBackgroundViewTo(rootView, R.id.grid_view);
        }
    }

    private void onImageSearchFailed(final ImageSearchActivityUiActionHandler.ImageSearchFailedEvent imageSearchFailedEvent) {
        final View rootView = getView();
        if (rootView != null) {
            final View messageView = rootView.findViewById(R.id.message);
            final Optional<Throwable> exception = imageSearchFailedEvent.getException();

            mMessageViewHelper.updateMessageView(
                    messageView,
                    MessageViewHelper.MessageType.ERROR,
                    exception.isPresent() ? exception.get().getLocalizedMessage() : "Unknown error occurred");

            switchBackgroundViewTo(rootView, R.id.message);
        }
    }

    private void onShowMessage(final ImageSearchActivityUiActionHandler.ShowMessageEvent showMessageEvent) {
        final View rootView = getView();
        if (rootView != null) {
            final View messageView = rootView.findViewById(R.id.message);
            mMessageViewHelper.updateMessageView(
                    messageView,
                    showMessageEvent.getMessageType(),
                    showMessageEvent.getMessage());

            switchBackgroundViewTo(rootView, R.id.message);
        }
    }

    private void onShowImageDetail(final ImageSearchActivityUiActionHandler.ImageDetailShowEvent imageDetailShowEvent) {
        final View rootView = getView();
        if (rootView != null) {
            final View imageDetailView = rootView.findViewById(R.id.image_detail);
            final ImageView imageView = (ImageView)imageDetailView.findViewById(R.id.image);
            final TextView titleView = (TextView)imageDetailView.findViewById(R.id.title);
            final ImageData imageData = imageDetailShowEvent.getImageToShow();

            mPicasso.load(imageData.largeUri()).into(imageView);
            titleView.setText(imageData.title());

            switchBackgroundViewTo(rootView, R.id.image_detail);
        }
    }

    @Override public void onDetach() {
        mActivityBus.unregister(this);

        super.onDetach();
    }

    /**
     * In order to keep the search fragment always on top of the background, we just use several overlapping background views
     * which we show / hide dynamically.
     * This works here since there's not so much, but more functionality another `Activity` or `Fragment` would be appropriate.
     * @param viewToSwitchTo the view to show, others will be hidden
     */
    protected void switchBackgroundViewTo(final @Nonnull View rootView, final int viewToSwitchTo) {
        for (final int backgroundViewResourceId : mBackgroundViewResourceIds) {
            rootView.findViewById(backgroundViewResourceId).setVisibility(viewToSwitchTo == backgroundViewResourceId ? View.VISIBLE : View.GONE);
        }

        // don't change the search view visibility but make sure it doesn't take focus on other view changes
        final View searchView = rootView.findViewById(R.id.image_search_view);
//        searchView.clearFocus();
    }

    protected boolean isBackgroundViewShowing(final @Nonnull View rootView, final int viewResourceId) {
        final View view = rootView.findViewById(viewResourceId);
        return view.getVisibility() == View.VISIBLE;
    }


    private class UiActionEventDispatcher implements ImageSearchActivityUiActionHandler.UiActionEventVisitor {
        @Override public void visit(final ImageSearchActivityUiActionHandler.ImageSearchFailedEvent imageSearchFailedEvent) {
            onImageSearchFailed(imageSearchFailedEvent);
        }

        @Override public void visit(final ImageSearchActivityUiActionHandler.ShowMessageEvent showMessageEvent) {
            onShowMessage(showMessageEvent);
        }

        @Override
        public void visit(final ImageSearchActivityUiActionHandler.ImageSearchResultShowEvent imageSearchResultShowEvent) {
            onShowImageSearchResult(imageSearchResultShowEvent);
        }

        @Override public void visit(final ImageSearchActivityUiActionHandler.ImageDetailShowEvent imageDetailShowEvent) {
            onShowImageDetail(imageDetailShowEvent);
        }
    }
}
