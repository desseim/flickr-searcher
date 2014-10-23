package net.guillaume.flickrsimplesearcher.model;

import android.os.Parcelable;

import com.google.common.collect.ImmutableSet;

import java.util.List;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class SearchProviderSetting implements Parcelable {

    /*package*/ SearchProviderSetting() { }

    public static SearchProviderSetting create(final ImmutableSet<SearchProvider> enabledSearchProviders) {
        return new AutoParcel_SearchProviderSetting(enabledSearchProviders.asList());
    }

    /*package*/ abstract List<SearchProvider> enabledSearchProvidersMutableList();

    public ImmutableSet<SearchProvider> enabledSearchProviders() {
        return ImmutableSet.copyOf(enabledSearchProvidersMutableList());
    }

}
