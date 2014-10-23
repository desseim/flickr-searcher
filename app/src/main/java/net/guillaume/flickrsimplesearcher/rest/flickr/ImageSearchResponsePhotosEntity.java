package net.guillaume.flickrsimplesearcher.rest.flickr;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root
/*package*/ class ImageSearchResponsePhotosEntity {

    @Attribute int page;
    @Attribute int pages;
    @Attribute int perpage;
    @Attribute long total;

}
