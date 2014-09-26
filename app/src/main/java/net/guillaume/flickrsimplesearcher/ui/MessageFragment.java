package net.guillaume.flickrsimplesearcher.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.guillaume.flickrsimplesearcher.BaseFragment;
import net.guillaume.flickrsimplesearcher.R;

public class MessageFragment extends BaseFragment {

    private static final String ARGUMENT_KEY_MESSAGE_TYPE = "MessageFragment.message_type";
    private static final String ARGUMENT_KEY_MESSAGE      = "MessageFragment.message";

    public static MessageFragment create(final MessageType messageType, final String message) {
        final MessageFragment messageFragment = new MessageFragment();

        final Bundle arguments = new Bundle();
        arguments.putSerializable(ARGUMENT_KEY_MESSAGE_TYPE, messageType);
        arguments.putString(ARGUMENT_KEY_MESSAGE, message);
        messageFragment.setArguments(arguments);

        return messageFragment;
    }

    @Nullable @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.message, container, false);
    }

    @Override public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final View rootView = getView();
        if (rootView != null) {
            final MessageType messageType = (MessageType) getArguments().getSerializable(ARGUMENT_KEY_MESSAGE_TYPE);
            final String message = getArguments().getString(ARGUMENT_KEY_MESSAGE);

            final TextView messageTextView = (TextView) rootView.findViewById(R.id.message);
            messageTextView.setBackgroundColor(messageType.backgroundColor());
            messageTextView.setTextColor(messageType.foregroundColor());
            messageTextView.setText(message);
        }
    }
}
