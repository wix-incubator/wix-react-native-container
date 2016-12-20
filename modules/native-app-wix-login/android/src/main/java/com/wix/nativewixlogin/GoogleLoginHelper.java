

//see GAPS.md

//package com.wix.nativewixlogin;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//
//import com.facebook.react.bridge.ActivityEventListener;
//import com.facebook.react.bridge.Arguments;
//import com.facebook.react.bridge.Promise;
//import com.facebook.react.bridge.ReactApplicationContext;
//import com.facebook.react.bridge.WritableMap;
//import com.google.android.gms.auth.api.Auth;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.auth.api.signin.GoogleSignInResult;
//import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
//import com.google.android.gms.common.api.GoogleApiClient;
//
//public class GoogleLoginHelper implements ActivityEventListener {
//
//    public static final int GGL_LOGIN_RESULT_CODE = 9001;
//
//    private ReactApplicationContext reactContext;
//    private GoogleApiClient apiClient;
//    private Promise loginPromise;
//
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == GGL_LOGIN_RESULT_CODE) {
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//
//            if (!result.isSuccess()) {
//                loginPromise.reject("GoogleSignIn", GoogleSignInStatusCodes.getStatusCodeString(result.getStatus().getStatusCode()));
//                return;
//            }
//
//            WritableMap params = Arguments.createMap();
//            GoogleSignInAccount acct = result.getSignInAccount();
//
//            params.putString("id", acct.getId());
//            params.putString("name", acct.getDisplayName());
//            params.putString("email", acct.getEmail());
//            params.putString("idToken", acct.getIdToken());
//            params.putString("serverAuthCode", acct.getServerAuthCode());
//
//            loginPromise.resolve(params);
//            loginPromise = null;
//        }
//    }
//
//    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
//        onActivityResult(requestCode, resultCode, data);
//    }
//
//    public void onNewIntent(Intent intent) {
//    }
//
//    public GoogleLoginHelper(ReactApplicationContext reactApplicationContext) {
//        reactContext = reactApplicationContext;
//        reactContext.addActivityEventListener(this);
//    }
//
//    public void setGoogleClientId(final String googleClientId) {
//        reactContext.runOnUiQueueThread(new Runnable() {
//
//            @Override
//            public void run() {
//                Log.d("NativeLogin", googleClientId);
//                GoogleSignInOptions apiOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                        .requestIdToken(googleClientId)
//                        .requestServerAuthCode(googleClientId)
//                        .requestId()
//                        .requestEmail()
//                        .build();
//
//                apiClient = new GoogleApiClient.Builder(reactContext)
//                        .addApi(Auth.GOOGLE_SIGN_IN_API, apiOptions)
//                        .build();
//                apiClient.connect();
//            }
//        });
//    }
//
//    public void login(final Promise promise) {
//
//        this.loginPromise = promise;
//
//        if (apiClient == null) {
//            throw new RuntimeException("Trying to connect with no Google Client!");
//        }
//
//        reactContext.runOnUiQueueThread(new Runnable() {
//            @Override
//            public void run() {
//                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
//                reactContext.startActivityForResult(signInIntent, GGL_LOGIN_RESULT_CODE, null);
//            }
//        });
//    }
//
//    public void logout() {
//
//        if (apiClient == null) {
//            throw new RuntimeException("Trying to sign out with no Google Client!");
//        }
//
//        Auth.GoogleSignInApi.signOut(apiClient);
//    }
//}
