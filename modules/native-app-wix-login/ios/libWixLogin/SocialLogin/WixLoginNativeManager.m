
#import "WixLoginNativeManager.h"
#import <FBSDKCoreKit/FBSDKCoreKit.h>
#import <FBSDKLoginKit/FBSDKLoginKit.h>
#import <FBSDKCoreKit/FBSDKCoreKit.h>
#import "GoogleSignInHelperManager.h"

#define kLoginPayloadPersistencyKey @"LoginPayload"

NSString* const WixLoginNativeManagerGoogleClientIdChangedNotification = @"WixLoginNativeManagerGoogleClientIdChangedNotification";

@implementation WixLoginNativeManager

RCT_EXPORT_MODULE();

- (dispatch_queue_t)methodQueue
{
  return dispatch_get_main_queue();
}

+(void)handleRCTPromiseRejectBlock:(RCTPromiseRejectBlock)reject error:(NSError*)error
{
  NSString *errorDescription = error.localizedDescription;
  
  NSDictionary *fbParsedError = error.userInfo[@"com.facebook.sdk:FBSDKGraphRequestErrorParsedJSONResponseKey"];
  if (fbParsedError != nil && [fbParsedError isKindOfClass:[NSDictionary class]])
  {
    NSString *message = fbParsedError[@"body"][@"error"][@"message"];
    if (message != nil)
    {
      errorDescription = [errorDescription stringByAppendingFormat:@"\n%@", message];
    }
  }
  reject([NSString stringWithFormat: @"%lu", (long)error.code], errorDescription, error);
}

+(void)handleFBAccessTokenReceived:(FBSDKAccessToken*)token isCanceled:(BOOL)isCanceled resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject
{
  NSMutableDictionary *resultUserInfo = [@{@"token": [WixLoginNativeManager safeString:token.tokenString],
                                           @"userId": [WixLoginNativeManager safeString:token.userID],
                                           @"isCanceled": @(isCanceled)} mutableCopy];
  
  if (isCanceled)
  {
    resolve(resultUserInfo);
    return;
  }
  
  FBSDKGraphRequest *fbGraphRequest = [[FBSDKGraphRequest alloc] initWithGraphPath:@"me" parameters:@{@"fields": @"email"}];
  [fbGraphRequest startWithCompletionHandler:^(FBSDKGraphRequestConnection *connection, id result, NSError *error)
   {
     if (error != nil)
     {
       [WixLoginNativeManager handleRCTPromiseRejectBlock:reject error:error];
     }
     else
     {
       NSString *userEmail = @"";
       if (!error && [result isKindOfClass:[NSDictionary class]])
       {
         userEmail = result[@"email"];
       }
       
       resultUserInfo[@"email"] = userEmail;
       
       resolve(resultUserInfo);
     }
   }];
}

RCT_REMAP_METHOD(loginWithFacebook,
                 fbLoginResolver:(RCTPromiseResolveBlock)resolve
                 fbLoginRejecter:(RCTPromiseRejectBlock)reject)
{
  FBSDKAccessToken *currentAccessToken = [FBSDKAccessToken currentAccessToken];
  if (currentAccessToken != nil)
  {
    [WixLoginNativeManager handleFBAccessTokenReceived:currentAccessToken isCanceled:NO resolve:resolve reject:reject];
    return;
  }
  
  FBSDKLoginManager *login = [[FBSDKLoginManager alloc] init];
  [login logInWithReadPermissions: @[@"email", @"public_profile"] fromViewController:[WixLoginNativeManager getRootViewController] handler:^(FBSDKLoginManagerLoginResult *result, NSError *error)
  {
      if (error != nil)
      {
        [WixLoginNativeManager handleRCTPromiseRejectBlock:reject error:error];
        return;
      }
    
      [WixLoginNativeManager handleFBAccessTokenReceived:result.token isCanceled:result.isCancelled resolve:resolve reject:reject];
  }];
}

