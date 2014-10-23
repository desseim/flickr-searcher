package net.guillaume.flickrsimplesearcher.control;

import android.support.annotation.Nullable;

import net.guillaume.flickrsimplesearcher.data.ImageBasicData;
import net.guillaume.flickrsimplesearcher.data.LocationData;
import net.guillaume.flickrsimplesearcher.rest.flickr.FlickrImageSearchController;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Global controller for image searching.
 * It kinda plugs UI layer with network communication layer, abstracting the details of the later for the former.
 */
public class ImageSearchController {

    @Inject FlickrImageSearchController mFlickrImageSearchController;

    public Observable<List<ImageBasicData>> searchImages(
            final String query,
            final boolean includeLocation,
            final @Nullable LocationData lastLocation) {
        return mFlickrImageSearchController
                .searchImages(query, includeLocation ? lastLocation : null);
    }

}
