
 package com.wixoneapp.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.wix.container.R;

import java.io.IOException;

public class GcmInstanceIdRefreshHandlerService extends IntentService {

    private final String TAG = this.getClass().getSimpleName();

    public GcmInstanceIdRefreshHandlerService() {
        super(GcmInstanceIdRefreshHandlerService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GoogleCloudMessaging.getInstance(getApplicationContext()).close();
        final InstanceID instanceId = InstanceID.getInstance(getApplicationContext());
        Log.i(TAG, "GCM is refreshing token... instanceId=" + instanceId.getId());

        final String registrationToken;
        try {
            registrationToken = instanceId.getToken(getSenderId(), GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            Log.i(TAG, "GCM has a new token: instanceId=" + instanceId.getId() + ", token=" + registrationToken);
        } catch (IOException e) {
            Log.e(TAG, "FATAL: Failed to fetch a fresh new token, instanceId=" + instanceId.getId(), e);
            return;
        }

        notifyTokenEvent(registrationToken);
    }

    private String getSenderId() {
        return getApplicationContext().getResources().getString(R.string.gcmSenderId);
    }

    private void notifyTokenEvent(String registrationToken) {

    }
}
