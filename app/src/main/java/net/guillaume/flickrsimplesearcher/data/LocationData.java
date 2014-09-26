package net.guillaume.flickrsimplesearcher.data;

import android.os.Parcelable;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class LocationData implements Parcelable {

    /*package*/ LocationData() { }

    public static LocationData create(final double latitude, final double longitude) {
        return new AutoParcel_LocationData(latitude, longitude);
    }

    public abstract double latitude();
    public abstract double longitude();

}
