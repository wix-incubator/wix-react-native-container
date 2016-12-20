
 package com.wixoneapp.notifications.posting;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.wixoneapp.ApplicationObjects;
import com.wixoneapp.LifecycleCallbacks;
import com.wixoneapp.gcm.NewGcmMessageEvent;
import com.wixoneapp.notifications.PushNotification;

public class NotificationsPostingLifecycleManager {

    private final String TAG = this.getClass().getSimpleName();

    private final Context mAppContext;

    public NotificationsPostingLifecycleManager(Application application) {
        mAppContext = application;

        application.registerActivityLifecycleCallbacks(new LifecycleCallbacks() {
            @Override
            public void onActivityResumed(Activity activity) {
                clearAllPostedNotifications();
            }
        });
    }

    public void onNewGcmMessage(NewGcmMessageEvent event) {
        final PushNotification notification = new PushNotification(mAppContext, event.getBundle());
        try {
            notification.onReceived();
        } catch (PushNotification.InvalidNotificationException e) {
            // A GCM message, yes - but not the kind we know how to work with.
            Log.v(TAG, "GCM message handling aborted", e);
        }
    }

    private void clearAllPostedNotifications() {
        final NotificationManager notificationManager = (NotificationManager) mAppContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        ApplicationObjects.get().getNotificationsStore().clearAll();
    }
}
