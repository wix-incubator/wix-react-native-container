

#import "VideoViewManager.h"
#import "VideoView.h"

@implementation VideoViewManager

RCT_EXPORT_MODULE()

- (UIView *)view
{
  return [[VideoView alloc] initWithFrame:CGRectZero];
}

RCT_REMAP_VIEW_PROPERTY(loopVideo, loopVideo, BOOL)
RCT_REMAP_VIEW_PROPERTY(source, source, NSDictionary)
RCT_REMAP_VIEW_PROPERTY(loadingImage, loadingImage, UIImage)

@end
