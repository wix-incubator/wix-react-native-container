
 package com.wixoneapp.gcm;

import android.os.Bundle;

public class NewGcmMessageEvent {
    private final Bundle mBundle;

    public NewGcmMessageEvent(Bundle bundle) {
        mBundle = bundle;
    }

    public Bundle getBundle() {
        return mBundle;
    }
}
