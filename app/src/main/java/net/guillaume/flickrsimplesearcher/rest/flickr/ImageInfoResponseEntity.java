package net.guillaume.flickrsimplesearcher.rest.flickr;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "rsp")
/*package*/ class ImageInfoResponseEntity implements FlickrRestResponseEntity {

    @Attribute String stat;

    @Element(required = false)
    FlickrRestResponseErrorEntity err;

    @Element(required = false)
    ImageInfoResponsePhotoEntity photo;

    @Override public String getStat() {
        return stat;
    }

    @Override public FlickrRestResponseErrorEntity getError() {
        return err;
    }

}
