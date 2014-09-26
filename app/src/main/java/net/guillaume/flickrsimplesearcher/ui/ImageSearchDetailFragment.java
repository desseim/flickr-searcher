package net.guillaume.flickrsimplesearcher.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.guillaume.flickrsimplesearcher.BaseFragment;
import net.guillaume.flickrsimplesearcher.R;
import net.guillaume.flickrsimplesearcher.data.ImageData;

import javax.inject.Inject;

public class ImageSearchDetailFragment extends BaseFragment {

    private static final String ARGUMENT_KEY_IMAGE_DATA = "ImageSearchDetailFragment.image_data";

    @Inject Picasso mPicasso;

    public static ImageSearchDetailFragment create(final ImageData imageData) {
        final ImageSearchDetailFragment imageSearchDetailFragment = new ImageSearchDetailFragment();

        final Bundle arguments = new Bundle();
        arguments.putParcelable(ARGUMENT_KEY_IMAGE_DATA, imageData);

        imageSearchDetailFragment.setArguments(arguments);
        return imageSearchDetailFragment;
    }

    @Nullable @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.image_detail, container, false);
    }

    @Override public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final View rootView = getView();
        if (rootView != null) {
            final ImageView imageView = (ImageView) rootView.findViewById(R.id.image);
            final TextView titleView = (TextView) rootView.findViewById(R.id.title);
            final ImageData imageData = getArguments().getParcelable(ARGUMENT_KEY_IMAGE_DATA);

            mPicasso.load(imageData.largeUri()).into(imageView);
            titleView.setText(imageData.title());
        }
    }
}
