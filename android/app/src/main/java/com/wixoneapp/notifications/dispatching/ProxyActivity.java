
 package com.wixoneapp.notifications.dispatching;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.reactnativenavigation.NavigationApplication;
import com.wix.container.MainActivity;
import com.wixoneapp.notifications.NotificationIntentAdapter;
import com.wixoneapp.notifications.NotificationIntentAdapter.PendingNotificationCookie;
import com.wixoneapp.notifications.PushNotification;
import com.wixoneapp.notifications.dispatching.RNPushNotifications.InitialNotificationPersistence;

/**
 * The front-end (hidden) activity for handling notification <b>opening</b> actions triggered by the
 * device user (i.e. upon tapping them in the notifications drawer).
 */
public class ProxyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (NavigationApplication.instance.isReactContextInitialized()) {
            handleNotificationImmediately();
        } else {
            startFullAppAndStoreNotification();
        }

        finish();
    }

    protected void handleNotificationImmediately() {
        final PushNotification notification = NotificationIntentAdapter.getPendingNotification(this);
        if (notification != null) {
            notification.onOpened();
        }
    }

    protected void startFullAppAndStoreNotification() {
        final PendingNotificationCookie notificationCookie = NotificationIntentAdapter.getPendingNotificationCookie(this);
        InitialNotificationPersistence.setCookie(notificationCookie);

        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
