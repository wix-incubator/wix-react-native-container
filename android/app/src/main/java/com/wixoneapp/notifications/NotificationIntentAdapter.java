
 package com.wixoneapp.notifications;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.drew.lang.annotations.NotNull;
import com.wix.container.MainApplication;
import com.wixoneapp.notifications.dispatching.ProxyActivity;

public class NotificationIntentAdapter {
    private static final int PENDING_INTENT_CODE = 0;
    private static final String PUSH_NOTIFICATION_EXTRA_NAME = "pushNotification";

    public static class PendingNotificationCookie {
        private final Bundle mRawNotificationData;

        public PendingNotificationCookie(Bundle rawNotificationData) {
            mRawNotificationData = rawNotificationData;
        }

        /* package */ Bundle getNotificationData() {
            return mRawNotificationData;
        }
    }

    public static PendingIntent getCTAIntent(Context appContext, PushNotificationProps notification) {
        final Intent cta = new Intent(appContext, ProxyActivity.class);
        cta.putExtra(PUSH_NOTIFICATION_EXTRA_NAME, notification.asBundle());
        return PendingIntent.getActivity(appContext, PENDING_INTENT_CODE, cta, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public static PendingIntent getMultiCategoriesCTAIntent(Context appContext, String metaSiteId) {
        final Intent cta = new Intent(appContext, ProxyActivity.class);
        cta.putExtra(PUSH_NOTIFICATION_EXTRA_NAME, new PushNotificationProps(null, "multi-category", metaSiteId).asBundle());
        return PendingIntent.getActivity(appContext, PENDING_INTENT_CODE, cta, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public static PushNotification getPendingNotification(Activity activity) {
        final Intent intent = activity.getIntent();
        final Bundle notificationData = intent.getBundleExtra(PUSH_NOTIFICATION_EXTRA_NAME);
        if (notificationData == null) {
            return null;
        }

        return new PushNotification(activity, notificationData);
    }

    @NotNull
    public static PendingNotificationCookie getPendingNotificationCookie(Activity activity) {
        final Intent intent = activity.getIntent();
        final Bundle notificationData = intent.getBundleExtra(PUSH_NOTIFICATION_EXTRA_NAME);
        return new PendingNotificationCookie(notificationData);
    }

    @Nullable
    public static PushNotification getPendingNotification(@Nullable PendingNotificationCookie cookie) {
        if (cookie == null) {
            return null;
        }

        final Bundle notificationData = cookie.getNotificationData();
        if (notificationData == null) {
            return null;
        }

        return new PushNotification(MainApplication.instance, notificationData);
    }

    public static void clearPendingNotification(Activity activity) {
        final Intent intent = activity.getIntent();
        intent.putExtra(PUSH_NOTIFICATION_EXTRA_NAME, (Bundle) null);
    }

}
