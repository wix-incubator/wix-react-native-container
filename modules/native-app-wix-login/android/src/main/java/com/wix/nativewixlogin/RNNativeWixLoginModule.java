

//see GAPS.md

//package com.wix.nativewixlogin;
//
//import android.app.Activity;
//import android.support.annotation.NonNull;
//
//import com.facebook.react.bridge.Promise;
//import com.facebook.react.bridge.ReactApplicationContext;
//import com.facebook.react.bridge.ReactContextBaseJavaModule;
//import com.facebook.react.bridge.ReactMethod;
//import com.facebook.react.bridge.ReadableArray;
//
//public class RNNativeWixLoginModule extends ReactContextBaseJavaModule{
//
//    private final GoogleLoginHelper googleHelper;
//    private final FacebookLoginHelper facebookHelper;
//    private ReactApplicationContext reactContext;
//
//    public RNNativeWixLoginModule(ReactApplicationContext reactContext) {
//        super(reactContext);
//        this.reactContext = reactContext;
//        googleHelper = new GoogleLoginHelper(reactContext);
//        facebookHelper = new FacebookLoginHelper(reactContext);
//    }
//
//    @Override
//    public String getName() {
//        return "WixLoginNativeManager";
//    }
//
//    @ReactMethod
//    public void loginWithGoogle(Promise promise) {
//        googleHelper.login(promise);
//    }
//
//    @ReactMethod
//    public void setGoogleClientId(final String googleClientId) {
//        reactContext.runOnUiQueueThread(new Runnable() {
//            @Override
//            public void run() {
//                googleHelper.setGoogleClientId(googleClientId);
//            }
//        });
//    }
//
//    @ReactMethod
//    public void logoutFromGoogle() {
//        reactContext.runOnUiQueueThread(new Runnable() {
//            @Override
//            public void run() {
//                googleHelper.logout();
//            }
//        });
//    }
//
//    @ReactMethod
//    public void logoutFromFacebook() {
//        reactContext.runOnUiQueueThread(new Runnable() {
//            @Override
//            public void run() {
//                facebookHelper.logout();
//            }
//        });
//    }
//
//    @ReactMethod
//    public void loginWithFacebook(final Promise promise) {
//        reactContext.runOnUiQueueThread(new Runnable() {
//            @Override
//            public void run() {
//                facebookHelper.login(promise, getActivity());
//            }
//        });
//    }
//
//    @Deprecated
//    @ReactMethod
//    public void deleteCookies(ReadableArray cookies, String domain) {
//    }
//
//    @NonNull
//    private Activity getActivity() {
//        final Activity activity = getCurrentActivity();
//        if (activity == null) {
//            throw(new RuntimeException("No activity in Native Wix Login"));
//        }
//        return activity;
//    }
//}
