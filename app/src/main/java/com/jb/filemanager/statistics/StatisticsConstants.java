package com.jb.filemanager.statistics;

/**
 * Created by bill wang on 2017/6/20.
 *
 */

public interface StatisticsConstants {

    // 19协议功能id
    int PROTOCOL_19_PID = 194;
    // 45协议功能id
    int PROTOCOL_41_FUN_ID = 864;
    // 45协议功能id
    int PROTOCOL_45_FUN_ID = 863;
    // 101协议功能id
    int PROTOCOL_101_FUN_ID = 862;
    // 102协议功能id
    int PROTOCOL_102_FUN_ID = 861;
    // 103协议功能id
    int PROTOCOL_103_FUN_ID = 584;


    // 侧边栏呼出
    String HOME_SIDE_SHOW = "f000_drawer";

    // 侧边栏关闭
    String HOME_SIDE_CLOSE = "t000_drawer_close";

    // Home页
    String HOME_CLICK_DRAWER            = "c000_home_tool";
    String HOME_CLICK_SEARCH            = "c000_home_search";
    String HOME_CLICK_TAB_CATEGORY      = "c000_home_cate";
    String HOME_CLICK_TAB_STORAGE       = "c000_home_stor";
    String HOME_CLICK_CATEGORY_PHOTO    = "c000_home_pho";
    String HOME_CLICK_CATEGORY_VIDEO    = "c000_home_vedio";
    String HOME_CLICK_CATEGORY_APP      = "c000_home_app";
    String HOME_CLICK_CATEGORY_AUDIO    = "c000_home_audio";
    String HOME_CLICK_CATEGORY_DOC      = "c000_home_doc";
    String HOME_CLICK_CATEGORY_ZIP      = "c000_home_zip";
    String HOME_CLICK_CATEGORY_DOWNLOAD = "c000_home_down";
    String HOME_CLICK_CATEGORY_RECENT   = "c000_home_recent";
    String HOME_CLICK_CATEGORY_AD       = "c000_home_ad";
    String HOME_CLICK_SWITCH_SD         = "c000_home_sd";
    String HOME_CLICK_CLEAN             = "c000_home_clean";

    // 搜索页
    String SEARCH_EXIT_SEARCH           = "c000_home_exit";
    String SEARCH_SHOW_ANIM             = "f000_home_search";
    String SEARCH_SHOW_RESULT           = "f000_home_result";
    String SEARCH_CLICK_RESULT          = "c000_home_result";

    // 工具栏
    String DRAWER_ENTER_APP_LOCKER      = "c000_tool_locker";
    String DRAWER_ENTER_SMART_CHARGE    = "c000_tool_charge";
    String DRAWER_CLICK_USB_SWITCH      = "c000_tool_usb";
    String DRAWER_CLICK_LOW_SWITCH      = "c000_tool_low";
    String DRAWER_CLICK_LOGGER_SWITCH   = "c000_tool_logger";
    String DRAWER_CLICK_RATING          = "c000_tool_rating";
    String DRAWER_CLICK_UPDATE          = "c000_tool_update";
    String DRAWER_CLICK_FEEDBACK        = "c000_tool_Feed";
    String DRAWER_CLICK_ABOUT           = "c000_tool_About";

    // 树状结构
    String STORAGE_CLICK_PATH           = "c000_stor_way";
    String STORAGE_CLICK_STYLE_SWITCH   = "c000_stor_list";
    String STORAGE_CLICK_SEARCH         = "c000_stor_search";
    String STORAGE_CLICK_ACTION_MORE    = "c000_stor_caidan";
    String STORAGE_CLICK_CREATE_FOLDER  = "c000_stor_creat";
    String STORAGE_CREATE_FOLDER_OK     = "c000_stor_newok";
    String STORAGE_CREATE_FOLDER_CANCEL = "c000_stor_newexit";
    String STORAGE_CLICK_SORT           = "c000_stor_sort";
    String STORAGE_SORT_NAME            = "c000_stor_name";
    String STORAGE_SORT_DATE            = "c000_stor_data";
    String STORAGE_SORT_TYPE            = "c000_stor_type";
    String STORAGE_SORT_SIZE            = "c000_stor_size";
    String STORAGE_SORT_ASCENDING       = "c000_stor_Ascending";
    String STORAGE_SORT_DESCENDING      = "c000_stor_Descending";
    String STORAGE_CLICK_CUT            = "c000_stor_cut";
    String STORAGE_CLICK_COPY           = "c000_stor_copy";
    String STORAGE_CLICK_PASTE          = "c000_stor_paste";
    String STORAGE_CLICK_DELETE         = "c000_stor_delete";
    String STORAGE_CLICK_BOTTOM_MORE    = "c000_stor_more";
    String STORAGE_CLICK_DETAILS        = "c000_stor_detail";
    String STORAGE_CLICK_RENAME         = "c000_stor_rename";

