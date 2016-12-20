
 package com.wixoneapp.notifications.dispatching.RNPushNotifications;

import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.wixoneapp.notifications.NotificationIntentAdapter;
import com.wixoneapp.notifications.NotificationIntentAdapter.PendingNotificationCookie;
import com.wixoneapp.notifications.PushNotification;

public class PushNotificationsModule extends ReactContextBaseJavaModule {

    private static final String TAG = PushNotificationsModule.class.getSimpleName();

    public PushNotificationsModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "RNPushNotifications";
    }

    @ReactMethod
    public void getPendingNotificationData(final Promise promise) {
        Log.d(TAG, "Native method invocation: getPendingNotificationData()");

        Object result = null;
        try {
            final PendingNotificationCookie pendingNotificationCookie = InitialNotificationPersistence.getCookie();
            if (pendingNotificationCookie == null) {
                return;
            }

            final PushNotification pendingNotification = NotificationIntentAdapter.getPendingNotification(pendingNotificationCookie);
            if (pendingNotification == null) {
                return;
            }

            final Bundle rawNotificationData = pendingNotification.asProps().asBundle();
            result = Arguments.fromBundle(rawNotificationData);
        } finally {
            promise.resolve(result);
        }
    }

    @ReactMethod
    public void refreshToken() {
        Log.d(TAG, "Native method invocation: refreshToken()");
    }
}
