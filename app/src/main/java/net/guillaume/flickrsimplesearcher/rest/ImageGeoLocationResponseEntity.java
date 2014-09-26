package net.guillaume.flickrsimplesearcher.rest;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "rsp")
/*package*/ class ImageGeoLocationResponseEntity implements FlickrRestResponseEntity {

    @Attribute String stat;

    @Element(required = false)
    FlickrRestResponseErrorEntity err;

    @Element(required = false)
    ImageGeoLocationResponsePhotoEntity photo;

    @Override public String getStat() {
        return stat;
    }

    @Override public FlickrRestResponseErrorEntity getError() {
        return err;
    }

}
