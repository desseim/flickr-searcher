package net.guillaume.flickrsimplesearcher.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;

import net.guillaume.flickrsimplesearcher.R;
import net.guillaume.flickrsimplesearcher.data.ImageData;
import net.guillaume.flickrsimplesearcher.inject.ForActivity;
import net.guillaume.flickrsimplesearcher.util.GridViewHelper;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.inject.Inject;

class ImageSearchResultAdapter extends BaseAdapter {

    @Inject              LayoutInflater mLayoutInflater;
    @Inject              Picasso        mPicasso;
    @Inject @ForActivity Bus mBus;

    private ImmutableList<ImageData> mImageSearchResults = ImmutableList.of();

    /*package*/
    synchronized void setData(final @Nonnull Collection<ImageData> data) {
        mImageSearchResults = ImmutableList.copyOf(data);
        notifyDataSetChanged();
    }

    private synchronized ImmutableList<ImageData> getData() {
        return mImageSearchResults;
    }

    @Override public int getCount() {
        return getData().size();
    }

    @Override public ImageData getItem(final int position) {
        final ImmutableList<ImageData> imageData = getData();
        Preconditions.checkNotNull(imageData, "Tried to get the item at position " + position + " while no data is currently set");
        Preconditions.checkElementIndex(position, imageData.size());
        return imageData.get(position);
    }

    @Override public boolean hasStableIds() {
        return false;
    }

    @Override public long getItemId(final int position) {
        return position;  // OK since IDs are not stable
    }

    @Override public View getView(final int position, final View convertView, final ViewGroup parent) {
        // initialize view
        final View imageGridElementView;
        if (convertView == null) {
            imageGridElementView = mLayoutInflater.inflate(R.layout.image_grid_element, parent, false);
        } else {
            imageGridElementView = convertView;
        }

        // set the size of the element depending on the column width of the enclosing grid view since this one is decided dynamically by the framework
        final GridView gridView = (GridView)parent.findViewById(R.id.grid_view);
        Preconditions.checkNotNull(gridView, "Didn't find the root grid view, this isn't expected");
        final int imageBorderLength = GridViewHelper.getGridViewColumnWidthCompatible(gridView);
        imageGridElementView.setLayoutParams(new GridView.LayoutParams(imageBorderLength, imageBorderLength));

        // retrieve each sub-view
        final TextView titleView = (TextView)imageGridElementView.findViewById(R.id.title);
        Preconditions.checkNotNull(titleView, "Image title view not found");
        final ImageView imageView = (ImageView)imageGridElementView.findViewById(R.id.image);
        Preconditions.checkNotNull(titleView, "Image view not found");

        // populate the view with data
        final ImageData imageData = getItem(position);
        Preconditions.checkNotNull(imageData);
        titleView.setText(imageData.title());
        mPicasso.load(imageData.smallUri()).into(imageView);

        // show the details on click:
        imageGridElementView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(final View v) {
                mBus.post(new ImageSearchActivityEvents.ImageDetailShowEvent(imageData));
            }
        });

        return imageGridElementView;
    }

}
