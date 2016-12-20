
 package com.wix.container;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ReactContext;
import com.github.alinz.reactnativewebviewbridge.WebViewBridgePackage;
import com.reactnativenavigation.NavigationApplication;
import com.wix.RNCameraKit.RNCameraKitPackage;
import com.wix.RNSwipeView.SwipeViewPackage;
import com.wix.videoview.ReactVideoViewPackage;
import com.wixoneapp.ApplicationObjects;
import com.wixoneapp.LifecycleCallbacks;
import com.wixoneapp.NetworkClientInitializer;
import com.wixoneapp.notifications.dispatching.RNPushNotifications.PushNotificationsPackage;

import java.util.Arrays;
import java.util.List;

//see GAPS.md
//import com.wix.nativewixlogin.RNNativeWixLoginPackage;


public class MainApplication extends NavigationApplication {

  private ApplicationObjects appObjects;
  public static MainApplication instance;

  @Override
  public void onCreate() {
    super.onCreate();
    instance = this;

    // Set all activities to portrait
    registerActivityLifecycleCallbacks(new LifecycleCallbacks() {
      @Override
      public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
      }
    });

    initAppObjects();

    NetworkClientInitializer.init();
  }

  @Override
  public boolean isDebug() {
    return BuildConfig.DEBUG;
  }

  @Nullable
  @Override
  public List<ReactPackage> createAdditionalReactPackages() {
    return Arrays.asList(
            //see GAPS.md
//            new RNNativeWixLoginPackage(),
            new RNCameraKitPackage(),
            new ReactVideoViewPackage(),
            new WebViewBridgePackage(),
            new SwipeViewPackage(),
            new PushNotificationsPackage()
    );
  }

  @Override
  public void onReactInitialized(ReactContext reactContext) {
    super.onReactInitialized(reactContext);
  }

  private void initAppObjects() {
    appObjects = ApplicationObjects.get();
    appObjects.init(this);
  }
}
