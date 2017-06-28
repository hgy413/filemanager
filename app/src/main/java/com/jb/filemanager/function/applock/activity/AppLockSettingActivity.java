package com.jb.filemanager.function.applock.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.dialog.WrongTimesSettingDialog;
import com.jb.filemanager.function.applock.event.IntruderSwitcherStateEvent;
import com.jb.filemanager.function.applock.model.AppLockerDataManager;
import com.jb.filemanager.function.applock.view.CameraPermissionCheckView;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.util.APIUtil;

/**
 * Created by nieyh on 2017/1/4. <br/>
 * 描述: <br/>
 * 如果只是单独设置入侵者数据则使用{@link Intent#putExtra(String, boolean)} 传入参数 {@link #SETTING_ARG}
 * <ol>
 * <li>{@link #SETTING_INTRUDER} 展示单独入侵者设置</li>
 * <ol/>
 */

public class AppLockSettingActivity extends BaseActivity {

    public static final int SETTING_INTRUDER = 1;
    public static final String SETTING_ARG = "setting_argument";

    private View mPasscodeTitle;
    private View mWrongTimesSetEntrance;
    private TextView mWrongTimesTxt;
    private View mChangePasscode;
    private View mChangeIssues;
    private View mInrudersSwitcher;
    private ImageView mSwiterButton;
    private View mBack;
    private TextView mTitle;
    private TextView mPasscodeType;
    private CameraPermissionCheckView mCameraPermissionCheckView;
    private WrongTimesSettingDialog mWrongTimesSettingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applock_setting);
        initView();
        initListener();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        mCameraPermissionCheckView = new CameraPermissionCheckView(this);
        mInrudersSwitcher = findViewById(R.id.activity_applock_setting_intruder_switcher);
        mSwiterButton = (ImageView) findViewById(R.id.activity_applock_setting_intruder_switcher_btu);
        mWrongTimesSetEntrance = findViewById(R.id.activity_applock_setting_intruder_wrongtimes);
        mWrongTimesTxt = (TextView) findViewById(R.id.activity_applock_setting_intruder_wrongtimes_txt);
        mPasscodeTitle = findViewById(R.id.activity_applock_setting_passcode_title);
        mChangePasscode = findViewById(R.id.activity_applock_setting_passcode_change_psd);
        mChangeIssues = findViewById(R.id.activity_applock_setting_passcode_change_issues);
        mBack = findViewById(R.id.activity_title_icon);
        mTitle = (TextView) findViewById(R.id.activity_title_word);
        mTitle.setText(R.string.app_lock_setting_title);
        mPasscodeType = (TextView) findViewById(R.id.activity_applock_setting_passcode_type);
        ImageView arrow1 = (ImageView) findViewById(R.id.activity_applock_setting_passcode_change_psd_arrow);
        ImageView arrow2 = (ImageView) findViewById(R.id.activity_applock_setting_passcode_change_issues_arrow);
        arrow1.setColorFilter(0xff219eff, PorterDuff.Mode.SRC_ATOP);
        arrow2.setColorFilter(0xff219eff, PorterDuff.Mode.SRC_ATOP);
        initGradientBg();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int arg = bundle.getInt(SETTING_ARG);
            switch (arg) {
                case SETTING_INTRUDER:
                    mPasscodeTitle.setVisibility(View.GONE);
                    mChangePasscode.setVisibility(View.GONE);
                    mChangeIssues.setVisibility(View.GONE);
                    break;
            }
        }
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(TheApplication.getAppContext());
        //设置开关是否打开
        mSwiterButton.setSelected(sharedPreferencesManager.getBoolean(IPreferencesIds.KEY_APP_LOCK_REVEAL_ENABLE, false));
        //设置错误次数
        mWrongTimesTxt.setText(String.valueOf(sharedPreferencesManager.getInt(IPreferencesIds.KEY_APP_LOCK_WRONG_PSD_TIMES, 2)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isPatternPsd = AppLockerDataManager.getInstance().isPatternPsd();
        if (isPatternPsd) {
            mPasscodeType.setText(R.string.app_lock_setting_passcode_type_pattern);
        } else {
            mPasscodeType.setText(R.string.app_lock_setting_passcode_type_number);
        }
    }

    /**
     * 初始化渐变背景
     */
    private void initGradientBg() {
        View root = findViewById(R.id.activity_applock_setting_root_bg);
        int startColor = 0xff0084ff;
        int endColor = 0xff3bd6f2;
        GradientDrawable gradientDrawableLR = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{startColor, endColor});
        gradientDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawableLR.setShape(GradientDrawable.RECTANGLE);
        APIUtil.setBackground(root, gradientDrawableLR);
    }

    /**
     * 设置监听器
     */
    private void initListener() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mQuickClickGuard.isQuickClick(v.getId())) {
                    onBackPressed();
                }
            }
        });

        mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mQuickClickGuard.isQuickClick(v.getId())) {
                    onBackPressed();
                }
            }
        });

        mChangeIssues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mQuickClickGuard.isQuickClick(v.getId())) {
                    Intent i = new Intent(AppLockSettingActivity.this, PsdSettingActivity.class);
                    i.putExtra(PsdSettingActivity.PSD_SETTING_MODE, PsdSettingActivity.QUESTION_REST);
                    startActivity(i);
                }
            }
        });

        mChangePasscode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mQuickClickGuard.isQuickClick(v.getId())) {
                    Intent i = new Intent(AppLockSettingActivity.this, PsdSettingActivity.class);
                    i.putExtra(PsdSettingActivity.PSD_SETTING_MODE, PsdSettingActivity.PSD_REST);
                    i.putExtra(PsdSettingActivity.PSD_DEFAULT_TYPE_IS_PATTERN, AppLockerDataManager.getInstance().isPatternPsd());
                    startActivity(i);
                }
            }
        });

        mWrongTimesSetEntrance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!mQuickClickGuard.isQuickClick(v.getId())) {
                    //先不做
                    if (mWrongTimesSettingDialog == null) {
                        mWrongTimesSettingDialog = new WrongTimesSettingDialog(AppLockSettingActivity.this);
                    }
                    SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(TheApplication.getAppContext());
                    mWrongTimesSettingDialog.setChosenNumber(sharedPreferencesManager.getInt(IPreferencesIds.KEY_APP_LOCK_WRONG_PSD_TIMES, 2));
                    mWrongTimesSettingDialog.setOnListChoiceListener(new WrongTimesSettingDialog.OnListChoiceListener() {
                        @Override
                        public void onChoiced(int value) {
                            SharedPreferencesManager.getInstance(TheApplication.getAppContext()).commitInt(IPreferencesIds.KEY_APP_LOCK_WRONG_PSD_TIMES, value);
                            mWrongTimesTxt.setText(String.valueOf(value));
                        }
                    });
                    mWrongTimesSettingDialog.show();
                }
            }
        });

        mInrudersSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mQuickClickGuard.isQuickClick(v.getId())) {
                    SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(TheApplication.getAppContext());
                    boolean isEnable = sharedPreferencesManager.getBoolean(IPreferencesIds.KEY_APP_LOCK_REVEAL_ENABLE, false);
                    //如果是从关闭->开启 则需要验证权限
                    if (!isEnable) {
                        //是否成功开启
                        boolean isSuccess = mCameraPermissionCheckView.show();
                        if (isSuccess) {
                            mSwiterButton.setSelected(!isEnable);
                            TheApplication.getGlobalEventBus().post(new IntruderSwitcherStateEvent(!isEnable));
                            sharedPreferencesManager.commitBoolean(IPreferencesIds.KEY_APP_LOCK_REVEAL_ENABLE, !isEnable);
                            //设置设置项被修改过
                            sharedPreferencesManager.commitBoolean(IPreferencesIds.KEY_INTRUDER_SETTING_CHANGED, true);
                        } else {
                            //提示权限失败
                            Toast.makeText(AppLockSettingActivity.this, R.string.intruder_shot_info_open_fail_tip, Toast.LENGTH_SHORT).show();
                        }
                        mCameraPermissionCheckView.close();
                    } else {
                        mSwiterButton.setSelected(!isEnable);
                        TheApplication.getGlobalEventBus().post(new IntruderSwitcherStateEvent(!isEnable));
                        sharedPreferencesManager.commitBoolean(IPreferencesIds.KEY_APP_LOCK_REVEAL_ENABLE, !isEnable);
                        //设置设置项被修改过
                        sharedPreferencesManager.commitBoolean(IPreferencesIds.KEY_INTRUDER_SETTING_CHANGED, true);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
