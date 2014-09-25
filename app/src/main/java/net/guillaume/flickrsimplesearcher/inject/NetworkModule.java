package net.guillaume.flickrsimplesearcher.inject;

import android.content.res.Resources;

import net.guillaume.flickrsimplesearcher.R;
import net.guillaume.flickrsimplesearcher.rest.FlickrService;
import net.guillaume.flickrsimplesearcher.rest.ImageSearchController;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(injects = {
        ImageSearchController.class
},
        addsTo = ApplicationModule.class)
public class NetworkModule {

    public static final String NAME_FLICKR_API_KEY = "api_key";

    @Provides FlickrService provideFlickrService(final RestAdapter restAdapter) {
        return restAdapter.create(FlickrService.class);
    }

    @Provides @Named(NAME_FLICKR_API_KEY) String provideFlickrApiKey(@ForApplication Resources applicationResources) {
        return applicationResources.getString(R.string.flickr_api_key);
    }

}
