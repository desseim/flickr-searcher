package net.guillaume.flickrsimplesearcher;

import javax.inject.Inject;

public class LocationAwareBaseFragment extends BaseFragment {

    @Inject LocationListener mLocationListener;


    @Override public void onStart() {
        super.onStart();

        mLocationListener.startListening();
    }

    @Override public void onStop() {
        mLocationListener.stopListening();

        super.onStop();
    }

}
