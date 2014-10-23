package net.guillaume.flickrsimplesearcher.rest.flickr;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(strict = false)
/*package*/ class ImageInfoResponsePhotoEntity {

    @Attribute String id;
    @Attribute String secret;
    @Attribute String server;

    @Element     ImageInfoResponseOwnerEntity     owner;
    @Element     String                           title;
    @Element(required = false) String             description;
    @Element     long                             comments;
    @ElementList List<ImageInfoResponseTagEntity> tags;

}
