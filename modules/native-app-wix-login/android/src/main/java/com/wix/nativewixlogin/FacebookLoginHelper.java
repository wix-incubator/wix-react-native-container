

//see GAPS.md

//package com.wix.nativewixlogin;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//
//import com.facebook.AccessToken;
//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.FacebookRequestError;
//import com.facebook.FacebookSdk;
//import com.facebook.GraphRequest;
//import com.facebook.GraphResponse;
//import com.facebook.login.LoginManager;
//import com.facebook.login.LoginResult;
//import com.facebook.react.bridge.ActivityEventListener;
//import com.facebook.react.bridge.Arguments;
//import com.facebook.react.bridge.Promise;
//import com.facebook.react.bridge.ReactApplicationContext;
//import com.facebook.react.bridge.WritableMap;
//
//import org.json.JSONObject;
//
//import java.util.Arrays;
//
//public class FacebookLoginHelper implements ActivityEventListener {
//
//    private final ReactApplicationContext reactContext;
//    private Promise loginPromise;
//    private final CallbackManager callbackManager;
//
//    public FacebookLoginHelper(ReactApplicationContext reactApplicationContext) {
//        reactContext = reactApplicationContext;
//        reactContext.addActivityEventListener(this);
//        FacebookSdk.sdkInitialize(reactApplicationContext.getApplicationContext());
//        callbackManager = CallbackManager.Factory.create();
//
//        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                verifyPromise();
//                handleSuccesfulLogin(loginResult);
//            }
//
//            @Override
//            public void onCancel() {
//                verifyPromise();
//                WritableMap map = Arguments.createMap();
//                map.putBoolean("isCanceled", true);
//                loginPromise.resolve(map);
//                loginPromise = null;
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                verifyPromise();
//                loginPromise.reject("FacebookSignIn", "Facebook signin error");
//                loginPromise = null;
//            }
//        });
//    }
//
//    private void handleSuccesfulLogin(LoginResult loginResult) {
//        final AccessToken accessToken = loginResult.getAccessToken();
//
//        GraphRequest request = GraphRequest.newMeRequest(accessToken,
//            new GraphRequest.GraphJSONObjectCallback() {
//                @Override
//                public void onCompleted(JSONObject object, GraphResponse response) {
//                    handleGraphRequest(accessToken, object, response);
//                }
//            });
//
//        Bundle params = new Bundle();
//        String fields = "id, email";
//        params.putString("fields", fields);
//        request.setParameters(params);
//        request.executeAsync();
//    }
//
//    private void handleGraphRequest(AccessToken accessToken, JSONObject object, GraphResponse response) {
//        FacebookRequestError error = response.getError();
//        if (error == null) {
//            WritableMap map = Arguments.createMap();
//
//            map.putString("email", object.optString("email"));
//            map.putString("userId", object.optString("id"));
//            map.putString("token", response.getRequest().getAccessToken().getToken());
//            map.putBoolean("isCanceled", false);
//
//            loginPromise.resolve(map);
//            loginPromise = null;
//
//        } else {
//            loginPromise.reject("FacebookSignIn", error.getErrorMessage());
//        }
//    }
//
//    private void verifyPromise() {
//        if (loginPromise == null) {
//            throw new RuntimeException("Got Facebook signin result with no promise!");
//        }
//    }
//
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//    }
//
//    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
//        onActivityResult(requestCode, resultCode, data);
//    }
//
//    public void onNewIntent(Intent intent) {
//    }
//
//    public void logout() {
//        LoginManager.getInstance().logOut();
//    }
//
//    public void login(Promise promise, Activity activity) {
//        loginPromise = promise;
//        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"));
//    }
//}
