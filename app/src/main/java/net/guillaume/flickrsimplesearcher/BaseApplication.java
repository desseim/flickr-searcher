package net.guillaume.flickrsimplesearcher;

import android.app.Application;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import net.guillaume.flickrsimplesearcher.inject.ApplicationModule;

import dagger.ObjectGraph;

public class BaseApplication extends Application {

    private ObjectGraph mApplicationGraph;

    @Override public void onCreate() {
        super.onCreate();

        mApplicationGraph = ObjectGraph.create(getModules().toArray());
    }

    /*package*/ Optional<ObjectGraph> getApplicationGraph() {
        return Optional.fromNullable(mApplicationGraph);
    }

    protected ImmutableList<Object> getModules() {
        return ImmutableList.of((Object) new ApplicationModule(this));
    }

}
