package net.guillaume.flickrsimplesearcher.data;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.Nonnull;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class ImageInfoData implements Parcelable {

    /*package*/ ImageInfoData() { }  // hide constructor to force use of static instance creation method

    public static ImageInfoData create(
            final @Nonnull String id,
            final @Nonnull String title,
            final @Nullable String description,
            final @Nullable LocationData location,
            final @Nonnull List<ImageTagData> tags
    ) {
        return new AutoParcel_ImageInfoData(
                id,
                title,
                Optional.fromNullable(description),
                location,
                tags
        );
    }

    public abstract String id();
    public abstract String title();
    public abstract Optional<String> description();
    public abstract @Nullable LocationData locationNullable();
    /*package*/ abstract List<ImageTagData> tagsMutableList();

    public Optional<LocationData> location() { return Optional.fromNullable(locationNullable()); }
    public ImmutableList<ImageTagData> tags() {
        return ImmutableList.copyOf(tagsMutableList());
    }

}
