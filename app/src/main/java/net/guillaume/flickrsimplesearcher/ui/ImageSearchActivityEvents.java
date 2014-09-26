package net.guillaume.flickrsimplesearcher.ui;

import android.support.annotation.Nullable;

import com.google.common.base.Optional;

import net.guillaume.flickrsimplesearcher.data.ImageBasicData;

import java.util.List;

/*package*/ class ImageSearchActivityEvents {

    /*package*/ static class ImageSearchNewResultEvent {
        private final String               mQueryString;
        private final List<ImageBasicData> mResults;

        public ImageSearchNewResultEvent(final String queryString, final List<ImageBasicData> results) {
            mQueryString = queryString;
            mResults = results;
        }

        public List<ImageBasicData> getResults() {
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
        private final ImageBasicData mImageToShow;

        public ImageDetailShowEvent(final ImageBasicData imageToShow) {
            mImageToShow = imageToShow;
        }

        public ImageBasicData getImageToShow() {
            return mImageToShow;
        }
    }

}
