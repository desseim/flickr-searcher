package net.guillaume.flickrsimplesearcher.data;

import android.os.Parcelable;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class LocationData implements Parcelable {

    /*package*/ LocationData() { }

    public static LocationData create(final float latitude, final float longitude) {
        return new AutoParcel_LocationData(latitude, longitude);
    }

    public abstract float latitude();
    public abstract float longitude();

}
