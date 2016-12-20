

#import <UIKit/UIKit.h>

@interface VideoView : UIView
@property (nonatomic, strong) NSDictionary *source;
@property (nonatomic, strong) UIImage *loadingImage;
@property (nonatomic) BOOL loopVideo;
@end
