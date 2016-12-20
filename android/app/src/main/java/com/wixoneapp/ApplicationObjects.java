
 package com.wixoneapp;

import android.content.Context;

import com.wixoneapp.notifications.posting.NotificationsStore;

public class ApplicationObjects {

    private static ApplicationObjects mInstance;

    private Context mAppContext;
    private NotificationsStore mNotificationsStore;

    public static ApplicationObjects get() {
        if (mInstance == null) {
            mInstance = new ApplicationObjects();
        }
        return mInstance;
    }

    private ApplicationObjects() {
    }

    public void init(Context appContext) {
        mAppContext = appContext;
    }

    public NotificationsStore getNotificationsStore() {
        if (mNotificationsStore == null) {
            mNotificationsStore = new NotificationsStore(mAppContext);
        }
        return mNotificationsStore;
    }
}
