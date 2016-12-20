
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface WixLoginAppDelegateHandler : NSObject

+(BOOL)application:(UIApplication*)application handleDidFinishLaunchingWithOptions:(NSDictionary *)launchOptions;
+(BOOL)application:(UIApplication *)app openURL:(NSURL *)url options:(NSDictionary *)options;
+(BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation;

@end
