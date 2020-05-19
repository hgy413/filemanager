package com.jiubang.commerce.ad.window;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.jiubang.commerce.ad.ResourcesProvider;
import com.jiubang.commerce.utils.DrawUtils;

public class GuideDownloadWindowManager {
    private static GuideDownloadWindowManager sInstance;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public GuideDownloadView mGDView;
    /* access modifiers changed from: private */
    public WindowManager.LayoutParams mGDWindowLayoutParams;
    /* access modifiers changed from: private */
    public WindowManager mGDWindowManager;
    private Handler mHandler = new Handler();
    private boolean mIsRemove;
    private Runnable mRunnable = new Runnable() {
        public void run() {
            GuideDownloadWindowManager.this.mGDView.addAnimation();
        }
    };

    private GuideDownloadWindowManager(Context context) {
        this.mContext = context != null ? context.getApplicationContext() : null;
        this.mIsRemove = true;
    }

    public static GuideDownloadWindowManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new GuideDownloadWindowManager(context);
        }
        return sInstance;
    }

    public void createGuideDownloadWindow() {
        initGuideDownloadView(this.mContext);
        initWindowManager(this.mContext);
        initWindowLayoutParams(this.mContext);
        addView();
    }

    private void initGuideDownloadView(Context context) {
        if (this.mGDView == null) {
            this.mGDView = new GuideDownloadView(context);
        }
    }

    private void initWindowManager(Context context) {
        if (this.mGDWindowManager == null) {
            this.mGDWindowManager = (WindowManager) context.getSystemService("window");
        }
    }

    private void initWindowLayoutParams(Context context) {
        if (this.mGDWindowLayoutParams == null) {
            this.mGDWindowLayoutParams = new WindowManager.LayoutParams();
            this.mGDWindowManager.getDefaultDisplay().getMetrics(new DisplayMetrics());
            this.mGDWindowLayoutParams.type = 2003;
            this.mGDWindowLayoutParams.format = 1;
            this.mGDWindowLayoutParams.flags = 262152;
            this.mGDWindowLayoutParams.gravity = 51;
            this.mGDWindowLayoutParams.width = this.mGDView.mWindowViewWidth;
            this.mGDWindowLayoutParams.height = this.mGDView.mWindowViewHeight;
            this.mGDWindowLayoutParams.x = ResourcesProvider.getInstance(this.mContext).getDimensionPixelSize("ad_gp_install_btn_margin_left_edge");
            this.mGDWindowLayoutParams.y = ResourcesProvider.getInstance(this.mContext).getDimensionPixelSize("ad_gp_install_btn_margin_top_include_btn_height");
        }
    }

    private void addView() {
        this.mHandler.postDelayed(this.mRunnable, 3000);
        if (this.mIsRemove) {
            this.mGDWindowManager.addView(this.mGDView, this.mGDWindowLayoutParams);
            this.mIsRemove = false;
        }
    }

    public void hideGuideDownloadWindow() {
        if (this.mGDWindowManager != null && !this.mIsRemove) {
            this.mGDView.mImageBig.setAlpha(0);
            this.mGDView.mWindowImage.setAlpha(0);
            this.mHandler.removeCallbacks(this.mRunnable);
            this.mGDWindowManager.removeView(this.mGDView);
            this.mGDView = null;
            this.mIsRemove = true;
        }
    }

    class GuideDownloadView extends RelativeLayout {
        public ImageView mImageBig;
        public ImageView mWindowImage;
        public int mWindowViewHeight;
        public int mWindowViewWidth;

        public GuideDownloadView(Context context) {
            super(context);
            LayoutInflater.from(context).inflate(ResourcesProvider.getInstance(GuideDownloadWindowManager.this.mContext).getLayoutId("ad_google_guide_download_layout"), this);
            this.mImageBig = (ImageView) findViewById(ResourcesProvider.getInstance(GuideDownloadWindowManager.this.mContext).getId("float_window_image_big"));
            this.mWindowImage = (ImageView) findViewById(ResourcesProvider.getInstance(GuideDownloadWindowManager.this.mContext).getId("float_window_view"));
            this.mWindowViewWidth = this.mWindowImage.getLayoutParams().width;
            this.mWindowViewHeight = this.mWindowImage.getLayoutParams().height;
            this.mImageBig.setAlpha(0);
            this.mWindowImage.setAlpha(0);
        }

        public GuideDownloadView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public void addAnimation() {
            TranslateAnimation animation = new TranslateAnimation(0.0f, 24.0f, 0.0f, 0.0f);
            animation.setStartOffset(250);
            animation.setDuration(130);
            animation.setRepeatCount(1);
            animation.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    GuideDownloadView.this.mImageBig.setAlpha(255);
                    GuideDownloadView.this.mWindowImage.setAlpha(255);
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(final Animation animation) {
                    GuideDownloadView.this.mWindowImage.postDelayed(new Runnable() {
                        public void run() {
                            GuideDownloadView.this.mWindowImage.startAnimation(animation);
                        }
                    }, 2000);
                }
            });
            this.mWindowImage.startAnimation(animation);
        }

        public boolean onTouchEvent(MotionEvent event) {
            boolean ret = super.onTouchEvent(event);
            GuideDownloadWindowManager.this.hideGuideDownloadWindow();
            return ret;
        }

        /* access modifiers changed from: protected */
        public void onConfigurationChanged(Configuration newConfig) {
            int width;
            int height;
            int oriention = newConfig.orientation;
            int left = ResourcesProvider.getInstance(GuideDownloadWindowManager.this.mContext).getDimensionPixelSize("ad_gp_install_btn_margin_left_edge");
            if (2 == oriention) {
                int num0 = DrawUtils.getScreenWidth(GuideDownloadWindowManager.this.mContext);
                int num1 = DrawUtils.getScreenHeight(GuideDownloadWindowManager.this.mContext);
                if (num0 < num1) {
                    width = num0;
                } else {
                    width = num1;
                }
                if (num0 > num1) {
                    height = num0;
                } else {
                    height = num1;
                }
                left = height - (width - left);
            }
            if (!(GuideDownloadWindowManager.this.mGDWindowLayoutParams == null || GuideDownloadWindowManager.this.mGDWindowManager == null)) {
                GuideDownloadWindowManager.this.mGDWindowLayoutParams.x = left;
                GuideDownloadWindowManager.this.mGDWindowManager.updateViewLayout(GuideDownloadWindowManager.this.mGDView, GuideDownloadWindowManager.this.mGDWindowLayoutParams);
            }
            if (Build.VERSION.SDK_INT >= 9) {
                super.onConfigurationChanged(newConfig);
            }
        }
    }
}
