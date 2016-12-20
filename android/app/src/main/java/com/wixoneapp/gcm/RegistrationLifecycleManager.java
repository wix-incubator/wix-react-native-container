
 package com.wixoneapp.gcm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.reactnativenavigation.NavigationApplication;
import com.wixoneapp.ReactContextInitializedEvent;

public class RegistrationLifecycleManager {

    private static final String TAG = RegistrationLifecycleManager.class.getSimpleName();

    private final Context mAppContext;

    private String mToken;

    public RegistrationLifecycleManager(Context appContext) {
        mAppContext = appContext;
    }

    public void onReactContextInitialized(ReactContextInitializedEvent event) {
        // refreshed by the app when ready, anyways.
        Log.i(TAG, "React init => asking for new token");
        refreshGcmToken();
    }

    public synchronized void onManualTokenRefreshEvent(ManualGcmTokenRefreshEvent event) {
        if (mToken == null) {
            Log.i(TAG, "Manual token refresh => asking for new token");
            refreshGcmToken();
        } else {
            Log.i(TAG, "Manual token refresh => publishing existing token ("+mToken+")");
            sendTokenToJS();
        }
    }

    public synchronized void onNewToken(NewGcmTokenEvent event) {
        Log.i(TAG, "Got new token => notifying JS");
        mToken = event.getToken();
        sendTokenToJS();
    }

    private void refreshGcmToken() {
        final Intent tokenFetchIntent = new Intent(mAppContext, GcmInstanceIdRefreshHandlerService.class);
        mAppContext.startService(tokenFetchIntent);
    }

    private void sendTokenToJS() {
        NavigationApplication.instance.sendEvent("remoteNotificationsRegistered", mToken);
    }
}
