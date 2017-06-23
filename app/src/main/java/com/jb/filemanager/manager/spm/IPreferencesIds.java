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
}