RCT_REMAP_METHOD(loginWithGoogle,
                 googleLoginResolver:(RCTPromiseResolveBlock)resolve
                 googleLoginRejecter:(RCTPromiseRejectBlock)reject)
{
  [[GoogleSignInHelperManager sharedManager] signIn:^(NSDictionary* userInfo, NSError* error)
   {
     BOOL userCanceld = error != nil && [error.domain isEqualToString:kGIDSignInErrorDomain] && error.code == kGIDSignInErrorCodeCanceled;
     if(userCanceld)
     {
       if(userInfo == nil)
       {
         userInfo = @{@"isCanceled": @(YES)};
       }
       else
       {
         NSMutableDictionary *mutableUserInfo = [userInfo mutableCopy];
         mutableUserInfo[@"isCanceled"] = @(YES);
         userInfo = mutableUserInfo;
       }
     }
     
     if (userInfo != nil)
     {
       resolve(userInfo);
     }
     else
     {
       [WixLoginNativeManager handleRCTPromiseRejectBlock:reject error:error];
     }
   }];
}

RCT_EXPORT_METHOD(logoutFromGoogle)
{
  [[GoogleSignInHelperManager sharedManager] signOut];
}

RCT_EXPORT_METHOD(logoutFromFacebook)
{
  FBSDKLoginManager *loginManager = [[FBSDKLoginManager alloc] init];
  [loginManager logOut];
}

RCT_EXPORT_METHOD(setGoogleClientId:(NSString*)googleClientId)
{
  [WixLoginNativeManager setGoogleClientId:googleClientId];
}

RCT_EXPORT_METHOD(deleteCookies:(NSArray*)cookiesToDelete domain:(NSString*)domain)
{
  NSURL *URL = [NSURL URLWithString:domain];
  if (URL != nil)
  {
    NSArray *cookies = [[NSHTTPCookieStorage sharedHTTPCookieStorage] cookiesForURL:URL];
    for (NSHTTPCookie *cookie in cookies)
    {
      if ([cookiesToDelete containsObject:cookie.name])
      {
        [[NSHTTPCookieStorage sharedHTTPCookieStorage] deleteCookie:cookie];
      }
    }
  }
}

#pragma mark - helper methods

+(UIViewController*)getRootViewController
{
  UIViewController *rootViewController = [[UIApplication sharedApplication].delegate.window rootViewController];
  return rootViewController;
}

+(NSString*)safeString:(NSString*)string
{
  return (string != nil) ? string : @"";
}

+(NSDictionary*)sanitizedPayload:(NSDictionary*)payload
{
  NSMutableDictionary *sanitizedPayload = [NSMutableDictionary dictionaryWithDictionary:payload];
  
  for (NSString *key in [payload allKeys])
  {
    id obj = payload[key];
    if ([obj isKindOfClass:[NSNull class]])
    {
      sanitizedPayload[key] = @"";
    }
  }
  
  return sanitizedPayload;
}

#pragma mark - GoogleClientId

static NSString *gGoogleClientId = nil;

+(void)setGoogleClientId:(NSString*)googleClientId
{
  gGoogleClientId = googleClientId;
  [[NSNotificationCenter defaultCenter] postNotificationName:WixLoginNativeManagerGoogleClientIdChangedNotification object:nil userInfo:nil];
}

+(NSString*)googleClientId
{
  return gGoogleClientId;
}

+(void)registerForGoogleClientIdChangedNotif:(id)receiver action:(SEL)action
{
    [[NSNotificationCenter defaultCenter] addObserver:receiver selector:action name:WixLoginNativeManagerGoogleClientIdChangedNotification object:nil];
}

+(void)unregisterForGoogleClientIdChangedNotif:(id)receiver
{
   [[NSNotificationCenter defaultCenter] removeObserver:receiver name:WixLoginNativeManagerGoogleClientIdChangedNotification object:nil];
}

@end
