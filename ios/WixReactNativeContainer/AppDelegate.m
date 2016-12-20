
#import "AppDelegate.h"
#import "RCTRootView.h"
#import "RCCManager.h"
// see GAPS.md
//#import "WixLoginAppDelegateHandler.h"
#import "RNNotifications.h"
#import "RCTBundleURLProvider.h"

#ifdef DEBUG
#define isDebug true
#else
#define isDebug false
#endif

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
  NSURL *jsCodeLocation;
  
  jsCodeLocation = [[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:@"index.ios" fallbackResource:nil];
  
  // React Native Controllers bootstrap
  self.window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
  self.window.backgroundColor = [UIColor whiteColor];
  [[RCCManager sharedIntance] initBridgeWithBundleURL:jsCodeLocation launchOptions:launchOptions];
  
  // Default app style
  self.window.tintColor = [UIColor colorWithRed:0 green:0.678 blue:0.961 alpha:1];
  
  return YES;
}

// Required to register for notifications
- (void)application:(UIApplication *)application didRegisterUserNotificationSettings:(UIUserNotificationSettings *)notificationSettings
{
  [RNNotifications didRegisterUserNotificationSettings:notificationSettings];
}
// Required for the register event.
- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken
{
  [RNNotifications didRegisterForRemoteNotificationsWithDeviceToken:deviceToken];
}


// Required for the notification event.
- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)notification
{
  [RNNotifications didReceiveRemoteNotification:notification];
}

// Required for the localNotification event.
- (void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification
{
  [RNNotifications didReceiveLocalNotification:notification];
}

- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url options:(NSDictionary *)options
{
    // see GAPS.md
//  return [WixLoginAppDelegateHandler application:application openURL:url options:options];
  return NO;
}

- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation
{
    // see GAPS.md
//  return [WixLoginAppDelegateHandler application:application openURL:url sourceApplication:sourceApplication annotation:annotation];
  return NO;
}


@end
