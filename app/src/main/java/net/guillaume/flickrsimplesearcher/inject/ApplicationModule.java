package net.guillaume.flickrsimplesearcher.inject;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.location.LocationManager;

import com.squareup.otto.Bus;

import net.guillaume.flickrsimplesearcher.BuildConfig;
import net.guillaume.flickrsimplesearcher.LocationListener;

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

    private static final String FLICKR_API_REST_ENDPOINT = "https://api.flickr.com/services/rest/";

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
        return (LocationManager)application.getSystemService(Context.LOCATION_SERVICE);
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

    private RestAdapter.LogLevel getAppropriateLogLevel() {
        return BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE;
    }

    private Converter getFlickrApiConverter() {
        return new SimpleXMLConverter();
    }

}
