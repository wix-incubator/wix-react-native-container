# The Wix Mobile Container App

## Build The Project

### Requirements
1. Make sure you have all [requirements](https://facebook.github.io/react-native/docs/getting-started.html#requirements) for building React Native projects
2. Make sure you're using a macOS El Capitan and have Xcode 7.3.x installed (for iOS builds) and Android Studio (for Android builds)
3. Make sure you have npm 3+ installed (`npm -v`)
4. run `npm install`

### iOS

* Open `ios/WixReactNativeContainer.xcodeproj` inside Xcode<br>
run `npm run xcode` to open Xcode
* Alternatively, using command line only, run `react-native run-ios`

### Android

* Open `/android` inside Android Studio<br>
or run `npm run studio` to open Android Studio
* run `npm run start` to start the React Native packager
* Alternatively, using command line only run `react-native run-android`

## Configuration Values and Credentials

The project does not contain internal configuration values and credentials, like:

* Google Play identities and certificates
* Apple Developer identities and certificates
* The Facebook API Key and Facebook App ID
* Google API Keys and Client ID
* etc

In order to use the various services with the project, you will have to create all relevant accounts under your own name and replace the configuration placeholder values with your own credentials.

## Gaps

see [GAPS](GAPS.md)

## License

See [LICENSE](LICENSE)

## Open Source Attributions

See [ATTRIBUTIONS](ATTRIBUTIONS.md)
