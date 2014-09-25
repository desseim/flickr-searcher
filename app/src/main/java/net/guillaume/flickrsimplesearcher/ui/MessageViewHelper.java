package net.guillaume.flickrsimplesearcher.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Preconditions;

import net.guillaume.flickrsimplesearcher.R;

import javax.annotation.Nonnull;

import auto.parcel.AutoParcel;

public class MessageViewHelper {

    private static final String SAVED_STATE_PARAM_NAME_MESSAGE_VIEW_STATE = "MessageViewHelper.message_view_state";

    private MessageViewState mLastViewState;

    public void updateMessageView(final @Nonnull View messageView, final @Nonnull MessageType type, final @Nonnull String message) {
        final MessageViewState messageViewState = MessageViewState.create(type, message);
        updateMessageView(messageView, messageViewState);
    }

    private void updateMessageView(final @Nonnull View messageView, final @Nonnull MessageViewState messageViewState) {
        final TextView messageTextView = (TextView) messageView.findViewById(R.id.message);
        Preconditions.checkNotNull(messageTextView, "Received a view different from the message view");

        messageTextView.setBackgroundColor(messageViewState.messageType().backgroundColor());
        messageTextView.setTextColor(messageViewState.messageType().foregroundColor());
        messageTextView.setText(messageViewState.message());

        mLastViewState = messageViewState;
    }

    /*package*/ void saveMessageViewState(final @Nonnull Bundle bundle) {
        bundle.putParcelable(SAVED_STATE_PARAM_NAME_MESSAGE_VIEW_STATE, mLastViewState);
    }

    /*package*/ void restoreMessageViewState(final @Nonnull View messageView, final @Nonnull Bundle savedBundle) {
        Preconditions.checkArgument(savedBundle.containsKey(SAVED_STATE_PARAM_NAME_MESSAGE_VIEW_STATE), "Message state wasn't previously saved in this bundle");
        updateMessageView(messageView, (MessageViewState)savedBundle.getParcelable(SAVED_STATE_PARAM_NAME_MESSAGE_VIEW_STATE));
    }

    public static enum MessageType {
        ERROR(Color.parseColor("#CC" + MessageType.COLOR_RGB_ORANGE_RED), Color.WHITE),
        FAILURE(Color.parseColor("#AA" + MessageType.COLOR_RGB_GREY), Color.WHITE),
        INFO(Color.parseColor("#CC" + MessageType.COLOR_RGB_INFO_BLUE), Color.WHITE);

        private static final String COLOR_RGB_ORANGE_RED = "FF4500";
        private static final String COLOR_RGB_GREY       = "808080";
        private static final String COLOR_RGB_INFO_BLUE  = "33b5e5";

        private final int mBackgroundColor;
        private final int mForegroundColor;

        private MessageType(final int backgroundColor, final int foregroundColor) {
            mBackgroundColor = backgroundColor;
            mForegroundColor = foregroundColor;
        }

        public int backgroundColor() { return mBackgroundColor; }

        public int foregroundColor() { return mForegroundColor; }
    }


    @AutoParcel
    /*package*/ static abstract class MessageViewState implements Parcelable {
        /*package*/ MessageViewState() { }
        public static MessageViewState create(final @Nonnull MessageType messageType, final @Nonnull String message) {
            return new AutoParcel_MessageViewHelper_MessageViewState(messageType, message);
        }
        public abstract MessageType messageType();
        public abstract String message();
    }

}
