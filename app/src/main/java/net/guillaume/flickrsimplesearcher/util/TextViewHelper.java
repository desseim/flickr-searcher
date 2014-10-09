package net.guillaume.flickrsimplesearcher.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.widget.GridView;
import android.widget.TextView;

import java.lang.reflect.Field;

public class TextViewHelper {

    private static final int IMPL_LINES = 1;
    private static final String LOG_TAG = TextViewHelper.class.getSimpleName();

    @TargetApi(16)  // since we conditionally check for API method availability
    public static int getTextViewMaxLinesCompatible(final TextView textView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return textView.getMaxLines();
        } else {
            final Field mMaximumField;
            final Field mMaxModeField;
            try {
                mMaximumField = textView.getClass().getDeclaredField("mMaximum");
                mMaxModeField = textView.getClass().getDeclaredField("mMaxMode");
            } catch (NoSuchFieldException noSuchFieldException) {
                Log.w(LOG_TAG, "No maximum or mode field found", noSuchFieldException);
                return -1;
            }

            if (mMaximumField != null && mMaxModeField != null) {
                try {
                    mMaximumField.setAccessible(true);
                    mMaxModeField.setAccessible(true);

                    final int mMaximum = mMaximumField.getInt(textView);
                    final int mMaxMode = mMaxModeField.getInt(textView);

                    return mMaxMode == IMPL_LINES ? mMaximum : -1;
                } catch (IllegalAccessException illegalAccessException) {
                    Log.w(LOG_TAG, "Should have changed accessibility, something is wrong", illegalAccessException);
                    return -1;
                } finally {
                    mMaximumField.setAccessible(false);
                    mMaxModeField.setAccessible(false);
                }
            } else {
                Log.w(LOG_TAG, "No maximum or mode field found");
                return -1;
            }
        }
    }

}
