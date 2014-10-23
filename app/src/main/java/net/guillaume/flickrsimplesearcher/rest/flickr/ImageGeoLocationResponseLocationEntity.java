package net.guillaume.flickrsimplesearcher.rest.flickr;

import net.guillaume.flickrsimplesearcher.data.LocationData;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(strict = false)
/*package*/ class ImageGeoLocationResponseLocationEntity {

    @Attribute float latitude;
    @Attribute float longitude;
    @Attribute int accuracy;

    public LocationData toLocationData() {
        return LocationData.create(latitude, longitude);
    }

}
