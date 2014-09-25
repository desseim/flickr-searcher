package net.guillaume.flickrsimplesearcher.ui;

import android.support.annotation.Nullable;

import com.google.common.base.Optional;
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;

import net.guillaume.flickrsimplesearcher.data.ImageData;
import net.guillaume.flickrsimplesearcher.inject.ForActivity;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton  // singleton within the activity scope
public class ImageSearchActivityUiActionHandler {

    private final Bus mActivityBus;

    private UiActionEvent mLastActionEvent;

    @Inject
    public ImageSearchActivityUiActionHandler(final @ForActivity Bus activityBus) {
        mActivityBus = activityBus;
        mActivityBus.register(this);  // as a producer
    }

    /*package*/ synchronized void handleUiActionEvent(final UiActionEvent event) {
        mLastActionEvent = event;
        mActivityBus.post(produceLastUiActionEvent());
    }

    @Produce public synchronized UiActionEvent produceLastUiActionEvent() {
        return mLastActionEvent;
    }

    //--- event classes

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

    /*package*/ static abstract class UiActionEvent {
        public abstract void accept(final @Nonnull UiActionEventVisitor visitor);
    }

    /*package*/ static interface UiActionEventVisitor {
        void visit(ImageSearchFailedEvent imageSearchFailedEvent);
        void visit(ShowMessageEvent showMessageEvent);
        void visit(ImageSearchResultShowEvent imageSearchResultShowEvent);
        void visit(ImageDetailShowEvent imageDetailShowEvent);
    }

    /*package*/ static class ImageSearchFailedEvent extends UiActionEvent {
        private final Throwable mException;

        public ImageSearchFailedEvent(final @Nullable Throwable exception) {
            mException = exception;
        }

        @Override public void accept(final @Nonnull UiActionEventVisitor visitor) {
            visitor.visit(this);
        }

        public Optional<Throwable> getException() {
            return Optional.fromNullable(mException);
        }
    }

    /*package*/ static class ShowMessageEvent extends UiActionEvent {
        private final MessageViewHelper.MessageType mMessageType;
        private final String mMessage;

        public ShowMessageEvent(final MessageViewHelper.MessageType messageType, final String message) {
            mMessageType = messageType;
            mMessage = message;
        }

        @Override public void accept(final @Nonnull UiActionEventVisitor visitor) {
            visitor.visit(this);
        }

        public MessageViewHelper.MessageType getMessageType() {
            return mMessageType;
        }

        public String getMessage() {
            return mMessage;
        }
    }

    /*package*/ static class ImageSearchResultShowEvent extends UiActionEvent {
        @Override public void accept(final @Nonnull UiActionEventVisitor visitor) {
            visitor.visit(this);
        }
    }

    /*package*/ static class ImageDetailShowEvent extends UiActionEvent {
        private final ImageData mImageToShow;

        public ImageDetailShowEvent(final ImageData imageToShow) {
            mImageToShow = imageToShow;
        }

        @Override public void accept(final @Nonnull UiActionEventVisitor visitor) {
            visitor.visit(this);
        }

        public ImageData getImageToShow() {
            return mImageToShow;
        }
    }
}
