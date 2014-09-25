package net.guillaume.flickrsimplesearcher.rest;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "rsp")
/*package*/ class ImageSearchResponseEntity {

    @Attribute String stat;

    @Element(required = false)
    ImageSearchResponseErrorEntity err;

    @ElementList(required = false)
    List<ImageSearchResponsePhotoEntity> photos;

}
