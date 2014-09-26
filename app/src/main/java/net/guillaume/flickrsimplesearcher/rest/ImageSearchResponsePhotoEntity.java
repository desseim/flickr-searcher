package net.guillaume.flickrsimplesearcher.rest;

import android.net.Uri;

import net.guillaume.flickrsimplesearcher.data.ImageBasicData;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root
/*package*/ class ImageSearchResponsePhotoEntity {

    private static final String URI_AUTHORITY_FARM_PREFIX = "farm";
    private static final String URI_AUTHORITY_SUFFIX = ".staticflickr.com";
    private static final String URI_PATH_IMAGE_SUFFIX_THUMBNAIL = "_t.jpg";
    private static final String URI_PATH_IMAGE_SUFFIX_SMALL = "_n.jpg";
    private static final String URI_PATH_IMAGE_SUFFIX_LARGE = "_b.jpg";

    @Attribute String  id;
    @Attribute String  owner;
    @Attribute String  secret;
    @Attribute String  server;
    @Attribute int     farm;
    @Attribute String  title;
    @Attribute boolean ispublic;
    @Attribute boolean isfriend;
    @Attribute boolean isfamily;

    /*package*/ ImageBasicData toImageData() {
        return ImageBasicData.create(
                id,
                title,
                buildThumbnailUri(),
                buildSmallUri(),
                buildLargeUri());
    }

    private Uri buildThumbnailUri() {
        return buildImageUriFromSuffix(URI_PATH_IMAGE_SUFFIX_THUMBNAIL);
    }

    private Uri buildSmallUri() {
        return buildImageUriFromSuffix(URI_PATH_IMAGE_SUFFIX_SMALL);
    }

    private Uri buildLargeUri() {
        return buildImageUriFromSuffix(URI_PATH_IMAGE_SUFFIX_LARGE);
    }

    private Uri buildBaseUri() {
        return new Uri.Builder()
                .scheme("https")
                .authority(getUriAuthorityString())
                .appendPath(server)
                .build();
    }

    private Uri buildImageUriFromSuffix(final String suffix) {
        return buildBaseUri()
                .buildUpon()
                .appendPath("" + id + "_" + secret + suffix)
                .build();
    }

    private String getUriAuthorityString() {
        return URI_AUTHORITY_FARM_PREFIX + farm + URI_AUTHORITY_SUFFIX;
    }

}
