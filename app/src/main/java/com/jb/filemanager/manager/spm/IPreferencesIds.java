package com.jb.filemanager.manager.spm;

/**
 * Created by bill wang on 2017/6/21.
 */

public class IPreferencesIds {
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
    //是否开启拍照
    public static final String KEY_APP_LOCK_REVEAL_ENABLE = "key_app_lock_reveal_enable";
    //密码输入错误次数 达到则 进行拍照
    public static final String KEY_APP_LOCK_WRONG_PSD_TIMES = "key_app_lock_wrong_psd_times";
    //密码键值
    public static final String KEY_APP_LOCK_PASSWORD = "key_app_lock_password";
    //应用锁的密码类型
    public static final String KEY_APP_LOCK_PSD_TYPE = "key_app_lock_psd_type";
    //问题的内容
    public static final String KEY_APP_LOCK_QUESTION_NAME = "key_app_lock_question_name";
    //问题的答案
    public static final String KEY_APP_LOCK_QUESTION_RESULT = "key_app_lock_question_result";
    //弹框上次弹出的时间
    public static final String KEY_LAST_INTRUDER_DIALOG_POP_TIME = "key_last_intruder_dialog_pop_time";
    //弹出次数
    public static final String KEY_INTRUDER_DIALOG_POP_TIMES = "key_intruder_dialog_pop_times";
    //设置开关是否修改过
    public static final String KEY_INTRUDER_SETTING_CHANGED = "key_intruder_setting_changed";
    //返回弹窗提示次数
    public static final String KEY_BACK_TIP_DIALOG_SHOW_TIMES = "key_back_tip_dialog_show_times";
}
