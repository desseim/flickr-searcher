package net.guillaume.flickrsimplesearcher.rest;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import net.guillaume.flickrsimplesearcher.data.ImageBasicData;
import net.guillaume.flickrsimplesearcher.data.ImageInfoData;
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

    public Observable<List<ImageBasicData>> searchImages(final String queryText) {
        return mFlickrService.searchImages(mFlickrApiKey, queryText)
                .map(new Func1<ImageSearchResponseEntity, List<ImageBasicData>>() {
                    @Override public List<ImageBasicData> call(final ImageSearchResponseEntity imageSearchResponseEntity) {
                        verifyResponse(imageSearchResponseEntity);

                        Preconditions.checkNotNull(imageSearchResponseEntity.photos, "No photos in valid response");
                        return Lists.transform(imageSearchResponseEntity.photos, new Function<ImageSearchResponsePhotoEntity, ImageBasicData>() {
                            @Nullable @Override public ImageBasicData apply(@Nullable final ImageSearchResponsePhotoEntity input) {
                                return input != null ? input.toImageData() : null;
                            }
                        });
                    }
                });
    }

    public Observable<ImageInfoData> getImageInfo(final String imageId) {
        return mFlickrService.getInfo(mFlickrApiKey, imageId)
                .map(new Func1<ImageInfoResponseEntity, ImageInfoData>() {
                    @Override public ImageInfoData call(final ImageInfoResponseEntity imageInfoResponseEntity) {
                        verifyResponse(imageInfoResponseEntity);

                        return imageInfoResponseEntity.photo.toImageInfoData();
                    }
                });
    }

    private void verifyResponse(final FlickrRestResponseEntity responseEntity) {
        Preconditions.checkNotNull(responseEntity.getStat(), "Response status absent or not parsed");
        if (responseEntity.getStat().equals("fail")) {
            final FlickrRestResponseErrorEntity errorEntity = responseEntity.getError();
            switch (errorEntity.code) {
                case API_ERROR_CODE_INVALID_API_KEY:
                    throw new FlickrRestErrorInvalidApiKey();
                case API_ERROR_CODE_METHOD_NOT_FOUND:
                    throw new FlickrRestErrorMethodNotFound();
                default:
                    throw new FlickrRestErrorGenericError(errorEntity.code, errorEntity.msg);
            }
        } else if (!responseEntity.getStat().equals("ok")) {
            throw new RuntimeException("Unknown response status: " + responseEntity.getStat());
        }
    }
}
