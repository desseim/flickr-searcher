package net.guillaume.flickrsimplesearcher.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import net.guillaume.flickrsimplesearcher.BaseFragment;
import net.guillaume.flickrsimplesearcher.R;
import net.guillaume.flickrsimplesearcher.data.ImageBasicData;

import java.util.List;

import javax.inject.Inject;

public class ImageSearchResultFragment extends BaseFragment {

    private static final String ARGUMENT_KEY_RESULT_DATA = "ImageSearchResultFragment.result_data";

    @Inject ImageSearchResultAdapter mImageSearchResultAdapter;

    public static ImageSearchResultFragment create(final List<ImageBasicData> imageBasicData) {
        final ImageSearchResultFragment imageSearchResultFragment = new ImageSearchResultFragment();
        final Bundle arguments = new Bundle();
        arguments.putParcelableArrayList(ARGUMENT_KEY_RESULT_DATA, Lists.newArrayList(imageBasicData));

        imageSearchResultFragment.setArguments(arguments);
        return imageSearchResultFragment;
    }

    @Nullable @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_search_results, container, false);
    }

    @Override public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mImageSearchResultAdapter.setData(getArguments().<ImageBasicData>getParcelableArrayList(ARGUMENT_KEY_RESULT_DATA));

        // initialize grid view
        final View rootView = getView();
        if (rootView != null) {
            final GridView gridView = (GridView) rootView.findViewById(R.id.grid_view);
            Preconditions.checkNotNull(gridView, "Didn't find grid view");
            gridView.setAdapter(mImageSearchResultAdapter);
        }
    }

    /*package*/ void updateResultData(final List<ImageBasicData> newResultData) {
        getArguments().putParcelableArrayList(ARGUMENT_KEY_RESULT_DATA, Lists.newArrayList(newResultData));

        if (mImageSearchResultAdapter != null) {
            mImageSearchResultAdapter.setData(newResultData);
        }
    }

}