    // TODO @nieyh 统计 USB 加完清除todo
    String LOW_SHOW                     = "f000_tool_low";
    String LOW_CLICK_CLEAN              = "c000_tool_clean";

    // TODO 统计logger
    String LOGGER_SHOW                  = "f000_tool_logger";

    // TODO @nieyh 统计 USB 加完清除todo
    String USB_SHOW                     = "f000_tool_usb";
    String USB_CLICK_MANAGER            = "c000_tool_manage";

    //清理按钮动画展示
    String HOME_SHOW_CLEAN_ANIM         = "f000_home_clean";

    //APP管理页面
    String APP_CLICK_COLLAPSE_GROUP = "c000_app_sort";
    String APP_CLICK_GROUP_SELECT_BOX = "c000_app_sortselec";
    String APP_CLICK_ITEM_SELECT_BOX = "c000_app_select";
    String APP_CLICK_UNINSTALL = "c000_app_delete";
    String APP_CLICK_SEARCH_BUTTON = "c000_app_search";

    //文档管理
    String DOC_CLICK_OTHER_FILE = "c000_doc_other";
    String DOC_CLICK_COLLAPSE_GROUP = "c000_doc_sort";
    String DOC_CLICK_GROUP_SELECT_BOX = "c000_doc_sortselect";
    String DOC_CLICK_ITEM_SELECT_BOX = "c000_doc_select";
    String DOC_CLICK_SEARCH_BUTTON = "c000_doc_search";
    String DOC_CLICK_COPY = "c000_doc_copy";
    String DOC_CLICK_CUT = "c000_doc_cut";
    String DOC_CLICK_PAST = "c000_doc_paste";
    String DOC_CLICK_DELETE = "c000_doc_delete";
    String DOC_CLICK_MORE = "c000_doc_more";
    String DOC_CLICK_DETAIL = "c000_doc_detail";
    String DOC_CLICK_RENAME = "c000_doc_rename";
    String DOC_CLICK_TXT = "c000_doc_txt";


    //清理
    String CLEAN_SCAN_ANIM_SHOW = "f000_clean_gom";
    String CLEAN_CLICK_COLLAPSE_GROUP = "c000_clean_sort";
    String CLEAN_CLICK_GROUP_SELECT_BOX = "c000_clean_sortselect";
    String CLEAN_CLICK_ITEM_SELECT_BOX = "c000_clean_select";
    String CLEAN_CLICK_CACHE_JUNK_ITEM = "c000_clean_cache";
    String CLEAN_CLICK_RESIDUAL_FILE_ITEM = "c000_clean_res";
    String CLEAN_CLICK_AD_JUNK_ITEM = "c000_clean_ad";
    String CLEAN_CLICK_SYSTEM_TEMP_ITEM = "c000_clean_sys";
    String CLEAN_CLICK_OBSOLETE_APK_ITEM = "c000_clean_obso";
    String CLEAN_CLICK_BIG_FILE_ITEM = "c000_clean_Big";
    String CLEAN_CLICK_CLEAN_BUTTON = "c000_clean_clean";
    String CLEAN_CLICK_TRASH_IGNORE = "c000_clean_white";
    String CLEAN_CLEAN_ANIM_SHOW = "f000_clean_go";
    String CLEAN_PAGE_EXIT = "c000_clean_exit";


