package net.guillaume.flickrsimplesearcher.rest;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import net.guillaume.flickrsimplesearcher.data.ImageInfoData;
import net.guillaume.flickrsimplesearcher.data.ImageTagData;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

import javax.annotation.Nullable;

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

    /*package*/ ImageInfoData toImageInfoData() {
        return ImageInfoData.create(
                id,
                title,
                Strings.emptyToNull(description),
                Lists.transform(tags, new Function<ImageInfoResponseTagEntity, ImageTagData>() {
                    @Nullable @Override public ImageTagData apply(@Nullable final ImageInfoResponseTagEntity input) {
                        return input != null ? input.toImageTagData() : null;
                    }
                })
        );
    }

}
