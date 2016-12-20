
 package com.wixoneapp.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.reactnativenavigation.NavigationApplication;
import com.wixoneapp.ApplicationObjects;
import com.wixoneapp.notifications.posting.NotificationSummaryStrategy;
import com.wixoneapp.notifications.posting.NotificationsStore;
import com.wixoneapp.notifications.posting.NotificationsStore.StoreException;

import java.util.List;
import java.util.Map;

public class PushNotification {

    private static final String TAG = PushNotification.class.getSimpleName();

    public static class InvalidNotificationException extends Exception {
        public InvalidNotificationException(String detailMessage) {
            super(detailMessage);
        }
    }

    private static final NotificationSummaryStrategy sSummaryStrategy = new NotificationSummaryStrategy();

    private final Context mAppContext;
    private PushNotificationProps mNotificationProps;

    public PushNotification(Context context, Bundle bundle) {
        mAppContext = context.getApplicationContext();
        mNotificationProps = new PushNotificationProps(bundle);
    }

    public void onReceived() throws InvalidNotificationException {
        if (mNotificationProps.getMetaSiteId() == null) {
            throw new InvalidNotificationException("No meta-site / link data");
        }

        postNotification();
        
        if (NavigationApplication.instance.isReactContextInitialized()) {
            final WritableMap notificationAsMap = Arguments.fromBundle(mNotificationProps.asBundle());
            NavigationApplication.instance.sendNavigatorEvent("notificationReceived", notificationAsMap);
        }
    }

    public void onOpened() {
        handleNotification();
    }

    public PushNotificationProps asProps() {
        return mNotificationProps.copy();
    }

    private void postNotification() {
        final Map<Integer, Notification> notifications = getNotificationsMap();
        if (notifications == null) {
            return;
        }

        final NotificationManager notificationManager = (NotificationManager) mAppContext.getSystemService(Context.NOTIFICATION_SERVICE);
        for (Map.Entry<Integer, Notification> entry : notifications.entrySet()) {
            final Integer id = entry.getKey();
            final Notification notification = entry.getValue();
            notificationManager.notify(id, notification);
        }
    }

    private Map<Integer, Notification> getNotificationsMap() {
        final NotificationsStore store = ApplicationObjects.get().getNotificationsStore();
        List<PushNotificationProps> allNotifications;
        try {
            store.addNotification(mNotificationProps);
            allNotifications = store.getAllNotifications();
        } catch (StoreException e) {
            Log.e(TAG, "Failed to update notifications store - new notification will be ignored", e);
            return null;
        }

        return sSummaryStrategy.getNotificationsMap(mAppContext, allNotifications);
    }

    private void handleNotification() {
        final WritableMap notificationAsMap = Arguments.fromBundle(mNotificationProps.asBundle());
        NavigationApplication.instance.sendNavigatorEvent("notificationOpened", notificationAsMap);
    }
}
