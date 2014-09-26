package net.guillaume.flickrsimplesearcher.data;

import android.net.Uri;
import android.os.Parcelable;

import javax.annotation.Nonnull;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class ImageBasicData implements Parcelable {

    /*package*/ ImageBasicData() { }  // hide constructor to force use of static instance creation method

    public static ImageBasicData create(
            final @Nonnull String id,
            final @Nonnull String title,
            final @Nonnull Uri thumbnailUri,
            final @Nonnull Uri smallUri,
            final @Nonnull Uri largeUri
    ) {
        return new AutoParcel_ImageBasicData(
                id,
                title,
                thumbnailUri,
                smallUri,
                largeUri
        );
    }

    public abstract String id();
    public abstract String title();
    public abstract Uri thumbnailUri();
    public abstract Uri smallUri();
    public abstract Uri largeUri();

}
