
 package com.wixoneapp.gcm;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class GcmMessageHandleService extends GcmListenerService {
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onMessageReceived(String s, Bundle bundle) {
        Log.i(TAG, "New message from GCM: " + bundle);
        dispatchMessage(bundle);
    }

    private void dispatchMessage(Bundle bundle) {

    }
}
