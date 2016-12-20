
 package com.wixoneapp.notifications.dispatching.RNPushNotifications;

import com.wixoneapp.notifications.NotificationIntentAdapter.PendingNotificationCookie;

public class InitialNotificationPersistence {

    private static PendingNotificationCookie sCookie;

    public static void setCookie(PendingNotificationCookie cookie) {
        sCookie = cookie;
    }

    public static PendingNotificationCookie getCookie() {
        return sCookie;
    }
}
