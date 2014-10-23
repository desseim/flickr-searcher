package net.guillaume.flickrsimplesearcher.rest.flickr;

public class FlickrRestErrorGenericError extends RuntimeException {

    private final int errorCode;
    private final String errorMessage;

    public FlickrRestErrorGenericError(final int errorCode, final String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int errorCode() {
        return errorCode;
    }

    public String errorMessage() {
        return errorMessage;
    }

}
