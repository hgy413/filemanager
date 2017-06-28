package com.jb.filemanager.function.applock.view;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.receiver.HomeWatcherReceiver;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.DrawUtils;
import com.jb.filemanager.util.Logger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by nieyh on 2017/1/5.
 * 应用锁展示的view
 */

public class FloatAppLockerView extends FrameLayout implements AntiPeepCameraHolder.CameraEventListener {

    @IntDef({VIEW_OUTSIDE_APP, VIEW_INSIDE_APP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FloatViewMode {
    }

    //在外部触发展示时
    public static final int VIEW_OUTSIDE_APP = 1;

    //在应用内展示
    public static final int VIEW_INSIDE_APP = 2;
    //返回键
    private ImageView mBack;
    //模糊的背景
    private ImageView mBgBlur;
    //标题
    private TextView mTitle;
    //icon
    private ImageView mIcon;
    //应用名字
    private TextView mAppName;
    //忘记
    private TextView mForget;
    //图案锁
    private PatternView mPatternView;
    //数字锁
    private NumberLockerView mNumberLockerView;
    //遮罩视图
    private View mShadeView;
    private IFloatAppLockerViewEvtListener mIFloatAppLockerViewEvtListener;

    private HomeWatcherReceiver mHomeKeyEventReceiver;
    //相机是否打开
    private boolean mIsCameraOpening;
    //拍照视图
    private AntiPeepCameraHolder mCameraHolder;

    private int mMode = VIEW_OUTSIDE_APP;

    private Handler mHandler;

    private View mContentRoot;

    public FloatAppLockerView(Context context) {
        super(context);
        mContentRoot = LayoutInflater.from(context).inflate(R.layout.view_applock_float_layout, null);
        mBack = (ImageView) mContentRoot.findViewById(R.id.activity_title_icon);
        mTitle = (TextView) mContentRoot.findViewById(R.id.activity_title_word);
        mBgBlur = (ImageView) mContentRoot.findViewById(R.id.view_applock_float_layout_blur_bg);
        mIcon = (ImageView) mContentRoot.findViewById(R.id.view_applock_float_layout_icon);
        mAppName = (TextView) mContentRoot.findViewById(R.id.view_applock_float_layout_appname);
        mPatternView = (PatternView) mContentRoot.findViewById(R.id.view_applock_float_layout_patternview);
        mNumberLockerView = (NumberLockerView) mContentRoot.findViewById(R.id.view_applock_float_layout_numberview);
        mForget = (TextView) mContentRoot.findViewById(R.id.view_applock_float_layout_forget);
        mShadeView = mContentRoot.findViewById(R.id.view_applock_float_layout_shade);
        initListener();
        addView(mContentRoot);
        mHomeKeyEventReceiver = new HomeWatcherReceiver();
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIFloatAppLockerViewEvtListener != null) {
                    mIFloatAppLockerViewEvtListener.onBackPress();
                }
            }
        });
        mTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIFloatAppLockerViewEvtListener != null) {
                    mIFloatAppLockerViewEvtListener.onBackPress();
                }
            }
        });
        mForget.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIFloatAppLockerViewEvtListener != null) {
                    mIFloatAppLockerViewEvtListener.onForgetClick(v);
                }
            }
        });

        mPatternView.setOnPatternListener(new PatternView.OnPatternListener() {
            @Override
            public void onPatternStart() {
                removeClearErrorWork();
            }

            @Override
            public void onPatternCleared() {

            }

            @Override
            public void onPatternCellAdded(List<PatternView.Cell> pattern) {

            }

            @Override
            public void onPatternDetected(List<PatternView.Cell> pattern) {
                if (mIFloatAppLockerViewEvtListener != null) {
                    mIFloatAppLockerViewEvtListener.onInputCompleted(pattern, null);
                }
            }
        });

        mNumberLockerView.setOnNumberListener(new NumberLockerView.OnNumberListener() {
            @Override
            public void onNumberFinish(String[] numbers) {
                if (mIFloatAppLockerViewEvtListener != null) {
                    mIFloatAppLockerViewEvtListener.onInputCompleted(null, numbers);
                }
            }

            @Override
            public void onNumberInput(String number) {

            }

            @Override
            public void onNumberAllDeleted() {

            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        try {
            super.onAttachedToWindow();
        } catch (Exception e) {
            //此处情况可能为相机SurefaceView已经释放并为空，却又触发了onAttachedToWindow, 父类在操作中抛出空指针异常。
            //所以针对 此种空指针异常不处理
            if (e == null || !(e instanceof NullPointerException)) {
                throw e;
            }
        }
        mHandler = new Handler();
        mHomeKeyEventReceiver.setTouchSystemKeyListener(new HomeWatcherReceiver.TouchSystemKeyListener() {
            @Override
            public void onTouchHome() {
                if (mIFloatAppLockerViewEvtListener != null) {
                    mIFloatAppLockerViewEvtListener.onHomeClick();
                }
            }
        });
        if (!mHomeKeyEventReceiver.isRegistered()) {
            mHomeKeyEventReceiver.register(getContext(), new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mHomeKeyEventReceiver.isRegistered()) {
            mHomeKeyEventReceiver.unregister(getContext());
        }
        closeCamera();
    }

    /**
     * 打开相机
     */
    public void openCamera(String pkgName) {
        if (mCameraHolder == null) {
            // 添加相机视图
            mCameraHolder = new AntiPeepCameraHolder(this.getContext());
            mCameraHolder.setOnCameraEventListener(this);
            View contentView = mCameraHolder.getContentView();
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(2, 2);
            contentView.setLayoutParams(layoutParams);
            //此处必须addView 不然相机View将无法执行初始化
            addView(contentView, 1);
        }
        if (mCameraHolder != null) {
            // 打开相机
            mCameraHolder.setPackageName(pkgName);
            if (!mIsCameraOpening) {
                mIsCameraOpening = true;
                mCameraHolder.open();
            }
        }
    }

    /**
     * 拍照
     */
    public void capturePeep() {
        if (mCameraHolder != null) {
            mCameraHolder.captureAuto();
        }
    }

    /**
     * 关闭相机
     */
    private void closeCamera() {
        if (mCameraHolder != null) {
            mIsCameraOpening = false;
            removeView(mCameraHolder.getContentView());
            mCameraHolder.setOnCameraEventListener(null);
            mCameraHolder.close();
            mCameraHolder = null;
        }
    }

    @Override
    public void onOpenFail() {
        closeCamera();
    }

    @Override
    public void onCapture() {

    }

    @Override
    public void onImageSaved() {
        closeCamera();
    }

    public void setIFloatAppLockerViewEvtListener(IFloatAppLockerViewEvtListener iFloatAppLockerViewEvtListener) {
        this.mIFloatAppLockerViewEvtListener = iFloatAppLockerViewEvtListener;
    }

    public interface IFloatAppLockerViewEvtListener {

        void onBackPress();

        void onForgetClick(View v);

        void onInputCompleted(List<PatternView.Cell> cellList, String[] numbers);

        void onHomeClick();

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (mIFloatAppLockerViewEvtListener != null) {
                mIFloatAppLockerViewEvtListener.onBackPress();
            }
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 重置图案
     */
    public void resetPatternView() {
        if (mPatternView != null) {
            mPatternView.clearPattern();
            mPatternView.setDisplayMode(PatternView.DisplayMode.Correct);
        }
        if (mNumberLockerView != null) {
            mNumberLockerView.clean();
        }
    }

    /**
     * 绑定数据
     *
     * @param pkgName
     * @param mode    <p/>
     *                <ol>
     *                <li>{@link #VIEW_INSIDE_APP} 在应用内展示</li>
     *                <li>{@link #VIEW_OUTSIDE_APP} 在应用外展示</li>
     *                <ol/>
     */
    public void bindViewData(String pkgName, @FloatViewMode int mode, boolean isPatternPsd) {
        if (isPatternPsd) {
            mNumberLockerView.setVisibility(GONE);
            mPatternView.setVisibility(VISIBLE);
        } else {
            mNumberLockerView.setVisibility(VISIBLE);
            mPatternView.setVisibility(GONE);
        }

        mMode = mode;
        //通过不同模式来判断展示情况
        //1、图标换色
        //2、文字换色
        //3、遮罩换色
        //4、图案密码换色
        //5、标题栏文字更换
        //6、中心文字更换
        switch (mode) {
            case VIEW_INSIDE_APP:
                mBack.setColorFilter(0xFF0d96fc, PorterDuff.Mode.SRC_ATOP);
                mShadeView.setAlpha(1);
                mShadeView.setBackgroundColor(Color.WHITE);
                mForget.setVisibility(VISIBLE);
                mForget.setTextColor(0xFF0d96fc);
                mForget.setText(R.string.applock_float_view_forget_psd);
                mAppName.setTextColor(0xFF0d96fc);
                mTitle.setVisibility(View.VISIBLE);
                mTitle.setTextColor(0xFF0d96fc);
                mTitle.setText(R.string.activity_applock_title);
                mBgBlur.setVisibility(View.GONE);
                if (isPatternPsd) {
                    mPatternView.setRegularColor(0xFF0d96fc);
                    mPatternView.setSuccessColor(0xFF0d96fc);
                } else {
                    mNumberLockerView.setColorStyle(NumberLockerView.DARK_STYLE);
                }
                break;
            case VIEW_OUTSIDE_APP:
                mBack.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                mShadeView.setAlpha(0.85f);
                mShadeView.setBackgroundColor(0xFF2c3d4a);
                mForget.setVisibility(GONE);
                mForget.setTextColor(Color.WHITE);
                mForget.setText(R.string.applock_float_view_forget_psd);
                mAppName.setTextColor(Color.WHITE);
                mTitle.setVisibility(View.GONE);
                mBgBlur.setVisibility(View.VISIBLE);
                if (isPatternPsd) {
                    mPatternView.setRegularColor(Color.WHITE);
                    mPatternView.setSuccessColor(Color.WHITE);
                } else {
                    mNumberLockerView.setColorStyle(NumberLockerView.LIGHT_STYLE);
                }
                break;
        }

        PackageInfo packageInfo = AppUtils.getAppPackageInfo(TheApplication.getAppContext(), pkgName);
        String appName = AppUtils.getAppName(TheApplication.getAppContext(), packageInfo);
        Drawable appIcon = AppUtils.getIconByPkgInfo(TheApplication.getAppContext(), packageInfo);
        if (!TextUtils.isEmpty(appName)) {
            mAppName.setText(appName);
        }
        if (appIcon != null) {
            mIcon.setImageDrawable(appIcon);
            //只有在外部显示的时候才设置模糊
            if (mode == VIEW_OUTSIDE_APP) {
                //模糊半径10dp
//                float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, TheApplication.getAppContext().getResources().getDisplayMetrics());
                Bitmap blur = null;
                try {
                    blur = DrawUtils.drawable2Bitmap(appIcon);
                    blur = toBlurBg(blur);
                } catch (OutOfMemoryError e) {
                    //内存溢出则隐藏
                    mBgBlur.setVisibility(View.GONE);
                    blur = null;
                }
                if (blur != null) {
                    //设置模糊图片
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mBgBlur.setBackground(new BitmapDrawable(getResources(), blur));
                    } else {
                        mBgBlur.setBackgroundDrawable(new BitmapDrawable(getResources(), blur));
                    }
                }
            }
        }
    }

    /**
     * 模糊应用锁背景 <br>
     * 记录：经过测试 不使用RenderScript 进行模糊处理，平均400毫秒
     *      使用RenderScript 平均200毫秒。(时间包括压缩并执行模糊)<br>
     * 比较优缺点：RenderScript 对于大图片进行模糊处理，性能远远优于直接Java层的图像处理 (<code> {@link DrawUtils#blur(Bitmap, int, boolean)}}</code>) <br/>
     * 但是 RenderScirpt <b>不支持低于API 17</b>.
     * @param bitmap 图片位图文件
     * @return 模糊后的整个背景 <br>
     *
     * */
    private Bitmap toBlurBg(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        Bitmap dst = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        View root = LayoutInflater.from(TheApplication.getAppContext()).inflate(R.layout.view_applock_blur_bottom, null);
        ImageView icon = (ImageView) root.findViewById(R.id.view_applock_float_layout_bg_icon);
        icon.setImageBitmap(bitmap);
        Canvas canvas = new Canvas(dst);
        // 绘制到画板上
        int withSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        root.measure(withSpec, heightSpec);
        root.layout(0, 0, root.getMeasuredWidth(), root.getMeasuredHeight());
        root.draw(canvas);
        Logger.w("bitmap start >> ", String.valueOf(System.currentTimeMillis()));
        dst = DrawUtils.scaleBitmap(dst, 0.2f);
        Bitmap to = DrawUtils.blur(dst, 25, true);
        to = DrawUtils.scaleBitmap(to, 1f);
        Logger.w("bitmap end >> ", String.valueOf(System.currentTimeMillis()));
        return to;
    }


    private final long DELAY_CLEAR_ERROR_TIME_LONG = 1000;

    private Runnable mDelayClearErrorWork = new Runnable() {
        @Override
        public void run() {
            if (mPatternView != null) {
                mPatternView.clearPattern();
            }
        }
    };

    /**
     * 延时清除掉错误图案
     */
    public void delayClearErrorPattern() {
        if (mPatternView != null) {
            mPatternView.setDisplayMode(PatternView.DisplayMode.Wrong);
        }
        if (mHandler != null) {
            mHandler.postDelayed(mDelayClearErrorWork, DELAY_CLEAR_ERROR_TIME_LONG);
        }
    }

    public void showNumberErrorState() {
        if (mNumberLockerView != null) {
            mNumberLockerView.shakeNumberLayout();
        }
    }

    private void removeClearErrorWork() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mDelayClearErrorWork);
        }
    }

    /**
     * 是否需要杀掉应用
     */
    public boolean isNeedKillLockerApp() {
        return mMode == VIEW_OUTSIDE_APP;
    }

    /**
     * 是否在内部
     */
    public boolean isInsideAppLockPop() {
        return mMode == VIEW_INSIDE_APP;
    }
}
