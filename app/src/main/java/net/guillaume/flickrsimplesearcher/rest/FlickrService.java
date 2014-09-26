package net.guillaume.flickrsimplesearcher.rest;

import android.support.annotation.Nullable;

import javax.annotation.Nonnull;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface FlickrService {

    @GET("/?method=flickr.photos.search")
    Observable<ImageSearchResponseEntity> searchImages(@Query("api_key") @Nonnull String apiKey, @Query("text") @Nullable String textToSearch);

    @GET("/?method=flickr.photos.getInfo")
    Observable<ImageInfoResponseEntity> getInfo(@Query("api_key") @Nonnull String apiKey, @Query("photo_id") @Nullable String id);

    @GET("/?method=flickr.photos.geo.getLocation")
    Observable<ImageGeoLocationResponseEntity> getGeoLocation(@Query("api_key") @Nonnull String apiKey, @Query("photo_id") @Nullable String id);

}
