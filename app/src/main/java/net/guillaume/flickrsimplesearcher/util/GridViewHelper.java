package net.guillaume.flickrsimplesearcher.util;

import android.os.Build;
import android.widget.GridView;

import java.lang.reflect.Field;

public class GridViewHelper {

    public static int getGridViewColumnWidthCompatible(final GridView gridView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return gridView.getColumnWidth();
        } else {
            Field columnWidthField = null;
            try {
                columnWidthField = GridView.class.getDeclaredField("mColumnWidth");
                columnWidthField.setAccessible(true);

                return (Integer)columnWidthField.get(gridView);
            } catch (final NoSuchFieldException noSuchFieldException) {
                throw new RuntimeException("No column width field, this is unexpected", noSuchFieldException);
            } catch (final IllegalAccessException illegalAccessException) {
                throw new RuntimeException("Field wasn't accessible while it should have been made so, unexpected", illegalAccessException);
            } finally {
                if (columnWidthField != null) columnWidthField.setAccessible(false);
            }
        }
    }

}
