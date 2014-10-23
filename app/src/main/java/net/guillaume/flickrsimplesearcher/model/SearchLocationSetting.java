package net.guillaume.flickrsimplesearcher.model;

import android.os.Parcelable;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class SearchLocationSetting implements Parcelable {

    /*package*/ SearchLocationSetting() { }

    public static SearchLocationSetting create(final boolean searchNearCurrentLocation) {
        return new AutoParcel_SearchLocationSetting(searchNearCurrentLocation);
    }

    public abstract boolean searchNearCurrentLocation();

}
