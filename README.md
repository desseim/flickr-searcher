flickr-searcher [![Build Status](https://travis-ci.org/desseim/flickr-searcher.svg?branch=master)](https://travis-ci.org/desseim/flickr-searcher)
===============

:bulb: *This app demonstrated a few good practices to architect an Android app in 2014. Although it has been updated to build in Android Studio Chipmunk, some / most of its concept have since then been made obsolete, mostly by advances in the official Android development environment. Nevertheless, some of the architectural concepts it adresses are still relevant and can be of interest to a modern application, despite some of its external libraries and tools, or their versions, being now deprecated.*

:warning: Due to this app still implementing the "old" permission mechanism, on modern devices after installation the user must manually give the app location permission from the device settings before being able to run the app.

Simple Android app allowing to search Flickr for pictures, with an option to find pictures taken near the user current place.


## Screenshots
![screenshot of results of a search](../gh-pages/screenshots/search_skateboarding.png?raw=true "Searching for 'skateboarding'")
![screenshot of results of a search with location enable](../gh-pages/screenshots/search_skateboarding_location_macba.png?raw=true "Searching for 'skateboarding' matching pictures taken nearby when at Macba")
![screenshot of showing the detail of a guy doing a kickflip](../gh-pages/screenshots/show_detail_kickflip_warschauer.png?raw=true "Showing the details of a picture")

## API token

The app communicates with the [Flickr API](https://secure.flickr.com/services/api/) and for this requires a [Flickr API key](https://secure.flickr.com/services/apps/create/).
So you will need to specify your key to build the app ; you can do so by adding the following line to your `~/.gradle/gradle.properties` file (or to the one of the repository i.e. `app/gradle.properties`):

    flickrApiKey=your-flickr-api-key

(replacing `your-flickr-api-key` with your actual key).

## Technologies

It takes advantages of:
* [`auto-parcel`](https://github.com/frankiesardo/auto-parcel) : for definition of clean, immutable and `Parcelable` internal data types
* [`dagger`](https://square.github.io/dagger/) : to inject most of the app components, with application- and activity-scoped injection
* [`guava`](https://code.google.com/p/guava-libraries/) : mostly for `Optional`s, immutable collections, and few other utilities as `Joiner`s
* [`otto`](https://square.github.io/otto/) : for cross-UI-components communication, and background tasks to UI notifications
* [`picasso`](https://square.github.io/picasso/) : for all image downloading / showing
* [`retrofit`](https://square.github.io/retrofit/) : simply for networking calls
* [`RxJava`](https://github.com/ReactiveX/RxJava) : for handling and combining asynchronous networks calls
* [`Simple`](http://simple.sourceforge.net/home.php) : as a converter for the retrofit `RestAdapter`, to handle Flickr xml formatted REST API

### Patterns

The application has 2 main injection graphs supporting 2 scopes of injection: application scope and activity scope.
As such, there is for example one event bus for each scope:
```java
@Inject @ForApplication Bus mApplicationBus;
@Inject @ForActivity Bus mActivityBus;
```
The application-scoped bus is used for application-level events (well, obviously...) like changes in the device location ;
the activity-scoped bus carry information which shouldn't leak outside of the scope of the `Activity` such as UI action events ("show fragment x").

The same activity-scoped injection graph is conserved across activity restarts (the dreaded configuration changes -- screen rotation), with provisions not to leak the activity,
which means that non-activity-depending objects such as the event bus can be declared `@Singleton` (effectively activity-singleton) and the same instance will be injected within
the scope of a given activity, even when it restarts.
For example, one can:
```java
public static ActivityA extends Activity {

  @Inject @ForActivity Bus mAcivityBus;

  // instance injection, registration to the bus etc...

  public void someMethod() {
    // ...
    startBackgroundTask(new ResultListener() {
      @Override public void call() {
        mActivityBus.post(new SomeEvent());  // closure of the activity bus at the time of the call to #someMethod()
      }
    }
  
  @Subscribe public void onSomeEvent(final SomeEvent event) {
    // this will be called even after the activity restarted (rotated) and even if #someMethod() was executed before the restart
  }

}

// of course, communication between independent activity-scoped elements are a simple matter of injecting a bus instance:
public static FragmentZ extends Fragment {

  @Inject @ForActivity Bus mActivityBus;  // same instance as the one held by the ActivityA it's attached to
  
  private void someOtherMethod() {
    // ...
    mActivityBus.post(new SomeEvent());  // will be delivered to the #onSomeEvent(SomeEvent) method of the instance of ActivityA this fragment instance is attached to
  }

}
```

The caveat is that one must be cautious about not leaking the `Activity` by scoping as "activity-singleton" an object which holds a reference to it 
(it would actually leak the first instance of the activity in this case).
Those should be dynamically provided upon each injection request.

## Roadmap

This is currently a very simple list of things to explore in the future.

- [ ] provide visual feedback (spinning wheel ?) while performing search
- [ ] hide search bar as well in detail view / full screen mode (triggered by a tap on the image)
  - [ ] use animations when switching between full-screen modes
- [x] re-trigger search on "search near current location" option check / uncheck
