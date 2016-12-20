
#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import "RCTBridgeModule.h"

@interface WixLoginNativeManager : NSObject <RCTBridgeModule>
+(UIViewController*)getRootViewController;
+(NSString*)safeString:(NSString*)string;
+(NSString*)googleClientId;
+(void)registerForGoogleClientIdChangedNotif:(id)receiver action:(SEL)action;
+(void)unregisterForGoogleClientIdChangedNotif:(id)receiver;
@end
