package net.guillaume.flickrsimplesearcher.rest.flickr;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "rsp")
/*package*/ class ImageSearchResponseEntity implements FlickrRestResponseEntity {

    @Attribute String stat;

    @Element(required = false)
    FlickrRestResponseErrorEntity err;

    @ElementList(required = false)
    List<ImageSearchResponsePhotoEntity> photos;

    @Override public String getStat() {
        return stat;
    }

    @Override public FlickrRestResponseErrorEntity getError() {
        return err;
    }

}
