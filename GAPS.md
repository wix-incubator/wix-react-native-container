# Gaps

* Third party libraries (GoogleSignInSDK and FacebookSDK) are not a part of the project.
* Code references to the above have been left inside the released source files in order to provide a clear picture of where they fit in. Read below for specific instructions on how to resolve them.


## iOS

### Social Login (modules/native-app-wix-login)

#### Facebook SDK
1. Download Facebook SDK - https://developers.facebook.com/docs/ios (tested on 4.17.0)
2. Follow the Facebook SDK installation instructions (place the frameworks in this directory)

#### GoogleSignIn SDK
1. Download GoogleSignIn SDK - https://developers.google.com/identity/sign-in/ios/sdk/ (tested on 4.0.0)
2. Follow the GoogleSignIn SDK installation instructions.

#### When both are added, enable Google/Facebook SDK integration
1. Add all `.m` files in `libWixLogin/SocialLogin` to `WixReactNativeContainer` target (Target Membership)
2. Search for `//see GAPS.md` comments in `AppDelegate.m` and uncomment the following lines.


### GCM
1. Acquire `GoogleService-Info.plist` by following the instructions here <https://developers.google.com/cloud-messaging/ios/client>


## Android

### Social Login (modules/native-app-wix-login)
1. add Google Services Auth SDK and Facebook SDK to the dependencies in `/android/build.gradle`.
  
  ```gradle
  dependencies {
    ...
    compile 'com.google.android.gms:play-services-auth:9.4.0'
    compile 'com.facebook.android:facebook-android-sdk:4.10.+'
  }
  ```
2. Uncomment code in `/android/src/main/java/com/wix/nativewixlogin`

### GCM
1. Acquire `google-services.json` by following the instructions here <https://developers.google.com/cloud-messaging/android/client>
