package net.guillaume.flickrsimplesearcher.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import net.guillaume.flickrsimplesearcher.BaseFragment;
import net.guillaume.flickrsimplesearcher.R;
import net.guillaume.flickrsimplesearcher.data.ImageBasicData;
import net.guillaume.flickrsimplesearcher.data.ImageInfoData;
import net.guillaume.flickrsimplesearcher.data.ImageTagData;
import net.guillaume.flickrsimplesearcher.inject.ForActivity;
import net.guillaume.flickrsimplesearcher.rest.ImageSearchController;
import net.guillaume.flickrsimplesearcher.util.TextViewHelper;

import java.util.Collection;

import javax.inject.Inject;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ImageSearchDetailFragment extends BaseFragment {

    private static final String LOG_TAG                 = ImageSearchDetailFragment.class.getSimpleName();

    private static final String ARGUMENT_KEY_IMAGE_DATA = "ImageSearchDetailFragment.image_data";
    private static final String ARGUMENT_KEY_IMAGE_INFO = "ImageSearchDetailFragment.image_info";

    @Inject @ForActivity Bus                   mBus;
    @Inject              Picasso               mPicasso;
    @Inject              ImageSearchController mImageSearchController;

    public static ImageSearchDetailFragment create(final ImageBasicData imageBasicData) {
        final ImageSearchDetailFragment imageSearchDetailFragment = new ImageSearchDetailFragment();

        final Bundle arguments = new Bundle();
        arguments.putParcelable(ARGUMENT_KEY_IMAGE_DATA, imageBasicData);

        imageSearchDetailFragment.setArguments(arguments);
        return imageSearchDetailFragment;
    }

    @Nullable @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.image_detail, container, false);
    }

    @Override public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBus.register(this);

        final View rootView = getView();
        if (rootView != null) {
            final ImageView imageView = (ImageView) rootView.findViewById(R.id.image);
            final View textFrameView = rootView.findViewById(R.id.text_frame);
            final View textFrameInnerView = rootView.findViewById(R.id.text_frame_inner);
            final TextView titleView = (TextView) rootView.findViewById(R.id.title);
            final ImageBasicData imageBasicData = getArguments().getParcelable(ARGUMENT_KEY_IMAGE_DATA);

            mPicasso.load(imageBasicData.largeUri()).into(imageView);
            titleView.setText(imageBasicData.title());

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(final View v) {
                    // on click on image, toggle description visibility:
                    textFrameView.setVisibility(textFrameView.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
                }
            });
            textFrameInnerView.setOnClickListener(new View.OnClickListener() {
                private int mPreviousMaxLines = Integer.MAX_VALUE;
                private TextUtils.TruncateAt mPreviousEllipsize = null;

                @Override public void onClick(final View v) {
                    // toggles between showing the full text and just a few lines
                    // (considering all 3 views have the same number of max lines when shrunk)

                    final TextView titleView = (TextView) v.findViewById(R.id.title);
                    final TextView descriptionView = (TextView) v.findViewById(R.id.description);
                    final TextView tagsView = (TextView) v.findViewById(R.id.tags);

                    final int maxLines = TextViewHelper.getTextViewMaxLinesCompatible(titleView);
                    final TextUtils.TruncateAt ellipsize = titleView.getEllipsize();

                    titleView.setMaxLines(mPreviousMaxLines);
                    titleView.setEllipsize(mPreviousEllipsize);
                    descriptionView.setMaxLines(mPreviousMaxLines);
                    descriptionView.setEllipsize(mPreviousEllipsize);
                    tagsView.setMaxLines(mPreviousMaxLines);
                    tagsView.setEllipsize(mPreviousEllipsize);

                    mPreviousMaxLines = maxLines;
                    mPreviousEllipsize = ellipsize;
                }
            });

            final ImageInfoData imageInfoData = (ImageInfoData) getArguments().get(ARGUMENT_KEY_IMAGE_INFO);
            if (imageInfoData != null) {
                updateViewWithImageInfo(imageInfoData);
            } else {
                // we don't have the info yet, retrieve them:
                mImageSearchController
                        .getImageInfo(imageBasicData.id())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ImageInfoData>() {
                            @Override public void onCompleted() {
                                // nothing to do
                            }

                            @Override public void onError(final Throwable exception) {
                                // don't fail, just log
                                Log.w(LOG_TAG, "Failed to retrieve image info for image " + imageBasicData.id(), exception);
                            }

                            @Override public void onNext(final ImageInfoData imageInfoData) {
                                mBus.post(new ImageInfoReceivedEvent(imageInfoData));
                            }
                        });
            }
        }
    }

    private void updateViewWithImageInfo(final ImageInfoData imageInfoData) {
        final View rootView = getView();
        if (rootView != null) {
            final TextView descriptionView = (TextView) rootView.findViewById(R.id.description);
            final TextView tagsView = (TextView) rootView.findViewById(R.id.tags);

            descriptionView.setText(imageInfoData.description().isPresent() ? imageInfoData.description().get() : "");
            tagsView.setText(formatTagsText(imageInfoData.tags()));
        }
    }

    @Subscribe public void onImageInfoReceived(final ImageInfoReceivedEvent imageInfoReceivedEvent) {
        updateViewWithImageInfo(imageInfoReceivedEvent.getImageInfoData());
    }

    @Override public void onDetach() {
        mBus.unregister(this);

        super.onDetach();
    }

    private String formatTagsText(final Collection<ImageTagData> tagData) {
        final Collection<String> tagRawPrefixed = Collections2.transform(
                tagData,
                new Function<ImageTagData, String>() {
                    @javax.annotation.Nullable @Override
                    public String apply(@javax.annotation.Nullable final ImageTagData input) {
                        return input != null ?  input.rawText() : null;
                    }
                }
        );

        return Joiner.on(' ').skipNulls().join(tagRawPrefixed);
    }


    /*package*/ static class ImageInfoReceivedEvent {
        private final ImageInfoData mImageInfoData;

        public ImageInfoReceivedEvent(final ImageInfoData imageInfoData) {
            mImageInfoData = imageInfoData;
        }

        public ImageInfoData getImageInfoData() {
            return mImageInfoData;
        }
    }
}
