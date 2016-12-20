
#import "GoogleSignInHelperManager.h"
#import "WixLoginNativeManager.h"

@interface GoogleSignInHelperManager() <GIDSignInDelegate, GIDSignInUIDelegate>
@property (copy, nonatomic) SignInCompletionBlock signInCompletionBlock;
@end

@implementation GoogleSignInHelperManager

+(instancetype)sharedManager
{
  static GoogleSignInHelperManager *instance = nil;
  static dispatch_once_t googleSignInHelperManagerOnceToken = 0;
  
  dispatch_once(&googleSignInHelperManagerOnceToken,^
  {
    if (instance == nil)
    {
      instance = [[GoogleSignInHelperManager alloc] init];
    }
  });
  
  return instance;
}

-(instancetype)init
{
  self = [super init];
  if (self)
  {
    [self setCliendId];
    
    GIDSignIn *signIn = [GIDSignIn sharedInstance];
    signIn.shouldFetchBasicProfile = YES;
    signIn.delegate = self;
    signIn.uiDelegate = self;
    
    [WixLoginNativeManager registerForGoogleClientIdChangedNotif:self action:@selector(onGoogleClientIdChangedNotif)];
  }
  return self;
}

-(void)setCliendId
{
  [GIDSignIn sharedInstance].clientID = [WixLoginNativeManager googleClientId];
}

-(void)onGoogleClientIdChangedNotif
{
  [self setCliendId];
}

- (void)signIn:(SignInCompletionBlock)completion
{
  if (self.signInCompletionBlock != nil)
  {
    return;
  }
  self.signInCompletionBlock = completion;
  
  [[GIDSignIn sharedInstance] signIn];
}

- (void)signOut
{
  [[GIDSignIn sharedInstance] signOut];
}

- (void)disconnect
{
  [[GIDSignIn sharedInstance] disconnect];
}

#pragma mark - GIDSignInDelegate

- (void)signIn:(GIDSignIn *)signIn didSignInForUser:(GIDGoogleUser *)user withError:(NSError *)error
{
  if (error != nil)
  {
    if (self.signInCompletionBlock != nil)
    {
      self.signInCompletionBlock(nil, error);
    }
    self.signInCompletionBlock = nil;
    return;
  }
  
  NSDictionary *userInfo = @{@"name": [WixLoginNativeManager safeString:user.profile.name],
                             @"email": [WixLoginNativeManager safeString:user.profile.email],
                             @"accessToken": [WixLoginNativeManager safeString:user.authentication.accessToken],
                             @"idToken": [WixLoginNativeManager safeString:user.authentication.idToken],
                             @"serverAuthCode": [WixLoginNativeManager safeString:user.serverAuthCode],
                             @"refreshToken": [WixLoginNativeManager safeString:user.authentication.refreshToken],
                             @"accessTokenExpirationDate": [NSNumber numberWithDouble:user.authentication.accessTokenExpirationDate.timeIntervalSinceNow]};
  
  if (self.signInCompletionBlock != nil)
  {
    self.signInCompletionBlock(userInfo, nil);
  }
  self.signInCompletionBlock = nil;
}

- (void)signIn:(GIDSignIn *)signIn didDisconnectWithUser:(GIDGoogleUser *)user withError:(NSError *)error
{
  if (error)
  {
  }
}

- (void)signIn:(GIDSignIn *)signIn presentViewController:(UIViewController *)viewController
{
  [[WixLoginNativeManager getRootViewController] presentViewController:viewController animated:YES completion:nil];
}

- (void)signIn:(GIDSignIn *)signIn dismissViewController:(UIViewController *)viewController
{
  [[WixLoginNativeManager getRootViewController] dismissViewControllerAnimated:YES completion:nil];
}

@end
