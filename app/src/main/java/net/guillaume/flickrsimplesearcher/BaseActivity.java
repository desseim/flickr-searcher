package net.guillaume.flickrsimplesearcher;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import net.guillaume.flickrsimplesearcher.inject.ActivityModule;

import java.util.List;

import javax.annotation.Nonnull;

import dagger.ObjectGraph;

public class BaseActivity extends Activity {

    private static final String FRAGMENT_TAG_STATE = "BaseActivity.state_fragment";

    private InjectionEnvironmentHoldingFragment mInjectionEnvironmentHoldingFragment;

    private void initInstance(final @Nonnull FragmentManager fragmentManager) {
        InjectionEnvironmentHoldingFragment injectionEnvironmentHoldingFragment = (InjectionEnvironmentHoldingFragment)fragmentManager.findFragmentByTag(FRAGMENT_TAG_STATE);
        if (injectionEnvironmentHoldingFragment == null) {
            // create and save one
            injectionEnvironmentHoldingFragment = new InjectionEnvironmentHoldingFragment();
            fragmentManager.beginTransaction().add(injectionEnvironmentHoldingFragment, FRAGMENT_TAG_STATE).commit();
        }
        injectionEnvironmentHoldingFragment.init(this);
        mInjectionEnvironmentHoldingFragment = injectionEnvironmentHoldingFragment;
    }

    @Override protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initInstance(getFragmentManager());

        // inject this activity dependencies:
        getActivityGraph().inject(this);
    }

    @Override protected void onDestroy() {
        mInjectionEnvironmentHoldingFragment = null;  // speed up garbage collection a bit

        super.onDestroy();
    }

    /*package*/ ObjectGraph getActivityGraph() {
        Preconditions.checkNotNull(mInjectionEnvironmentHoldingFragment, "Activity injection graph not ready ; are you trying to retrieve it before onCreate() has run ?");
        return mInjectionEnvironmentHoldingFragment.getActivityObjectGraph();
    }

    /**
     * Overridable by subclasses to provide additional injection modules for their types.
     */
    protected List<Object> getModules() {
        return Lists.newArrayList();
    }


    public static class InjectionEnvironmentHoldingFragment extends Fragment {
        private ActivityModule mActivityModule;
        private ObjectGraph mActivityObjectGraph;

        public InjectionEnvironmentHoldingFragment() {
            super();
            setRetainInstance(true);
        }

        private synchronized void init(final @Nonnull BaseActivity baseActivity) {
            if (mActivityObjectGraph == null) {
                mActivityModule = new ActivityModule(baseActivity);

                final Optional<ObjectGraph> applicationGraph = ((BaseApplication)baseActivity.getApplication()).getApplicationGraph();
                Preconditions.checkArgument(applicationGraph.isPresent(), "Couldn't retrieve application graph to create activity one");
                final List<Object> modules = baseActivity.getModules();
                modules.add(mActivityModule);
                mActivityObjectGraph = applicationGraph.get().plus(modules.toArray());  // add activity-scoped object graph
            } else {
                Preconditions.checkState(mActivityModule != null, "Activity object graph isn't null but module is, this should never happen");
                mActivityModule.attachActivity(baseActivity);
            }
        }

        public synchronized @Nonnull ObjectGraph getActivityObjectGraph() {
            Preconditions.checkNotNull(mActivityObjectGraph, "Trying to retrieve the activity object graph before it has been initialized ; be sure to cal init() before");
            return mActivityObjectGraph;
        }

        @Override public void onDetach() {
            if (mActivityModule != null) mActivityModule.detachActivity();

            // also clean up to speed up garbage collection a bit if we know the activity won't restart
            if (getActivity().isFinishing()) {
                mActivityModule = null;
                mActivityObjectGraph = null;
            }

            super.onDetach();
        }
    }

}
