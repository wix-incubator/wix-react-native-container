
 package com.wix.videoview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import java.net.URL;
import java.util.Map;

import javax.annotation.Nullable;

public class ReactVideoView extends ViewGroup {

    private static final String LOG_TAG = ReactVideoView.class.getSimpleName();

    private VideoView mVideoView;
    private ImageView mLoadingImageView;
    private boolean mLoopVideo;

    public ReactVideoView(Context ctx) {
        super(ctx);
        mLoopVideo = false;
        initLoadingImageView(ctx);
        initVideoView(ctx);
    }

    private void initLoadingImageView(Context ctx) {
        mLoadingImageView = new ImageView(ctx);
        mLoadingImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        this.addView(mLoadingImageView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mLoadingImageView.setVisibility(GONE);
    }

    private void initVideoView(Context context) {
        mVideoView = new VideoView(context);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(mLoopVideo);

                if (!mVideoView.isPlaying()) {
                    mVideoView.start();
                }

                //wait until there is progress on the video and then hide the loading image
                Thread thread = new Thread(new Runnable(){
                    public void run() {
                        try {
                            while (mVideoView.getCurrentPosition() <= 0) {
                                Thread.sleep(5);
                            }

                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mLoadingImageView.setVisibility(GONE);
                                }
                            }, 50);
                        }
                        catch (Exception e){
                            Log.e(LOG_TAG, "error hiding the loading image", e);
                        }
                    }
                });
                thread.start();
            }
        });
        this.addView(mVideoView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        super.addView(child, index, params);
        if (child != null && child != mVideoView && child != mLoadingImageView) {
            this.bringChildToFront(child);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mVideoView.layout(l, t, r, b);
        mLoadingImageView.layout(l, t, r, b);
    }

    private String getResourcePath(String uriString) {
        try {
            Context context = this.getContext();

            int lastIndexOfDot = uriString.lastIndexOf(".");
            if (lastIndexOfDot != -1) {
                uriString = uriString.substring(0, lastIndexOfDot);
            }

            Resources resources = context.getPackageManager().getResourcesForApplication(context.getPackageName());
            int resourceId = resources.getIdentifier(uriString, "raw", context.getPackageName());

            return "android.resource://" + context.getPackageName() + "/" + resourceId;
        } catch (Exception e) {
            Log.e(LOG_TAG, "error getting path for resource", e);
        }
        return null;
    }

    private Drawable getResourceDrawable(Context context, @Nullable String name) {
        int resId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        return resId > 0 ? context.getResources().getDrawable(resId) : null;
    }

    public void setLoopVideo(boolean loopVideo) {
        mLoopVideo = loopVideo;
    }

    public void setSource(Map<String, Object> source) {
        String uriString = (String)source.get("uri");
        if(uriString != null) {
            Uri videoUri = null;
            boolean isRemote = false;
            if (source.containsKey("isRemote")) {
                isRemote = (boolean) source.get("isRemote");
            }

            if (isRemote) {
                videoUri = Uri.parse(uriString);
            } else {
                String path = getResourcePath(uriString);
                if (path != null) {
                    videoUri = Uri.parse(path);
                }
            }

            if (videoUri != null) {
                mVideoView.setVideoURI(videoUri);
            }
        }
    }

    public void setLoadingImage(Map<String, Object> loadingImage) {
        if(loadingImage != null && loadingImage.containsKey("uri")) {
            String imageUriString = (String)loadingImage.get("uri");
            Uri imageUri = Uri.parse(imageUriString);
            if(imageUri != null) {
                try {
                    //In release builds imageUri would be a path in assets, In debug it's a http url and the image needs to be decoded from input stream
                    String scheme = imageUri.getScheme();
                    if (scheme != null && scheme.equals("http")) {
                        final URL imageURL = new URL(imageUriString);
                        Thread thread = new Thread(new Runnable(){
                            public void run() {
                            try {
                                final Drawable imageDrawable = Drawable.createFromStream(imageURL.openStream(), "src");
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mLoadingImageView.setImageDrawable(imageDrawable);
                                        if(!mVideoView.isPlaying()) {
                                            mLoadingImageView.setVisibility(VISIBLE);
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                Log.e(LOG_TAG, "error creating loading image", e);
                            }
                            }
                        });
                        thread.start();
                    }
                    else {
                        Drawable imageDrawable = getResourceDrawable(getContext(), imageUriString);
                        mLoadingImageView.setImageDrawable(imageDrawable);
                        mLoadingImageView.setVisibility(VISIBLE);
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, "error creating loading image", e);
                }
            }
        }
    }
}
