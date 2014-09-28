package net.guillaume.flickrsimplesearcher;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.common.base.Optional;
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;

import net.guillaume.flickrsimplesearcher.data.LocationData;
import net.guillaume.flickrsimplesearcher.inject.ForApplication;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LocationListener implements android.location.LocationListener {

    private static final String LOG_TAG                    = LocationListener.class.getSimpleName();

    /**
     * The minimum distance change for us to be notified about location updates.
     */
    private static final float  MIN_DISTANCE_UPDATE_METERS = 200;

    private LocationData mLastLocation;

    private final LocationManager mLocationManager;
    private final Bus             mApplicationBus;

    @Inject
    public LocationListener(final LocationManager locationManager, final @ForApplication Bus applicationBus) {
        mLocationManager = locationManager;
        mApplicationBus = applicationBus;

        mApplicationBus.register(this);
    }

    /**
     * @return true if it actually started listening, false otherwise
     */
    public boolean startListening() {
        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, MIN_DISTANCE_UPDATE_METERS, this);
            return true;
        } catch (final IllegalArgumentException illegalArgumentException) {
            Log.i(LOG_TAG, "Location provider unavailable", illegalArgumentException);
            return false;
        }
    }

    public void stopListening() {
        mLocationManager.removeUpdates(this);
        clearLocationData();  // since from now on we will have no idea of the actual location
    }

    public void clearLocationData() {
        updateLocation(null);
    }

    @Produce public LocationChangeEvent produceLastLocationData() {
        return new LocationChangeEvent(mLastLocation);
    }

    @Override public void onLocationChanged(final Location location) {
        updateLocation(LocationData.create(location.getLatitude(), location.getLongitude()));
    }

    @Override public void onStatusChanged(final String provider, final int status, final Bundle extras) {
        // no use for us now
    }

    @Override public void onProviderEnabled(final String provider) {
        // nothing to do here: just wait for a location to be available
    }

    @Override public void onProviderDisabled(final String provider) {
        clearLocationData();
    }

    private void updateLocation(final @Nullable LocationData locationData) {
        mLastLocation = locationData;
        mApplicationBus.post(produceLastLocationData());
    }


    public static class LocationChangeEvent {
        private final LocationData mNewLocation;

        public LocationChangeEvent(final @Nullable LocationData newLocation) {
            mNewLocation = newLocation;
        }

        public Optional<LocationData> getNewLocation() {
            return Optional.fromNullable(mNewLocation);
        }
    }
}
