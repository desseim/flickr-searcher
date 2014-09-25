package net.guillaume.flickrsimplesearcher;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.google.common.base.Preconditions;

import dagger.ObjectGraph;

public class BaseFragment extends Fragment {

    @Override public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity attachedActivity = getActivity();
        Preconditions.checkArgument(attachedActivity instanceof BaseActivity, "Fragment attached to an activity which doesn't inherit from BaseActivity");  // for now only support attachment to BaseActivity types
        final ObjectGraph activityGraph = ((BaseActivity)attachedActivity).getActivityGraph();

        // inject this fragment instance with the activity scoped graph:
        activityGraph.inject(this);
    }

}