    //about
    //点击联系我们
    String ABOUT_CONTACT_ME_CLI = "c000_About_contact";
    //点击隐私政策
    String ABOUT_PRO_CLI = "c000_About_pro";
    //点击用户体验计划
    String ABOUT_USER_CLI = "c000_About_yuser";
    //APPlock
    //点击搜索icon
    String APPLOCK_SEARCH_CLI = "c000_Locker_icon";
    //点击设置
    String APPLOCK_SETTING_CLI = "c000_Locker_set";
    //折叠Re分类
    String APPLOCK_RECOMMOND_FLOD_CLI = "c000_Locker_re";
    //折叠Other分类
    String APPLOCK_OTHER_FLOD_CLI = "c000_Locker_other";
    //点击Re分类下应用复选框
    String APPLOCK_PRE_RE_CHECK_CLI = "c000_Locker_reapp";
    //点击Ot分类下应用复选框
    String APPLOCK_PRE_OTHER_CHECK_CLI = "c000_Locker_othapp";
    //退出应用锁
    String APPLOCK_EXIT = "c000_Locker_exit";
    //点击锁定
    String APPLOCK_START_LOCK = "c000_Locker_lock";
    //密码设置第1步退出
    String APPLOCK_INIT_PSD_1_EXIT = "c000_Locker_exit1";
    //密码设置第2步退出
    String APPLOCK_INIT_PSD_2_EXIT = "c000_Locker_exit2";
    //密码设置第3步退出
    String APPLOCK_INIT_PSD_3_EXIT = "c000_Locker_exit3";
    //第3步切换问题
    String APPLOCK_INIT_PSD_3_SWITCH_QUESTION = "c000_Locker_ques";
    //第3步切换设置
    String APPLOCK_INIT_PSD_3_SWITCH_SETTING = "c000_Locker_setc";
    //第3步点击OK
    String APPLOCK_INIT_PSD_3_OK = "c000_Locker_ok";
    //应用外应用锁展示
    String APPLOCK_OUTTER_LOCK_SHOW = "f000_Locker_true";
    //应用外应用锁解锁
    String APPLOCK_OUTTER_LOCK_UNLOCK = "t000_Locker_unlock";
    //应用外点击忘记密码
    String APPLOCK_OUTTER_FORGET_PSD = "c000_Locker_forget";
    //应用外点击取消锁定
    String APPLOCK_OUTTER_CANCEL_LOCK = "c000_Locker_unset";
    //取消保护Re应用
    String APPLOCK_RE_UNLOCK = "c000_Locker_unpro";
    //保护Re应用
    String APPLOCK_RE_LOCK = "c000_Locker_pro";
    //取消保护Ot应用
    String APPLOCK_OTHER_UNLOCK = "c000_Locker_unproOt";
    //保护Ot应用
    String APPLOCK_OTHER_LOCK = "c000_Locker_proOt";
    //Feedback
    //点击yes,report
    String FEEDBACK_YES_CLI = "c000_feedback_yes";
    //点击NO
    String FEEDBACK_NO_CLI = "c000_feedback_no";
    //点击发送按钮
    String FEEDBACK_SEND_CLI = "c000_feedback_report";
    //切换反馈类型
    String FEEDBACK_SWITCH_TYPE = "c000_feedback_change";
    //smart charge
    String DIALOG_SMART_CHARGE_SHOW = "f000_smart_window";
    String DIALOG_SMART_CHARGE_SWITCH_CLI = "c000_smart_switch";
    String DIALOG_SMART_CHARGE_EXIT = "c000_smart_exit";

    // Download
    String DOWNLOAD_CLICK_GROUP_TITLE = "c000_down_sort";
    String DOWNLOAD_CLICK_SELECT_GROUP = "c000_down_sortselect";
    String DOWNLOAD_CLICK_SELECT_ITEN = "c000_down_select";
    String DOWNLOAD_CLICK_SEARCH = "c000_down_search";
    String DOWNLOAD_CLICK_COPY = "c000_down_copy";
    String DOWNLOAD_CLICK_CUT = "c000_down_cut";
    String DOWNLOAD_CLICK_PAST = "c000_down_paste";
    String DOWNLOAD_CLICK_DELETE = "c000_down_delete";
    String DOWNLOAD_CLICK_MORE = "c000_down_more";
    String DOWNLOAD_CLICK_DETAIL = "c000_down_detail";
    String DOWNLOAD_CLICK_RENAME = "c000_down_rename";
    String DOWNLOAD_CLICK_ITEM = "c000_down_single";
    String DOWNLOAD_CLICK_EXIT = "c000_down_exit";


    // Video
    String VIDEO_CLICK_GROUP_TITLE = "c000_vedio_sort";
    String VIDEO_CLICK_SELECT_GROUP = "c000_vedio_sortselect";
    String VIDEO_CLICK_SELECT_ITEN = "c000_vedio_select";
    String VIDEO_CLICK_SEARCH = "c000_vedio_search";
    String VIDEO_CLICK_COPY = "c000_vedio_copy";
    String VIDEO_CLICK_CUT = "c000_vedio_cut";
    String VIDEO_CLICK_PAST = "c000_vedio_paste";
    String VIDEO_CLICK_DELETE = "c000_vedio_delete";
    String VIDEO_CLICK_MORE = "c000_vedio_more";
    String VIDEO_CLICK_DETAIL = "c000_vedio_detail";
    String VIDEO_CLICK_RENAME = "c000_vedio_rename";
    String VIDEO_CLICK_ITEM = "c000_vedio_go";
    String VIDEO_CLICK_EXIT = "c000_vedio_exit";

    // Music
    String MUSIC_CLICK_GROUP_TITLE = "c000_audio_sort";
    String MUSIC_CLICK_SELECT_GROUP = "c000_audio_sortselect";
    String MUSIC_CLICK_SELECT_ITEN = "c000_audio_select";
    String MUSIC_CLICK_SEARCH = "c000_audio_search";
    String MUSIC_CLICK_COPY = "c000_audio_copy";
    String MUSIC_CLICK_CUT = "c000_audio_cut";
    String MUSIC_CLICK_PAST = "c000_audio_paste";
    String MUSIC_CLICK_DELETE = "c000_audio_delete";
    String MUSIC_CLICK_MORE = "c000_audio_more";
    String MUSIC_CLICK_DETAIL = "c000_audio_detail";
    String MUSIC_CLICK_RENAME = "c000_audio_rename";
    String MUSIC_CLICK_PLAY = "c000_audio_play";
    String MUSIC_CALLED_SYSTEM_PLAY = "t000_audio_success";
    String MUSIC_CLICK_EXIT = "c000_audio_exit";
}
