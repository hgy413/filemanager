package com.jb.filemanager.function.permissionalarm.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.permissionalarm.event.PermissionViewDismissEvent;
import com.jb.filemanager.function.permissionalarm.manager.PermissionAlarmManager;
import com.jb.filemanager.function.permissionalarm.utils.PermissionHelper;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;

import java.util.List;

/**
 * @author: nieyh
 * @date: 2017-2-10 11:34
 */
public class PermissionAlertView extends BasePermissionView {
    private RelativeLayout mRoot;
    private TextView mTvTitle, mTvDesc;

    private String mPackageName;
    private List<String> mPermissions;
    private int mPermissionLevel;
    private PermissionHelper mPermissionHelper;
    private ImageView mSetting;
    private ImageView mClose;
    private TextView mStop;

    public PermissionAlertView(Context context) {
        super(context);
    }

    public PermissionAlertView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PermissionAlertView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PermissionAlertView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void initView() {
        inflate(getContext(), R.layout.view_permission_alert, this);
        mPermissionHelper = new PermissionHelper();
        TextView tvDetail = (TextView) this.findViewById(R.id.tv_permission_alarm_details);
        mRoot = (RelativeLayout) this.findViewById(R.id.activity_permission_alarm_dialog_root);
        mTvTitle = (TextView) this.findViewById(R.id.tv_permission_alarm_title);
        mClose = (ImageView) this.findViewById(R.id.view_permission_alert_close);
        mTvDesc = (TextView) this.findViewById(R.id.tv_permission_alarm_description);
        mSetting = (ImageView) this.findViewById(R.id.view_permission_alert_setting);
        mStop = (TextView) this.findViewById(R.id.dialog_stop_stop);
        mSetting.setColorFilter(0x00000000, PorterDuff.Mode.SRC_ATOP);
        tvDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri packageURI = Uri.parse("package:" + mPackageName);
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
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

        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mQuickClickGuard.isQuickClick(v.getId())) {
                    PermissionAlarmManager.getInstance().changerSwitch();
                    //设置买量用户可以显示开关
                    SharedPreferencesManager.getInstance(TheApplication.getAppContext()).commitBoolean(IPreferencesIds.KEY_PERMISSION_ALARM_SWITCHER_BUY_USER_SHOW, true);
                    //销毁整个对话框
                    TheApplication.getGlobalEventBus().post(new PermissionViewDismissEvent(true));
                }
            }
        });
    }

    @Override
    protected FrameLayout getAdRootView() {
        return (FrameLayout) this.findViewById(R.id.fl_permission_alarm_ad);
    }

    @Override
    public void buildView(String packageName, List<String> permissions) {
        if (permissions == null || permissions.size() == 0 ) {
            TheApplication.getGlobalEventBus().post(new PermissionViewDismissEvent());
            return;
        }
        ImageView logo = (ImageView) this.findViewById(R.id.iv_permission_alarm_icon);
        mPackageName = packageName;
        mPermissions = permissions;
        int level = 1;
        if (permissions != null && permissions.size() > 0) {
            level = mPermissionHelper.getLevel(permissions.get(0));
        }
        setTitleByLevel(mTvTitle, level, permissions);
        String desc = getDescriptionByLevel(level);
        mPermissionLevel = level;
        mTvDesc.setText(desc);

        PackageInfo packageInfo = null;
        try {
            packageInfo = getContext().getPackageManager().getPackageInfo(mPackageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null) {
            logo.setImageResource(R.drawable.default_icon);
        } else {
            logo.setImageDrawable(packageInfo.applicationInfo.loadIcon(getContext().getPackageManager()));
        }
    }

    @Override
    public void toRelease() {
        super.toRelease();
    }

    private void setTitleByLevel(TextView titleView, int level, List<String> permissions) {
        int id = R.string.permission_alarm_dialog_title_lv1_lv2;
        int size = 0;
        StringBuilder sb = new StringBuilder();
        if (permissions != null) {
            for (String permission : permissions) {
                if (mPermissionHelper.getLevel(permission) == level) {
                    sb.append(getContext().getString(mPermissionHelper.getPermissionNameRes(permission)));
                    sb.append(",");
                    size++;
                    if (size >= 3) {
                        break;
                    }
                }
            }
        }
        if (sb.length() > 0) {
            int index = sb.lastIndexOf(",");
            if (index > 0) {
                sb.deleteCharAt(index);
            }
        }
        String title = null;
        int permissionSize = 0;
        if (permissions != null) {
            permissionSize = permissions.size();
        }
        if (level == 1 || level == 2) {
            title = getContext().getString(id, sb.toString(), permissionSize);
        } else if (level == 3) {
            id = R.string.permission_alarm_dialog_title_lv3;
            title = getContext().getString(id, sb.toString());
        }
        titleView.setText(title);
        if (size > 0 && title != null) {
            try {
                String startString = getContext().getString(mPermissionHelper.
                        getPermissionNameRes(permissions.get(0)));
                String endString = getContext().getString(mPermissionHelper.
                        getPermissionNameRes(permissions.get(size - 1)));
                int indexStart = title.indexOf(startString);
                int indexEnd = title.indexOf(endString) + endString.length();

                SpannableStringBuilder ssb = new SpannableStringBuilder(titleView.getText().toString());
                ssb.setSpan(new ForegroundColorSpan(Color.RED), indexStart, indexEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                titleView.setText(ssb);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private String getDescriptionByLevel(int level) {
        int id = R.string.permission_alarm_dialog_content_lv2;
        if (level == 1) {
            id = R.string.permission_alarm_dialog_content_lv1;
        } else if (level == 2) {
            id = R.string.permission_alarm_dialog_content_lv2;
        } else if (level == 3) {
            id = R.string.permission_alarm_dialog_content_lv3;
        }
        return getContext().getString(id);
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
}
