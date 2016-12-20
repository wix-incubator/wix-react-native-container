

#import "VideoView.h"
#import "UIView+React.h"
#import "RCTBridge.h"
#import "RCTConvert.h"

@import AVKit;
@import AVFoundation;

@interface VideoView ()
@property (nonatomic, strong) AVPlayerViewController *avPlayerViewController;
@property (nonatomic, strong) UIImageView *loadingImageView;
@property (nonatomic) BOOL removePlayerStatusObserverOnCleanup;
@property (nonatomic, strong) NSTimer *displayReadyTimer;
@end

@implementation VideoView

-(instancetype)initWithFrame:(CGRect)frame
{
  self = [super initWithFrame:frame];
  if (self)
  {
    self.loopVideo = NO;
    self.removePlayerStatusObserverOnCleanup = NO;
    self.displayReadyTimer = nil;

    self.avPlayerViewController = [[AVPlayerViewController alloc] init];
    self.avPlayerViewController.view.alpha = 0;
    self.avPlayerViewController.player = [[AVPlayer alloc] init];
    self.avPlayerViewController.showsPlaybackControls = NO;
    self.avPlayerViewController.videoGravity = AVLayerVideoGravityResizeAspectFill;
    self.avPlayerViewController.view.userInteractionEnabled = NO;
    [self addSubview:self.avPlayerViewController.view];

    self.loadingImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, frame.size.width, frame.size.height)];
    self.loadingImageView.hidden = YES;
    self.loadingImageView.contentMode = UIViewContentModeScaleAspectFill;
    [self addSubview:self.loadingImageView];

    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(playerItemDidPlayToEnd:)
                                                 name:AVPlayerItemDidPlayToEndTimeNotification
                                               object:nil];

    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onRNReload)
                                                 name:RCTReloadNotification object:nil];
  }
  return self;
}

-(void)cleanup
{
  [[NSNotificationCenter defaultCenter] removeObserver:self];
  [self removePlayerCurrentItemObservers];

  if (self.displayReadyTimer != nil)
  {
    [self.displayReadyTimer invalidate];
    self.displayReadyTimer = nil;
  }
}

-(void)dealloc
{
  [self cleanup];
}

-(void)onRNReload
{
  [self cleanup];
}

-(void)removePlayerCurrentItemObservers
{
  if (self.removePlayerStatusObserverOnCleanup && self.avPlayerViewController.player.currentItem != nil)
  {
    [self.avPlayerViewController.player.currentItem removeObserver:self forKeyPath:@"status"];
    self.removePlayerStatusObserverOnCleanup = NO;
  }
}

- (void)layoutSubviews
{
  [super layoutSubviews];

  self.avPlayerViewController.view.frame = CGRectMake(0, 0, self.frame.size.width, self.frame.size.height);
  [self sendSubviewToBack:self.avPlayerViewController.view];

  self.loadingImageView.frame = CGRectMake(0, 0, self.frame.size.width, self.frame.size.height);
  [self insertSubview:self.loadingImageView aboveSubview:self.avPlayerViewController.view];
}

-(void)handleReadyForDisplay
{
  if (self.displayReadyTimer != nil)
  {
    [self.displayReadyTimer invalidate];
    self.displayReadyTimer = nil;
  }

  if ([self.avPlayerViewController isReadyForDisplay])
  {
    self.avPlayerViewController.view.alpha = 1;
    self.loadingImageView.alpha = 0;
  }
  else
  {
    self.displayReadyTimer = [NSTimer scheduledTimerWithTimeInterval:0.1 target:self selector:@selector(handleReadyForDisplay) userInfo:nil repeats:NO];
  }
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context
{
  if (object == self.avPlayerViewController.player.currentItem && [keyPath isEqualToString:@"status"])
  {
    if (self.avPlayerViewController.player.currentItem.status == AVPlayerStatusReadyToPlay)
    {
      [self.avPlayerViewController.player play];
      [self handleReadyForDisplay];
    }
    else if (self.avPlayerViewController.player.status == AVPlayerStatusFailed)
    {
      NSLog(@"video player error: %@", self.avPlayerViewController.player.error);
    }
  }
}

-(void)playerItemDidPlayToEnd:(NSNotification*)notif
{
  if (self.loopVideo)
  {
    [self.avPlayerViewController.player.currentItem seekToTime:kCMTimeZero completionHandler:^(BOOL finished)
     {
       if (finished)
       {
         [self.avPlayerViewController.player play];
       }
     }];
  }
}

#pragma mark - view props

-(void)setSource:(NSDictionary*)videoSource
{
  [self.avPlayerViewController.player pause];
  [self removePlayerCurrentItemObservers];

  if (videoSource == nil)
  {
    return;
  }

  NSURL *URL = nil;

  NSString *uri = videoSource[@"uri"];
  BOOL isRemote = [RCTConvert BOOL:videoSource[@"isRemote"]];
  if (uri != nil)
  {
    if (isRemote)
    {
       URL = [NSURL URLWithString:uri];
    }
    else
    {
      NSString *type = videoSource[@"type"];
      NSString *path = [[NSBundle mainBundle] pathForResource:uri ofType:type];
      if (path != nil)
      {
        URL = [[NSURL alloc] initFileURLWithPath:path];
      }
    }
  }

  if (URL != nil)
  {
    AVPlayerItem *playerItem = [AVPlayerItem playerItemWithURL:URL];
    [self.avPlayerViewController.player replaceCurrentItemWithPlayerItem:playerItem];

    [playerItem addObserver:self forKeyPath:@"status" options:0 context:nil];
    self.removePlayerStatusObserverOnCleanup = YES;
  }
}

-(void)setLoadingImage:(UIImage *)loadingImage
{
  self.loadingImageView.image = loadingImage;
  self.loadingImageView.hidden = (loadingImage == nil);
}

@end
