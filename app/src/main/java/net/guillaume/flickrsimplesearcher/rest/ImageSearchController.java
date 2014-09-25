package net.guillaume.flickrsimplesearcher.rest;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import net.guillaume.flickrsimplesearcher.data.ImageData;
import net.guillaume.flickrsimplesearcher.inject.NetworkModule;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.functions.Func1;

public class ImageSearchController {

    private static final int API_ERROR_CODE_INVALID_API_KEY  = 100;
    private static final int API_ERROR_CODE_METHOD_NOT_FOUND = 112;

    @Inject FlickrService mFlickrService;
    @Inject @Named(NetworkModule.NAME_FLICKR_API_KEY) String mFlickrApiKey;

    public Observable<List<ImageData>> searchImages(final String queryText) {
        return mFlickrService.searchImages(mFlickrApiKey, queryText)
                .map(new Func1<ImageSearchResponseEntity, List<ImageData>>() {
                    @Override public List<ImageData> call(final ImageSearchResponseEntity imageSearchResponseEntity) {
                        Preconditions.checkNotNull(imageSearchResponseEntity.stat, "Response status absent or not parsed");
                        if (imageSearchResponseEntity.stat.equals("fail")) {
                            final ImageSearchResponseErrorEntity errorEntity = imageSearchResponseEntity.err;
                            switch (errorEntity.code) {
                                case API_ERROR_CODE_INVALID_API_KEY:
                                    throw new FlickrRestErrorInvalidApiKey();
                                case API_ERROR_CODE_METHOD_NOT_FOUND:
                                    throw new FlickrRestErrorMethodNotFound();
                                default:
                                    throw new FlickrRestErrorGenericError(errorEntity.code, errorEntity.msg);
                            }
                        } else if (imageSearchResponseEntity.stat.equals("ok")) {
                            Preconditions.checkNotNull(imageSearchResponseEntity.photos, "No photos in valid response");
                            return Lists.transform(imageSearchResponseEntity.photos, new Function<ImageSearchResponsePhotoEntity, ImageData>() {
                                @Nullable @Override public ImageData apply(@Nullable final ImageSearchResponsePhotoEntity input) {
                                    return input != null ? input.toImageData() : null;
                                }
                            });
                        } else {
                            throw new RuntimeException("Unknown response status: " + imageSearchResponseEntity.stat);
                        }
                    }
                });
    }

}
