package net.guillaume.flickrsimplesearcher.data;

import android.net.Uri;
import android.os.Parcelable;

import javax.annotation.Nonnull;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class ImageData implements Parcelable {

    /*package*/ ImageData() { }  // hide constructor to force use of static instance creation method

    public static ImageData create(
            final @Nonnull String title,
            final @Nonnull Uri thumbnailUri,
            final @Nonnull Uri smallUri,
            final @Nonnull Uri largeUri
    ) {
        return new AutoParcel_ImageData(
                title,
                thumbnailUri,
                smallUri,
                largeUri
        );
    }

    public abstract String title();
    public abstract Uri thumbnailUri();
    public abstract Uri smallUri();
    public abstract Uri largeUri();

}
