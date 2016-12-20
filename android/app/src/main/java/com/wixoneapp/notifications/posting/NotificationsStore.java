
 package com.wixoneapp.notifications.posting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.wixoneapp.notifications.PushNotificationProps;
import com.wixoneapp.preferences.SharedPrefKeys;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NotificationsStore {

    protected final static String COUNT_PREF_KEY = "count";

    private final Context mAppContext;

    public static class StoreException extends Exception {
        public StoreException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }
    }

    public NotificationsStore(Context appContext) {
        mAppContext = appContext.getApplicationContext();
    }

    public synchronized void addNotification(PushNotificationProps notification) throws StoreException {
        final SharedPreferences sharedPref = getSharedPreferences();

        JSONObject notificationAsJson;
        try {
            notificationAsJson = bundleToJson(notification.asBundle());
        } catch (JSONException e) {
            throw new StoreException("Failed to add notification", e);
        }

        final int count = sharedPref.getInt(COUNT_PREF_KEY, 0);
        sharedPref.edit().putString(getIndexKey(count), notificationAsJson.toString()).putInt(COUNT_PREF_KEY, count + 1).apply();
    }

    public synchronized List<PushNotificationProps> getAllNotifications() throws StoreException {
        final SharedPreferences sharedPref = getSharedPreferences();

        try {
            final List<String> rawNotifications = readRawNotifications(sharedPref);
            return convertRawToConcrete(rawNotifications);
        } catch (JSONException e) {
            throw new StoreException("Notifications read failed due to a JSON parsing error", e);
        }
    }

    public synchronized void clearAll() {
        final SharedPreferences sharedPref = getSharedPreferences();
        sharedPref.edit().clear().apply();
    }

    private SharedPreferences getSharedPreferences() {
        return mAppContext.getSharedPreferences(SharedPrefKeys.NOTIFICATIONS_STORE, Context.MODE_PRIVATE);
    }

    protected List<String> readRawNotifications(SharedPreferences sharedPref) {
        final int count = sharedPref.getInt(COUNT_PREF_KEY, 0);
        final List<String> rawNotifications = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            final String rawNotification = sharedPref.getString(getIndexKey(i), null);
            rawNotifications.add(rawNotification);
        }

        return rawNotifications;
    }

    protected List<PushNotificationProps> convertRawToConcrete(List<String> rawNotifications) throws JSONException {
        final ArrayList<PushNotificationProps> notifications = new ArrayList<>(rawNotifications.size());
        for (String rawNotification : rawNotifications) {
            final JSONObject jsonObject = new JSONObject(rawNotification);
            final Bundle bundle = jsonToBundle(jsonObject);
            notifications.add(new PushNotificationProps(bundle));
        }
        return notifications;
    }

    protected String getIndexKey(int index) {
        return "pref#"+index;
    }

    protected Bundle jsonToBundle(JSONObject jsonObject) throws JSONException {
        final Bundle bundle = new Bundle();
        for (Iterator<String> iterator = jsonObject.keys(); iterator.hasNext(); ) {
            final String key = iterator.next();
            bundle.putString(key, jsonObject.get(key).toString());
        }
        return bundle;
    }

    /**
     * Note: this implementation assumes a flat, simple (string-based) implementation of a push-notification bundle.
     */
    protected JSONObject bundleToJson(Bundle bundle) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        for (String key : bundle.keySet()) {
            jsonObject.put(key, bundle.get(key));
        }
        return jsonObject;

    }
}
