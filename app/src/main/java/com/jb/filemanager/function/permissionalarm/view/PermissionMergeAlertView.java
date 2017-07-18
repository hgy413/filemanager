package com.jb.filemanager.function.permissionalarm.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.permissionalarm.event.PermissionViewDismissEvent;
import com.jb.filemanager.function.permissionalarm.manager.PermissionAlarmManager;

import java.util.HashSet;
import java.util.List;

import static android.text.Html.FROM_HTML_MODE_LEGACY;


/**
 * @description
 * @author: nieyh
 * @date: 2017-2-10 11:34
 */
public class PermissionMergeAlertView extends BasePermissionView {
    private FrameLayout mAdRoot;
    private TextView mTvTitle;
    private TextView mTvDesc;
    private ImageView mIvArrowRight, mIvArrowLeft;
    private LinearLayout mLogoLine;
    private ImageView mSetting;
    private View mStop;
    /**
     * 第一个图标不需要LeftMargin
     */
    private boolean mIsFirstIcon;
    private HashSet<String> mPkgSet = new HashSet<>();
    private View mClose;

    public PermissionMergeAlertView(Context context) {
        super(context);
    }

    public PermissionMergeAlertView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PermissionMergeAlertView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PermissionMergeAlertView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void buildView(String packageName, List<String> permissionLists) {
        //如果对应包存在 则退出显示
        if (mPkgSet.contains(packageName)) {
            return;
        }
        //否则 添加到 包名set中
        mPkgSet.add(packageName);
        PackageInfo packageInfo = null;
        try {
            packageInfo = getContext().getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        LayoutInflater inflater = LayoutInflater.from(getContext());
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.layout_permission_logo_merge, this, false);
        ImageView logo = (ImageView) view.findViewById(R.id.iv_permission_logo_merge_icon);
        if (packageInfo == null) {
            logo.setImageResource(android.R.drawable.sym_def_app_icon);
        } else {
            logo.setImageDrawable(packageInfo.applicationInfo.loadIcon(getContext().getPackageManager()));
        }
        if (mIsFirstIcon) {
            View marginView = view.findViewById(R.id.v_permission_logo_merge_left_margin);
            marginView.setVisibility(GONE);
            mIsFirstIcon = !mIsFirstIcon;
        }
        mLogoLine.addView(view);

        int childCount = mLogoLine.getChildCount();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mTvTitle.setText(Html.fromHtml(getResources().getString(R.string.no_new_permissions_line1, childCount), FROM_HTML_MODE_LEGACY));
        } else {
            mTvTitle.setText(Html.fromHtml(getResources().getString(R.string.no_new_permissions_line1, childCount)));
        }
        if (childCount > 3) {
            mIvArrowRight.setVisibility(VISIBLE);
            mIvArrowLeft.setVisibility(VISIBLE);
        } else {
            mIvArrowRight.setVisibility(INVISIBLE);
            mIvArrowLeft.setVisibility(INVISIBLE);
        }

    }

    @Override
    public boolean onBackPress() {
        if (mStop.getVisibility() == VISIBLE) {
            mStop.setVisibility(GONE);
            return false;
        }
        return true;
    }

    @Override
    public void onOutSideTouch() {
        mStop.setVisibility(GONE);
    }

    @Override
    protected FrameLayout getAdRootView() {
        return (FrameLayout) this.findViewById(R.id.fl_permission_alarm_ad);
    }

    @Override
    public void toRelease() {
        super.toRelease();
    }

    @Override
    protected void initView() {
        inflate(getContext(), R.layout.layout_permission_alarm_view_merge, this);
        mIsFirstIcon = true;
        setBackgroundColor(Color.WHITE);
        TextView tvDetail = (TextView) this.findViewById(R.id.tv_permission_alarm_details);
        mTvTitle = (TextView) this.findViewById(R.id.tv_permission_alarm_title);
        mLogoLine = (LinearLayout) this.findViewById(R.id.ll_permission_alarm_merge_logo);
        mClose = this.findViewById(R.id.view_permission_alarm_view_merge_close);
        mTvDesc = (TextView) this.findViewById(R.id.tv_permission_alarm_desc);
        mIvArrowRight = (ImageView) this.findViewById(R.id.iv_permission_alarm_right_arrow);
        mIvArrowLeft = (ImageView) this.findViewById(R.id.iv_permission_alarm_left_arrow);
        mSetting = (ImageView) this.findViewById(R.id.view_permission_merge_setting);
        mStop = this.findViewById(R.id.dialog_stop_stop);
        Resources res = getResources();
        String desc = String.format(res.getString(R.string.no_new_permissions_line2), res.getString(R.string.app_name));
        mTvDesc.setText(desc);
        tvDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                getContext().startActivity(intent);
                setVisibility(GONE);
                TheApplication.getGlobalEventBus().post(new PermissionViewDismissEvent());
            }
        });
        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStop.getVisibility() == VISIBLE) {
                    mStop.setVisibility(GONE);
                } else {
                    mStop.setVisibility(VISIBLE);
                }
            }
        });
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
                TheApplication.getGlobalEventBus().post(new PermissionViewDismissEvent());
            }
        });

        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStop.setVisibility(GONE);
            }
        });

        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mQuickClickGuard.isQuickClick(v.getId())) {
                    PermissionAlarmManager.getInstance().changerSwitch();
                    //设置买量用户可以显示开关
//                    SharedPreferencesManager.getInstance(TheApplication.getAppContext()).commitBoolean(IPreferencesIds.KEY_PERMISSION_ALARM_SWITCHER_BUY_USER_SHOW, true);
                    //销毁整个对话框
                    TheApplication.getGlobalEventBus().post(new PermissionViewDismissEvent(true));
                }
            }
        });
    }

}
