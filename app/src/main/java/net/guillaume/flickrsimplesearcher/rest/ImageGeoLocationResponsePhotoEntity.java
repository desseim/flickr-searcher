package net.guillaume.flickrsimplesearcher.rest;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root()
/*package*/ class ImageGeoLocationResponsePhotoEntity {

    @Attribute String id;

    @Element
    ImageGeoLocationResponseLocationEntity location;

}
