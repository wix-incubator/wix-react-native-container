
#import "WixLoginAppDelegateHandler.h"
#import <GoogleSignIn/GoogleSignIn.h>
#import <FBSDKCoreKit/FBSDKCoreKit.h>

@implementation WixLoginAppDelegateHandler

+(BOOL)application:(UIApplication*)application handleDidFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    return [[FBSDKApplicationDelegate sharedInstance] application:application didFinishLaunchingWithOptions:launchOptions];
}

+(BOOL)application:(UIApplication *)app openURL:(NSURL *)url options:(NSDictionary *)options
{
    NSString *sourceApplication = options[UIApplicationOpenURLOptionsSourceApplicationKey];
    id annotation = options[UIApplicationOpenURLOptionsAnnotationKey];
    
    BOOL signInHandled = [[GIDSignIn sharedInstance] handleURL:url sourceApplication:sourceApplication annotation:annotation];
    
    if (!signInHandled)
    {
        signInHandled = [[FBSDKApplicationDelegate sharedInstance] application:app openURL:url sourceApplication:sourceApplication annotation:annotation];
    }
    
    return signInHandled;
}

+(BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation
{
    BOOL signInHandled = [[GIDSignIn sharedInstance] handleURL:url sourceApplication:sourceApplication annotation:annotation];
    
    if (!signInHandled)
    {
        signInHandled = [[FBSDKApplicationDelegate sharedInstance] application:application openURL:url sourceApplication:sourceApplication annotation:annotation];
    }
    
    return signInHandled;
}

@end
