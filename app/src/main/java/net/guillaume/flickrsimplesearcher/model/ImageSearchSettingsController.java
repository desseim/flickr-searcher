package net.guillaume.flickrsimplesearcher.model;

import android.content.SharedPreferences;
import android.os.Parcelable;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Bus;

import net.guillaume.flickrsimplesearcher.inject.ForApplication;
import net.guillaume.flickrsimplesearcher.inject.InjectionNames;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import auto.parcel.AutoParcel;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class ImageSearchSettingsController implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String PREFERENCE_KEY_SEARCH_NEAR              = "search_near";
    private static final String PREFERENCE_KEY_ENABLED_SEARCH_PROVIDERS = "enabled_search_providers";

    private final Bus               mApplicationBus;
    private final Gson              mGson;
    private final SharedPreferences mImageSearchSettingsPreferences;

    private static final Object SEARCH_PROVIDER_SETTINGS_LOCK = new Object();

    @Inject
    public ImageSearchSettingsController(
            final @Nonnull @ForApplication Bus applicationBus,
            final @Nonnull Gson gson,
            final @Nonnull @Named(InjectionNames.PREFERENCES_SEARCH_SETTINGS) SharedPreferences imageSearchSettingsPreferences) {
        Preconditions.checkNotNull(applicationBus);
        mApplicationBus = applicationBus;

        Preconditions.checkNotNull(gson);
        mGson = gson;

        Preconditions.checkNotNull(imageSearchSettingsPreferences);
        mImageSearchSettingsPreferences = imageSearchSettingsPreferences;
        mImageSearchSettingsPreferences.registerOnSharedPreferenceChangeListener(this);  // only registers a weak reference to this
    }

    @Override public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        final Object event;
        if (key.equals(PREFERENCE_KEY_SEARCH_NEAR)) {
            final Optional<SearchLocationSetting> newSearchLocationSetting = getSearchLocationSetting(sharedPreferences);
            Preconditions.checkArgument(newSearchLocationSetting.isPresent(), "Missing location setting in shared preferences ; this isn't expected");
            event = new SearchLocationSettingChangedEvent(newSearchLocationSetting.get());
        } else if (key.equals(PREFERENCE_KEY_ENABLED_SEARCH_PROVIDERS)) {
            final Optional<SearchProviderSetting> newSearchProviderSetting = getSearchProviderSetting(sharedPreferences);
            Preconditions.checkArgument(newSearchProviderSetting.isPresent(), "Missing search provider setting in shared preferences ; this isn't expected");
            event = new SearchProviderSettingChangedEvent(newSearchProviderSetting.get());
        } else {
            throw new UnsupportedOperationException("Preference change handling not implemented: " + key);
        }
        mApplicationBus.post(event);
    }

    public Observable<ImageSearchSettings> retrieveCurrentSearchSettings() {
        return Observable.create(new Observable.OnSubscribe<ImageSearchSettings>() {
            @Override public void call(final Subscriber<? super ImageSearchSettings> subscriber) {
                final ImageSearchSettings imageSearchSettings = ImageSearchSettings.create(
                        getSearchLocationSetting(mImageSearchSettingsPreferences).orNull(),
                        getSearchProviderSetting(mImageSearchSettingsPreferences).orNull()
                );
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(imageSearchSettings);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public Action0 getUpdateSearchLocationSettingAction(final boolean searchNear) {
        return new UpdateSearchLocationSettingAction(mImageSearchSettingsPreferences, searchNear);
    }

    @SuppressWarnings("unused")  // for later use
    public Action0 getAddSearchProviderSettingAction(final SearchProvider searchProviderToAdd) {
        return new AddSearchProviderSettingAction(this, mGson, mImageSearchSettingsPreferences, searchProviderToAdd);
    }

    @SuppressWarnings("unused")  // for later use
    public Action0 getRemoveSearchProviderSettingAction(final SearchProvider searchProviderToRemove) {
        return new RemoveSearchProviderSettingAction(this, mGson, mImageSearchSettingsPreferences, searchProviderToRemove);
    }

    protected Optional<SearchLocationSetting> getSearchLocationSetting(final @Nonnull SharedPreferences sharedPreferences) {
        if (!sharedPreferences.contains(PREFERENCE_KEY_SEARCH_NEAR)) return Optional.absent();
        // else
        final SearchLocationSetting searchLocationSetting = SearchLocationSetting.create(
                sharedPreferences.getBoolean(PREFERENCE_KEY_SEARCH_NEAR, false)
        );
        return Optional.of(searchLocationSetting);
    }

    protected Optional<SearchProviderSetting> getSearchProviderSetting(final @Nonnull SharedPreferences sharedPreferences) {
        final ImmutableSet<SearchProvider> savedProviders;
        synchronized (SEARCH_PROVIDER_SETTINGS_LOCK) {
            if (!sharedPreferences.contains(PREFERENCE_KEY_ENABLED_SEARCH_PROVIDERS)) return Optional.absent();
            // else
            savedProviders = mGson.fromJson(
                    sharedPreferences.getString(PREFERENCE_KEY_ENABLED_SEARCH_PROVIDERS, ""),
                    new TypeToken<ImmutableSet<SearchProvider>>() {}.getType()
            );
        }
        final SearchProviderSetting searchProviderSetting = SearchProviderSetting.create(savedProviders);
        return Optional.of(searchProviderSetting);
    }

    @AutoParcel
    public static abstract class ImageSearchSettings implements Parcelable {

        /*package*/ ImageSearchSettings() { }

        public static ImageSearchSettings create(
                final @Nullable SearchLocationSetting locationSetting,
                final @Nullable SearchProviderSetting providerSetting
        ) {
            return new AutoParcel_ImageSearchSettingsController_ImageSearchSettings(
                    locationSetting,
                    providerSetting
            );
        }

        /*package*/ abstract @Nullable SearchLocationSetting locationSettingNullable();

        /*package*/ abstract @Nullable SearchProviderSetting providerSettingNullable();

        public Optional<SearchLocationSetting> locationSetting() { return Optional.fromNullable(locationSettingNullable()); }

        @SuppressWarnings("unused")  // for later use
        public Optional<SearchProviderSetting> providerSetting() { return Optional.fromNullable(providerSettingNullable()); }

    }

    public static class SearchLocationSettingChangedEvent {
        private SearchLocationSetting newSearchLocationSetting;

        public SearchLocationSettingChangedEvent(final SearchLocationSetting newSearchLocationSetting) {
            this.newSearchLocationSetting = newSearchLocationSetting;
        }

        public SearchLocationSetting getNewSearchLocationSetting() {
            return newSearchLocationSetting;
        }
    }

    public static class SearchProviderSettingChangedEvent {
        private SearchProviderSetting newSearchProviderSetting;

        public SearchProviderSettingChangedEvent(final SearchProviderSetting newSearchProviderSetting) {
            this.newSearchProviderSetting = newSearchProviderSetting;
        }

        @SuppressWarnings("unused")  // for later use
        public SearchProviderSetting getNewSearchProviderSetting() {
            return newSearchProviderSetting;
        }
    }

    protected static class UpdateSearchLocationSettingAction implements Action0 {
        private final SharedPreferences mSharedPreferences;
        private final boolean mSearchNear;

        public UpdateSearchLocationSettingAction(final SharedPreferences sharedPreferences, final boolean searchNear) {
            mSharedPreferences = sharedPreferences;
            mSearchNear = searchNear;
        }

        @Override public void call() {
            final SharedPreferences.Editor imageSearchSettingsPreferencesEditor = mSharedPreferences.edit();
            imageSearchSettingsPreferencesEditor.putBoolean(PREFERENCE_KEY_SEARCH_NEAR, mSearchNear);
            imageSearchSettingsPreferencesEditor.apply();
        }
    }

    protected static abstract class UpdateSearchProviderSettingAction implements Action0 {
        private final ImageSearchSettingsController mImageSearchController;
        private final Gson                          mGson;
        private final SharedPreferences             mSharedPreferences;
        private final SearchProvider                mSearchProviderToUpdate;

        public UpdateSearchProviderSettingAction(
                final ImageSearchSettingsController imageSearchController,
                final Gson gson,
                final SharedPreferences sharedPreferences,
                final SearchProvider searchProviderToUpdate) {
            mImageSearchController = imageSearchController;
            mGson = gson;
            mSharedPreferences = sharedPreferences;
            mSearchProviderToUpdate = searchProviderToUpdate;
        }

        @Override public void call() {
            synchronized (SEARCH_PROVIDER_SETTINGS_LOCK) {
                final Optional<SearchProviderSetting> searchProviderSetting = mImageSearchController.getSearchProviderSetting(mSharedPreferences);

                final ImmutableSet<SearchProvider> newSearchProviders = updateSearchProviders(
                        searchProviderSetting.isPresent() ? searchProviderSetting.get().enabledSearchProviders() : ImmutableSet.<SearchProvider>of(),
                        mSearchProviderToUpdate);

                final SharedPreferences.Editor imageSearchSettingsPreferencesEditor = mSharedPreferences.edit();
                imageSearchSettingsPreferencesEditor.putString(PREFERENCE_KEY_ENABLED_SEARCH_PROVIDERS, mGson.toJson(newSearchProviders, new TypeToken<ImmutableSet<SearchProvider>>() {}.getType()));
                imageSearchSettingsPreferencesEditor.apply();
            }
        }

        protected abstract ImmutableSet<SearchProvider> updateSearchProviders(final ImmutableSet<SearchProvider> originalSearchProviders, final SearchProvider searchProviderToUpdate);
    }

    protected static class AddSearchProviderSettingAction extends UpdateSearchProviderSettingAction {

        public AddSearchProviderSettingAction(
                final ImageSearchSettingsController imageSearchController,
                final Gson gson,
                final SharedPreferences sharedPreferences,
                final SearchProvider searchProviderToAdd) {
            super(imageSearchController, gson, sharedPreferences, searchProviderToAdd);
        }

        @Override
        protected ImmutableSet<SearchProvider> updateSearchProviders(final ImmutableSet<SearchProvider> originalSearchProviders, final SearchProvider searchProviderToUpdate) {
            return ImmutableSet.<SearchProvider>builder()
                    .addAll(originalSearchProviders)
                    .add(searchProviderToUpdate)
                    .build();
        }

    }

    protected static class RemoveSearchProviderSettingAction extends UpdateSearchProviderSettingAction {

        public RemoveSearchProviderSettingAction(
                final ImageSearchSettingsController imageSearchController,
                final Gson gson,
                final SharedPreferences sharedPreferences,
                final SearchProvider searchProviderToRemove) {
            super(imageSearchController, gson, sharedPreferences, searchProviderToRemove);
        }

        @Override
        protected ImmutableSet<SearchProvider> updateSearchProviders(final ImmutableSet<SearchProvider> originalSearchProviders, final SearchProvider searchProviderToUpdate) {
            final ImmutableSet.Builder<SearchProvider> searchProvidersBuilder = ImmutableSet.builder();
            final Set<SearchProvider> searchProvidersToAdd = Sets.filter(originalSearchProviders, new Predicate<SearchProvider>() {
                @Override public boolean apply(@Nullable final SearchProvider input) {
                    return input != null && !input.equals(searchProviderToUpdate);
                }
            });
            searchProvidersBuilder.addAll(searchProvidersToAdd);
            return searchProvidersBuilder.build();
        }

    }

}
