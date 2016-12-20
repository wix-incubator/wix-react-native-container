
 
package com.wixoneapp.notifications.dispatching;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.reactnativenavigation.NavigationApplication;
import com.wix.container.MainActivity;
import com.wixoneapp.notifications.NotificationIntentAdapter;
import com.wixoneapp.notifications.PushNotification;
import com.wixoneapp.notifications.dispatching.RNPushNotifications.InitialNotificationPersistence;

public class ProxyActivityDbg extends AppCompatActivity {

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    protected void handleNotificationImmediately() {
        final PushNotification notification = new PushNotification(this, getIntent().getExtras());
        if (notification != null) {
            notification.onOpened();
        }
    }

    protected void startFullAppAndStoreNotification() {
        InitialNotificationPersistence.setCookie(new NotificationIntentAdapter.PendingNotificationCookie(getIntent().getExtras()));

        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
