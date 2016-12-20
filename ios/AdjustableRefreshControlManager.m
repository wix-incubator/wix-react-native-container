
#import "AdjustableRefreshControlManager.h"

#import "RCTRefreshControl.h"

@interface AdjustableRefreshControl : RCTRefreshControl
@property int adjustX;
@property int adjustY;
@end

@implementation AdjustableRefreshControl {
  BOOL adjusted;
}

-(void)layoutSubviews {
  [super layoutSubviews];
  if(!adjusted){
    adjusted = true;
    self.bounds = CGRectOffset(self.bounds, _adjustX, _adjustY);
    [self endRefreshing];
  }
}

@end


@implementation AdjustableRefreshControlManager

RCT_EXPORT_MODULE()

- (UIView *)view {
  return [AdjustableRefreshControl new];
}

RCT_EXPORT_VIEW_PROPERTY(onRefresh, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(refreshing, BOOL)
RCT_EXPORT_VIEW_PROPERTY(tintColor, UIColor)
RCT_EXPORT_VIEW_PROPERTY(title, NSString)

RCT_EXPORT_VIEW_PROPERTY(adjustX, int);
RCT_EXPORT_VIEW_PROPERTY(adjustY, int);

@end
