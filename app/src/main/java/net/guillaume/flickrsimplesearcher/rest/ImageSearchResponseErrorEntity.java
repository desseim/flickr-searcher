package net.guillaume.flickrsimplesearcher.rest;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root
/*package*/ class ImageSearchResponseErrorEntity {

    @Attribute int code;
    @Attribute String msg;

}
