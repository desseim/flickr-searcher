package net.guillaume.flickrsimplesearcher.inject;

import android.app.Application;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.location.LocationManager;

import com.squareup.otto.Bus;

import net.guillaume.flickrsimplesearcher.BuildConfig;
import net.guillaume.flickrsimplesearcher.LocationListener;
import net.guillaume.flickrsimplesearcher.ui.ImageSearchActivity;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.converter.Converter;
import retrofit.converter.SimpleXMLConverter;

@Module(injects = {
        LocationListener.class
},
        library = true)
public class ApplicationModule {

    private static final String FLICKR_API_REST_ENDPOINT   = "https://api.flickr.com/services/rest/";
    private static final Class  IMAGE_SEARCHABLE_COMPONENT = ImageSearchActivity.class;

    private final Application mApplication;

    public ApplicationModule(final Application application) {
        mApplication = application;
    }

    @Provides @Singleton Application provideApplication() {
        return mApplication;
    }

    @Provides @Singleton @ForApplication Context provideApplicationContext() {
        return mApplication;
    }

    @Provides @Singleton @ForApplication Bus provideApplicationBus() { return new Bus(); }

    @Provides LocationManager provideLocationManager(final Application application) {
        return (LocationManager) application.getSystemService(Context.LOCATION_SERVICE);
    }

    @Provides SearchManager provideSearchManager(final Application application) {
        return (SearchManager) application.getSystemService(Context.SEARCH_SERVICE);
    }

    @Provides @Singleton RestAdapter provideRestAdapter() {
        return new RestAdapter.Builder()
                .setLogLevel(getAppropriateLogLevel())
                .setEndpoint(FLICKR_API_REST_ENDPOINT)
                .setConverter(getFlickrApiConverter())
                .build();
    }

    @Provides @ForApplication Resources provideApplicationResources() {
        return mApplication.getResources();
    }

    @Provides @Named(InjectionNames.SEARCH_INFO_IMAGES) SearchableInfo provideSearchImagesSearchInfo(final @ForApplication Context applicationContext, final SearchManager searchManager) {
        final ComponentName searchableComponentName = new ComponentName(applicationContext, IMAGE_SEARCHABLE_COMPONENT);
        return searchManager.getSearchableInfo(searchableComponentName);
    }

    private RestAdapter.LogLevel getAppropriateLogLevel() {
        return BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE;
    }

    private Converter getFlickrApiConverter() {
        return new SimpleXMLConverter();
    }

}
