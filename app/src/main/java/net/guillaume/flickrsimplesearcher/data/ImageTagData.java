package net.guillaume.flickrsimplesearcher.data;

import android.os.Parcelable;

import javax.annotation.Nonnull;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class ImageTagData implements Parcelable {

    /*package*/ ImageTagData() { }  // hide constructor to force use of static instance creation method

    public static ImageTagData create(
            final @Nonnull String id,
            final @Nonnull String rawText
    ) {
        return new AutoParcel_ImageTagData(
                id,
                rawText
        );
    }

    public abstract String id();
    public abstract String rawText();

}
