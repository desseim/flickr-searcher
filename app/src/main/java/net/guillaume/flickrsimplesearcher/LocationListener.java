package net.guillaume.flickrsimplesearcher;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.common.base.Optional;
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;

import net.guillaume.flickrsimplesearcher.data.LocationData;
import net.guillaume.flickrsimplesearcher.inject.ForApplication;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LocationListener implements android.location.LocationListener {

    private LocationData mLastLocation;

    private final LocationManager mLocationManager;
    private final Bus mApplicationBus;

    @Inject
    public LocationListener(final LocationManager locationManager, final @ForApplication Bus applicationBus) {
        mLocationManager = locationManager;
        mApplicationBus = applicationBus;

        mApplicationBus.register(this);
    }

    public void startListening() {
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }

    public void stopListening() {
        mLocationManager.removeUpdates(this);
    }

    public void clearLocationData() {
        mLastLocation = null;
    }

    @Produce public LocationChangeEvent produceLastLocationData() {
        return new LocationChangeEvent(mLastLocation);
    }

    @Override public void onLocationChanged(final Location location) {
        mLastLocation = LocationData.create(location.getLatitude(), location.getLongitude());
        mApplicationBus.post(produceLastLocationData());
    }

    @Override public void onStatusChanged(final String provider, final int status, final Bundle extras) {
        // no use for us now
    }

    @Override public void onProviderEnabled(final String provider) {
        // no use for us now
    }

    @Override public void onProviderDisabled(final String provider) {
        // no use for us now
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
