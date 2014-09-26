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
            final @Nonnull List<ImageTagData> tags
    ) {
        return new AutoParcel_ImageInfoData(
                id,
                title,
                Optional.fromNullable(description),
                tags
        );
    }

    public abstract String id();
    public abstract String title();
    public abstract Optional<String> description();
    /*package*/ abstract List<ImageTagData> tagsMutableList();

    public ImmutableList<ImageTagData> tags() {
        return ImmutableList.copyOf(tagsMutableList());
    }
}
