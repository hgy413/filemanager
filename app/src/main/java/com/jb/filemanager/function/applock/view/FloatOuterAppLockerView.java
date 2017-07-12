package com.jb.filemanager.function.applock.view;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.receiver.HomeWatcherReceiver;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.DrawUtils;
import com.jb.filemanager.util.Logger;

import java.util.List;

/**
 * Created by nieyh on 2017/1/5.
 * 应用锁展示的view
 */

public class FloatOuterAppLockerView extends FrameLayout {

    //返回键
    private ImageView mBack;
    //模糊的背景
    private ImageView mBgBlur;
    //顶部
    private View mHeader;
    //标题
    private TextView mTitle;
    //设置按钮
    private ImageView mSetting;
    //icon
    private ImageView mIcon;
    //应用名字
    private TextView mAppName;
    //图案锁
    private PatternView mPatternView;
    //忘记密码
    private View mForget;
    //解除当前应用锁定
    private View mDontLock;
    //对话框浮窗
    private View mFloatDialog;
    //遮罩视图
    private View mShadeView;
    private IFloatAppLockerViewEvtListener mIFloatAppLockerViewEvtListener;

    private HomeWatcherReceiver mHomeKeyEventReceiver;

    private Handler mHandler;

    private View mContentRoot;

    public FloatOuterAppLockerView(Context context) {
        super(context);
        mContentRoot = LayoutInflater.from(context).inflate(R.layout.view_applock_outter_float_layout, this);
        mBack = (ImageView) mContentRoot.findViewById(R.id.common_applock_bar_layout_back);
        mTitle = (TextView) mContentRoot.findViewById(R.id.common_applock_bar_layout_title);
        mTitle.setText(R.string.app_name);
        mTitle.setTextColor(0xFF44D6C3);
        mBack.setImageResource(R.drawable.app_lock_lock_on);
        mSetting = (ImageView) mContentRoot.findViewById(R.id.common_applock_bar_layout_setting);
        mSetting.setVisibility(VISIBLE);
        mSetting.setImageResource(R.drawable.ic_applock_setting2);
        mHeader = findViewById(R.id.view_applock_outter_float_layout_title);
        mBgBlur = (ImageView) mContentRoot.findViewById(R.id.view_applock_outter_float_layout_blur_bg);
        mIcon = (ImageView) mContentRoot.findViewById(R.id.view_applock_outter_float_layout_icon);
        mForget = mContentRoot.findViewById(R.id.view_applock_outter_float_layout_forget_psd);
        mFloatDialog = mContentRoot.findViewById(R.id.view_applock_outter_float_layout_dialog);
        mDontLock = mContentRoot.findViewById(R.id.view_applock_outter_float_layout_dont_lock_this_app);
        mAppName = (TextView) mContentRoot.findViewById(R.id.view_applock_outter_float_layout_appname);
        mPatternView = (PatternView) mContentRoot.findViewById(R.id.view_applock_outter_float_layout_patternview);
        mShadeView = mContentRoot.findViewById(R.id.view_applock_outter_float_layout_shade);
        initListener();
        mHomeKeyEventReceiver = new HomeWatcherReceiver();
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        mHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mFloatDialog.setVisibility(VISIBLE);
            }
        });
        mFloatDialog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mFloatDialog.setVisibility(GONE);
            }
        });
        mForget.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIFloatAppLockerViewEvtListener != null) {
                    mIFloatAppLockerViewEvtListener.onForgetClick(v);
                }
                // TODO: 17-7-12 忘记密码
            }
        });

        mDontLock.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 17-7-12 解除锁定这个密码
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
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
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
    }

    public void setIFloatAppLockerViewEvtListener(IFloatAppLockerViewEvtListener iFloatAppLockerViewEvtListener) {
        this.mIFloatAppLockerViewEvtListener = iFloatAppLockerViewEvtListener;
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
        if (mFloatDialog != null) {
            mFloatDialog.setVisibility(GONE);
        }
    }

    /**
     * 绑定数据
     * 在应用外展示
     *
     * @param pkgName
     */
    public void bindViewData(String pkgName) {
        PackageInfo packageInfo = AppUtils.getAppPackageInfo(TheApplication.getAppContext(), pkgName);
        String appName = AppUtils.getAppName(TheApplication.getAppContext(), packageInfo);
        Drawable appIcon = AppUtils.getIconByPkgInfo(TheApplication.getAppContext(), packageInfo);
        if (!TextUtils.isEmpty(appName)) {
            mAppName.setText(appName);
        }
        if (appIcon != null) {
            mIcon.setImageDrawable(appIcon);
            //只有在外部显示的时候才设置模糊
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

    /**
     * 模糊应用锁背景 <br>
     * 记录：经过测试 不使用RenderScript 进行模糊处理，平均400毫秒
     * 使用RenderScript 平均200毫秒。(时间包括压缩并执行模糊)<br>
     * 比较优缺点：RenderScript 对于大图片进行模糊处理，性能远远优于直接Java层的图像处理 (<code> {@link DrawUtils#blur(Bitmap, int, boolean)}}</code>) <br/>
     * 但是 RenderScirpt <b>不支持低于API 17</b>.
     *
     * @param bitmap 图片位图文件
     * @return 模糊后的整个背景 <br>
     */
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

    private void removeClearErrorWork() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mDelayClearErrorWork);
        }
    }

    /**
     * @return 返回是否需要自己处理 <b>true</b>代表需要自己处理 反之直接外部处理
     * */
    public boolean isHandleBackPressed() {
        if (mFloatDialog != null) {
            if (mFloatDialog.getVisibility() == VISIBLE) {
                mFloatDialog.setVisibility(GONE);
                return true;
            }
        }
        return false;
    }

}
