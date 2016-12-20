
#import <Foundation/Foundation.h>
#import <GoogleSignIn/GoogleSignIn.h>

typedef void(^SignInCompletionBlock)(NSDictionary* userInfo, NSError* error);

@interface GoogleSignInHelperManager : NSObject <GIDSignInDelegate, GIDSignInUIDelegate>
+(instancetype)sharedManager;
-(void)signIn:(SignInCompletionBlock)completion;
-(void)signOut;
@end
