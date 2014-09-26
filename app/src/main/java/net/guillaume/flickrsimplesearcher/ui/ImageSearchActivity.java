package net.guillaume.flickrsimplesearcher.ui;

import android.os.Bundle;

import com.google.common.collect.Lists;

import net.guillaume.flickrsimplesearcher.BaseActivity;
import net.guillaume.flickrsimplesearcher.R;
import net.guillaume.flickrsimplesearcher.inject.NetworkModule;

import java.util.List;


public class ImageSearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_search);
    }

    @Override protected List<Object> getModules() {
        return Lists.newArrayList((Object)new NetworkModule());
    }

}
