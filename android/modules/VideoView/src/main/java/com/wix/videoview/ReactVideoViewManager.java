
 package com.wix.videoview;

import android.support.annotation.Nullable;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;

public class ReactVideoViewManager extends ViewGroupManager<ReactVideoView> {

    public static final String REACT_CLASS = "VideoView";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public ReactVideoView createViewInstance(ThemedReactContext context) {
        return new ReactVideoView(context);
    }

    @ReactProp(name = "source")
    public void setSource(ReactVideoView view, @Nullable ReadableMap source) {
        view.setSource(((ReadableNativeMap) source).toHashMap());
    }

    @ReactProp(name = "loopVideo")
    public void setLoopVideo(ReactVideoView view, boolean loopVideo) {
        view.setLoopVideo(loopVideo);
    }

    @ReactProp(name = "loadingImage")
    public void setLoadingImage(ReactVideoView view, @Nullable ReadableMap loadingImage) {
        view.setLoadingImage(((ReadableNativeMap) loadingImage).toHashMap());
    }
}
