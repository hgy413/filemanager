package com.jb.filemanager.manager.spm;

/**
 * Created by bill wang on 2017/6/21.
 */

public class IPreferencesIds {

    public static final String APP_NAME = "ace_network";
    public static final String KEY_FIRST_LAUNCH_VERSION_CODE = "key_first_launch_version_code";
    public static final String KEY_FIRST_LAUNCH_TIME = "key_first_launch_time";
    public static final String KEY_FIRST_INSTALL_TIME = "key_first_install_time";
    public static final String KEY_FIRST_INSTALL = "key_first_install";
    public static final String KEY_LAST_LAUNCH_VERSION_CODE = "key_last_launch_version_code";
    public static final String KEY_LAST_LAUNCH_TIME = "key_last_launch_time";

    // ======================= 隐私 =========================//
    // 是否同意隐私协议
    public final static String KEY_AGREE_PRIVACY = "key_agree_privacy";
    // 是否加入用户体验计划
    public final static String KEY_JOIN_USER_EXPERIENCE_PROGRAM = "key_join_user_experience_program";

    // ======================= AB Test =========================//
    // AB Test生成的用户类型  值类型:String
    public final static String KEY_AB_TEST_USER = "key_ab_test_user";
    public final static String KEY_AB_TEST_VERSION = "key_ab_test_version";

    // ======================= Facebook =========================//
    // facebook checker
    public static final String KEY_IS_FACEBOOK_CHECKER = "key_is_facebook_checker";

    // ======================= Feedback =========================//
    //是否反馈提示警报已经展示过
    public static final String KEY_FEEDBACK_WARN_TIP_SHOWED = "key_feedback_warn_tip_showed";

    // ======================= 统计 =========================//
    // 是否是新用户
    public final static String KEY_IS_NEW_USER = "key_is_new_user";



    // 设置是否显示隐藏文件
    public final static String KEY_SETTING_SHOW_HIDDEN_FILES = "key_setting_show_hidden_files";

    public static final String KEY_CLEAN_SCAN_TIME = "key_clean_scan_time";
    //applock 设置项
    public static final String KEY_APP_LOCK_ENABLE = "key_app_lock_enable";
    //密码键值
    public static final String KEY_APP_LOCK_PASSWORD = "key_app_lock_password";
    //问题的内容
    public static final String KEY_APP_LOCK_QUESTION_NAME = "key_app_lock_question_name";
    //问题的答案
    public static final String KEY_APP_LOCK_QUESTION_RESULT = "key_app_lock_question_result";
    //应用锁的锁模式
    public static final String KEY_APP_LOCK_IS_LOCK_FOR_LEAVE = "key_app_lock_is_lock_for_leave";

    //评分引导新增
    //短时间内离开gp
    public static final String KEY_EXIT_GP_SHORT_TIME = "key_rate_stimeexit";
    //上次dismiss时间
    public static final String KEY_LAST_TIME = "key_rate_last";
    //评价成功
    public static final String KEY_RATE_SUCCESS = "key_rate_success";
    //评分引导出现次数
    public static final String KEY_APPEAR_TIMES = "key_rate_appeartimes";
    //是否再次弹出 点击No之后
    public static final String KEY_IS_POP = "key_rate_ispop";
    //对话框是否已经展示过
    public static final String KEY_IS_SHOW_DIALOG_ONE = "key_is_show_dialog_one";
    public static final String KEY_IS_SHOW_DIALOG_TWO = "key_is_show_dialog_two";
    public static final String KEY_IS_SHOW_DIALOG_THIRD = "key_is_show_dialog_third";

    //权限警报
    public static final String KEY_PERMISSION_ALARM_USER_HAS_CHANGE = "key_permission_alarm_user_has_change";
    //权限警报
    public static final String KEY_PERMISSION_ALARM_ENABLE = "key_permission_alarm_enable";
    //买量用户是否展示开关
    public static final String KEY_PERMISSION_ALARM_SWITCHER_BUY_USER_SHOW = "key_permission_alert_switcher_buy_user_show";
    //权限对话框展示次数
    public static final String KEY_PERMISSION_DLG_SHOW_TIMES = "key_permission_dlg_show_times";
    //是否监听usb
    public static final String KEY_USB_CONNECTED_TIP_ENABLE = "key_usb_connected_tip_enable";
}
