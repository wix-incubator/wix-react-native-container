
#import "RCTScrollView+Extras.h"
#import "RCTUIManager.h"
#import "RCTScrollView.h"
#import <objc/runtime.h>


const char kDeallocWatcherKey;

@interface DeallocWatcher : NSObject
@property(nonatomic, weak) NSObject *refObj;
@property(nonatomic, weak) NSObject *observerRef;
@end

@implementation DeallocWatcher

- (void)dealloc
{
  if ([self.refObj isKindOfClass:[UIScrollView class]])
  {
    UIScrollView *scrollView = (UIScrollView*)self.refObj;
    [scrollView removeObserver:self.observerRef forKeyPath:@"contentSize"];
  }
}

@end


static __weak id currentFirstResponder;

@implementation UIResponder (FirstResponder)

+(id)currentFirstResponder {
  currentFirstResponder = nil;
  [[UIApplication sharedApplication] sendAction:@selector(findFirstResponder:) to:nil from:nil forEvent:nil];
  return currentFirstResponder;
}

-(void)findFirstResponder:(id)sender {
  currentFirstResponder = self;
}

@end


@implementation RCTScrollViewManager (Extras)

RCT_EXPORT_METHOD(activateScrollToCursorOnSizeChange:(nonnull NSNumber *)reactTag)
{
  [self.bridge.uiManager addUIBlock:
   ^(__unused RCTUIManager *uiManager, NSDictionary<NSNumber *, RCTScrollView *> *viewRegistry){
     RCTScrollView *view = viewRegistry[reactTag];
     if (view != nil && [view isKindOfClass:[RCTScrollView class]])
     {
       static dispatch_once_t onceToken;
       dispatch_once(&onceToken, ^
       {
         [view.scrollView addObserver:self forKeyPath:@"contentSize" options:NSKeyValueObservingOptionNew | NSKeyValueObservingOptionOld context:NULL];

                  DeallocWatcher *deallocWatcher = [DeallocWatcher new];
         deallocWatcher.refObj = view.scrollView;
         deallocWatcher.observerRef = self;
         objc_setAssociatedObject(view, &kDeallocWatcherKey, deallocWatcher, OBJC_ASSOCIATION_RETAIN);
       });
     }
   }];
}

- (void) observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context
{
  if ([keyPath isEqualToString:@"contentSize"] && [object isKindOfClass:[UIScrollView class]])
  {
    UIScrollView *scrollView = (UIScrollView*)object;
    id firstResponder = [UIResponder currentFirstResponder];
    if ([firstResponder isKindOfClass:[UITextView class]] && [firstResponder isDescendantOfView:scrollView])
    {
      UITextView *textView = (UITextView*)firstResponder;
      CGRect caretRect = [textView caretRectForPosition:textView.selectedTextRange.start];
      caretRect = [scrollView convertRect:caretRect fromView:textView];
      if (caretRect.origin.y + caretRect.size.height > scrollView.frame.size.height + scrollView.contentOffset.y - scrollView.contentInset.top  )
      {
        [scrollView setContentOffset:CGPointMake(scrollView.contentOffset.x, caretRect.origin.y - scrollView.frame.size.height + caretRect.size.height + 2) animated:YES];
      }
      else if(caretRect.origin.y < scrollView.contentOffset.y)
      {
        [scrollView setContentOffset:CGPointMake(scrollView.contentOffset.x, caretRect.origin.y - 2) animated:YES];
      }
    }
  }
}

@end
