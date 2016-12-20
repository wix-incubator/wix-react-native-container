
 package com.wixoneapp.gcm;

public class NewGcmTokenEvent {
    private final String mToken;

    public NewGcmTokenEvent(String token) {
        this.mToken = token;
    }

    public String getToken() {
        return mToken;
    }
}
