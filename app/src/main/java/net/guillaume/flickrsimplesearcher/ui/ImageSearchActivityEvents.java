package net.guillaume.flickrsimplesearcher.ui;

import android.support.annotation.Nullable;

import com.google.common.base.Optional;

import net.guillaume.flickrsimplesearcher.data.ImageData;

import java.util.List;

/*package*/ class ImageSearchActivityEvents {

    /*package*/ static class ImageSearchNewResultEvent {
        private final String          mQueryString;
        private final List<ImageData> mResults;

        public ImageSearchNewResultEvent(final String queryString, final List<ImageData> results) {
            mQueryString = queryString;
            mResults = results;
        }

        public List<ImageData> getResults() {
            return mResults;
        }

        public String getQueryString() {
            return mQueryString;
        }
    }

    /*package*/ static class ImageSearchFailedEvent {
        private final Throwable mException;

        public ImageSearchFailedEvent(final @Nullable Throwable exception) {
            mException = exception;
        }

        public Optional<Throwable> getException() {
            return Optional.fromNullable(mException);
        }
    }

    /*package*/ static class ShowMessageEvent {
        private final MessageType mMessageType;
        private final String      mMessage;

        public ShowMessageEvent(final MessageType messageType, final String message) {
            mMessageType = messageType;
            mMessage = message;
        }

        public MessageType getMessageType() {
            return mMessageType;
        }

        public String getMessage() {
            return mMessage;
        }
    }

    /*package*/ static class ImageDetailShowEvent {
        private final ImageData mImageToShow;

        public ImageDetailShowEvent(final ImageData imageToShow) {
            mImageToShow = imageToShow;
        }

        public ImageData getImageToShow() {
            return mImageToShow;
        }
    }

}
