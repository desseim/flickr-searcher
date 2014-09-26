package net.guillaume.flickrsimplesearcher.inject;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.view.LayoutInflater;

import com.google.common.base.Preconditions;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;

import net.guillaume.flickrsimplesearcher.ui.ImageSearchActivity;
import net.guillaume.flickrsimplesearcher.ui.ImageSearchDetailFragment;
import net.guillaume.flickrsimplesearcher.ui.ImageSearchFragment;
import net.guillaume.flickrsimplesearcher.ui.ImageSearchResultFragment;
import net.guillaume.flickrsimplesearcher.ui.MessageFragment;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                ImageSearchActivity.class,
                ImageSearchFragment.class,
                ImageSearchResultFragment.class,
                ImageSearchDetailFragment.class,
                MessageFragment.class
        },
        includes = {
                NetworkModule.class
        },
        library = true)
public class ActivityModule {

    private Activity mActivity;

    public ActivityModule(final @Nonnull Activity activity) {
        attachActivity(activity);
    }

    /**
     * Detaches the currently attach activity from this module.
     * Call when an activity is destroyed but its module is preserved (e.g. on configuration change) to prevent a leak of the attached activity.
     */
    public synchronized void detachActivity() {
        mActivity = null;
    }

    public synchronized void attachActivity(final @Nonnull Activity activity) {
        Preconditions.checkNotNull(activity, "Activity to attach the module to is null ; most likely a bug");
        mActivity = activity;
    }

    @Provides synchronized Activity provideActivity() {
        Preconditions.checkNotNull(mActivity, "Trying to inject an activity while it isn't attached to its module, something is wrong");
        return mActivity;
    }

    @Provides @ForActivity synchronized Context provideActivityContext() {
        Preconditions.checkNotNull(mActivity, "Trying to inject an activity context while the activity isn't attached to its module, something is wrong");
        return mActivity;
    }

    @Provides @Singleton @ForActivity Bus provideActivityBus() { return new Bus(); }

    @Provides LayoutInflater provideLayoutInflater(final Activity activity) {
        return LayoutInflater.from(activity);
    }

    @Provides FragmentManager provideFragmentManager(final Activity activity) {
        return activity.getFragmentManager();
    }

    @Provides Picasso provideActivityPicasso(final Activity activity) {
        return Picasso.with(activity);
    }
}
