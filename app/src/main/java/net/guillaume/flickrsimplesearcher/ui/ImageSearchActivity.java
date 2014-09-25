package net.guillaume.flickrsimplesearcher.ui;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import net.guillaume.flickrsimplesearcher.BaseActivity;
import net.guillaume.flickrsimplesearcher.R;
import net.guillaume.flickrsimplesearcher.data.ImageData;
import net.guillaume.flickrsimplesearcher.inject.NetworkModule;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


public class ImageSearchActivity extends BaseActivity {

    @Inject FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_search);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_search_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            onSearchRequested();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onBackPressed() {
        // search fragment may want to do something on its own on back key press

        final ImageSearchFragment imageSearchFragment = (ImageSearchFragment)mFragmentManager.findFragmentById(R.id.image_search_fragment);
        if (imageSearchFragment != null) {
            final boolean backPressHandled = imageSearchFragment.onBackPress();
            if (!backPressHandled) super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override protected List<Object> getModules() {
        return Lists.newArrayList((Object)new NetworkModule());
    }

}
