
#import "ParallaxImageViewManager.h"
#import "RCTScrollView.h"
#import "UIView+React.h"

#define kDefaultParallaxSpeed 0.45
#define kMaxParallaxMovePercentDefault 0.345

@interface ParallaxImageView : UIImageView <UIScrollViewDelegate>
@property (nonatomic, strong) UIVisualEffectView *blurView;
@property (nonatomic, strong) UIScrollView *scrollView;
@property (nonatomic, assign) float maxParallaxMove;
@property (nonatomic, assign) float maxParallaxMovePercent;
@property (nonatomic, assign) float parallaxSpeed;
@end

@implementation ParallaxImageView

-(instancetype)init
{
  self = [super init];
  if (self)
  {
    self.parallaxSpeed = kDefaultParallaxSpeed;
    self.maxParallaxMovePercent = kMaxParallaxMovePercentDefault;
  }
  return self;
}

-(void)dealloc
{
  [self.scrollView removeObserver:self forKeyPath:@"contentOffset" context:nil];
}

- (void)reactSetFrame:(CGRect)frame
{
  CGSize oldSize = self.frame.size;

    [super reactSetFrame:frame];

    if (!CGSizeEqualToSize(frame.size, oldSize))
  {
    self.maxParallaxMove = frame.size.height * self.maxParallaxMovePercent;

        if (self.blurView != nil)
    {
      self.blurView.frame = CGRectMake(0, 0, frame.size.width, frame.size.height);
    }
  }
}

- (void)didMoveToWindow
{
  [super didMoveToWindow];

    if (self.scrollView != nil)
  {
    return;
  }

    for (int i = 0; i < [self.superview.subviews count]; i++)
  {
    UIView *subView = self.superview.subviews[i];
    if ([subView isKindOfClass:[RCTScrollView class]])
    {
      self.scrollView = ((RCTScrollView*)subView).scrollView;
      [self.scrollView addObserver:self forKeyPath:@"contentOffset" options:NSKeyValueObservingOptionNew | NSKeyValueObservingOptionOld context:nil];
      break;
    }
  }
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context
{
  UIScrollView *scrollView = (UIScrollView*)object;
  float completion = MIN((float)scrollView.contentOffset.y / (float)self.maxParallaxMove, 1.0);
  float scale = 1.0;
  float additionalTranslation = 0;
  if (completion < 0)
  {
    scale = 1 - completion;
    additionalTranslation = (scale - 1) * self.bounds.size.height;
  }
  completion = MAX(completion, 0);

    CGFloat translation = -(completion * self.maxParallaxMove) + additionalTranslation;
  self.transform = CGAffineTransformScale(CGAffineTransformMakeTranslation(0, translation * self.parallaxSpeed), scale, scale);
  self.alpha = 1 - completion;
}

@end

@implementation ParallaxImageViewManager

RCT_EXPORT_MODULE()

- (UIView *)view
{
  ParallaxImageView *imageView = [[ParallaxImageView alloc] init];
  return imageView;
}

RCT_CUSTOM_VIEW_PROPERTY(source, UIImage, ParallaxImageView)
{
  view.image = [RCTConvert UIImage:json];
}

RCT_CUSTOM_VIEW_PROPERTY(maxParallxMovePercent, CGFloat, ParallaxImageView)
{
  view.maxParallaxMovePercent = [RCTConvert CGFloat:json];
}

RCT_CUSTOM_VIEW_PROPERTY(parallaxSpeed, CGFloat, ParallaxImageView)
{
  view.parallaxSpeed = [RCTConvert CGFloat:json];
}


@end
