package net.guillaume.flickrsimplesearcher.ui;

import android.graphics.Color;

public enum MessageType {
    ERROR(Color.parseColor("#CC" + MessageType.COLOR_RGB_ORANGE_RED), Color.WHITE),
    FAILURE(Color.parseColor("#AA" + MessageType.COLOR_RGB_GREY), Color.WHITE),
    INFO(Color.parseColor("#CC" + MessageType.COLOR_RGB_INFO_BLUE), Color.WHITE);

    private static final String COLOR_RGB_ORANGE_RED = "FF4500";
    private static final String COLOR_RGB_GREY       = "808080";
    private static final String COLOR_RGB_INFO_BLUE  = "33b5e5";

    private final int mBackgroundColor;
    private final int mForegroundColor;

    MessageType(final int backgroundColor, final int foregroundColor) {
        mBackgroundColor = backgroundColor;
        mForegroundColor = foregroundColor;
    }

    public int backgroundColor() { return mBackgroundColor; }

    public int foregroundColor() { return mForegroundColor; }
}
