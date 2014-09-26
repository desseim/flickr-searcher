package net.guillaume.flickrsimplesearcher.rest;

import net.guillaume.flickrsimplesearcher.data.ImageTagData;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root(strict = false)
/*package*/ class ImageInfoResponseTagEntity {

    @Attribute String id;
    @Attribute String author;
    @Attribute String raw;

    @Text String tag;

    public ImageTagData toImageTagData() {
        return ImageTagData.create(
                id,
                raw
        );
    }

}
