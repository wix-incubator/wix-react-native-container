
 package com.wixoneapp.notifications;

import android.os.Bundle;


public class PushNotificationProps {

    private Bundle mBundle;

    public PushNotificationProps(Bundle bundle) {
        mBundle = bundle;
    }

    public PushNotificationProps(String link, String category, String metaSiteId) {
        mBundle = new Bundle();
        mBundle.putString("link", link);
        mBundle.putString("category", category);
        mBundle.putString("metaSiteId", metaSiteId);
    }

    public String getLink() {
        return mBundle.getString("link");
    }

    public String getCategory() {
        return mBundle.getString("category");
    }

    public String getMetaSiteId() {
        return mBundle.getString("metaSiteId");
    }

    public String getTitle() {
        return mBundle.getString("title");
    }

    public String getBody() {
        return mBundle.getString("body");
    }

    public Bundle asBundle() {
        return (Bundle) mBundle.clone();
    }

    @Override
    public String toString() {
        return mBundle.toString();
    }

    protected PushNotificationProps copy() {
        return new PushNotificationProps((Bundle) mBundle.clone());
    }
}
