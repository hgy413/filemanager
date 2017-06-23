package com.jb.filemanager.abtest;

import android.content.Context;
import android.text.TextUtils;

import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.util.Logger;
import com.jb.ga0.commerce.util.http.GoHttpHeadUtil;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

/**
 * 类名称：ABTest<br>
 * 类描述：ABTEST测试<br>
 * 创建人：makai<br>
 * 修改人：makai, laojiale<br>
 * 修改时间：2014年11月21日 上午9:58:50<br>
 * 修改备注：<br>
 *
 * @version 1.0.0<br>
 */
@TestInfo(rebuild = false, testVersion = 8, startTime = "2017-05-17 00:00:00", endTime = "2017-05-24 23:59:59")
public class ABTest {

    private final static String TAG = ABTest.class.getSimpleName();

    private final static String HK_AND_TW_PROMOTIONS_START_TIME = "2016-08-04 00:00:00";
    private final static String HK_AND_TW_PROMOTIONS_END_TIME = "2016-08-11 23:59:59";
    private final static String EAST_SOUTH_ASIA_PROMOTIONS_START_TIME = "2016-08-05 00:00:00";
    private final static String EAST_SOUTH_ASIA_PROMOTIONS_END_TIME = "2016-08-19 23:59:59";


    private String mUser = "";
    private String mEndTime;
    private String mStartTime;

    private static ABTest sInstance;

    /**
     * 是否升级用户(老用户)
     */
    private boolean mIsUpGradeUser = false;

    private Context mContext;

    private ABTest(Context context) {
        mContext = context;
    }

    public static void initSingleton(Context context) {
        sInstance = new ABTest(context);
    }

    public static ABTest getInstance() {
        return sInstance;
    }

    public void init(String user, boolean isUpGradeUser) {
        // NOTE: isUpGradeUser 从v1.13.1才有效

        initLocal(user, isUpGradeUser);

        Logger.d(TAG, "mUser: " + mUser);

       /* FileLogger.writeFileLogger("用户类型: " + mUser,
                FileLogger.LOG_FILE_AB_TEST);*/
    }

    private void initLocal(String user, boolean isUpGradeUser) {
        boolean rebuild;
        int currentTestVersion;
        mIsUpGradeUser = isUpGradeUser;
        SharedPreferencesManager spm = SharedPreferencesManager.getInstance(mContext);
        if (getClass().isAnnotationPresent(TestInfo.class)) {
            TestInfo testInfo = getClass().getAnnotation(TestInfo.class);
            rebuild = testInfo.rebuild();
            currentTestVersion = testInfo.testVersion();
            mStartTime = testInfo.startTime();
            mEndTime = testInfo.endTime();
        } else {
            throw new RuntimeException("the test info not exist");
        }

        // 如果处于测试时段
        if (isValidTestDate(mStartTime, mEndTime)) {
            if (rebuild) {
                user = null;
            }
            int testVersion = spm.getInt(IPreferencesIds.KEY_AB_TEST_VERSION, 0);
            if (mIsUpGradeUser) {
                if (testVersion != currentTestVersion) {
                    user = TestUser.USER_Z;
                }
            } else {
                if (TextUtils.isEmpty(user)) {
                    user = genUser();
                }
            }

            if (testVersion != currentTestVersion) {
                spm.commitInt(IPreferencesIds.KEY_AB_TEST_VERSION, currentTestVersion);
            }
        } else {
            user = null;
        }

        if (TextUtils.isEmpty(user)) {
            user = getDefaultUser();
        }

        mUser = user;
        saveUser(mUser);
    }

    private String randomUser(ABTestBean abBean) {
        String user = "";
        final List<String> users = new ArrayList<>();
        for (TestUserBean userBean : abBean.mUsers) {
            for (int i = 0; i < userBean.getOdds(); i++) {
                users.add(userBean.getUser());
            }
        }
        if (users.size() > 0) {
            Random random = new Random();
            user = users.get(random.nextInt(users.size()));
        }
        return user;
    }

    private void saveUser(String user) {
        final SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(mContext);
        sharedPreferencesManager.commitString(IPreferencesIds.KEY_AB_TEST_USER, user);
    }

    /**
     * 是否升级用户(老用户)<br>
     * 注意: 需要在 {@link #init(String, boolean)} 调用后才有意义<br>
     *
     * @return result
     */
    public boolean isUpGradeUser() {
        return mIsUpGradeUser;
    }

    /**
     * 是否是测试用户isTestUser(这里用一句话描述这个方法的作用)(这里描述这个方法适用条件 – 可选)<br>
     *
     * @param user
     *            {@link TestUser}
     * @return boolean
     * @since 1.0.0
     */
    public boolean isTestUser(String user) {
        return !TextUtils.isEmpty(user) && user.equals(getUser());
//        return true;
    }

    /**
     * 获取当前测试用户getUser(这里用一句话描述这个方法的作用)(这里描述这个方法适用条件 – 可选)<br>
     *
     * @return String
     * @since 1.0.0
     */
    public String getUser() {
        return mUser;
    }

    private String genUser() {
        try {
            List<Field> userList = getUserList();
            int index = (int) (Math.random() * userList.size());
            Field field = userList.get(index);
            return field.get(null).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getDefaultUser();
    }

    private List<Field> getUserList() {
        Field[] fields = TestUser.class.getDeclaredFields();
        if (fields == null) {
            throw new RuntimeException("not find test user");
        }
        List<Field> list = new ArrayList<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(TestUserModel.class)) {
                TestUserModel userModel = field
                        .getAnnotation(TestUserModel.class);
                if (userModel.isTestUser()) {
                    // 区分新老用户
                    if (userModel.isUpGradeUser() != isUpGradeUser()) {
                        continue;
                    }
                    final int odds = userModel.odds();
                    for (int i = 0; i < odds; i++) {
                        list.add(field);
                    }
                }
            }
        }
        return list;
    }

    private String getDefaultUser() {
        Field[] fields = TestUser.class.getDeclaredFields();
        if (fields == null) {
            throw new RuntimeException("not find test user");
        }
        for (Field field : fields) {
            if (field.isAnnotationPresent(TestUserModel.class)) {
                TestUserModel userModel = field
                        .getAnnotation(TestUserModel.class);
                if (userModel.isDefaultUser()) {
                    try {
                        return field.get(null).toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return TestUser.USER_Z;
    }

    private boolean isValidTestDate(String start, String end) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        try {
            Date before = df.parse(start);
            Date now = new Date();
            Date after = df.parse(end);
            if (now.before(after) && now.after(before)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 是否启用服务器控制AB Test.<br>
     * 为了简易起见，本地AbTest与服务器控制不要混合使用<br>
     *
     * @return
     */
    public static boolean isRemoteAbTestEnabled() {
        // V1.23 使用服务器AbTest
        return false;
    }

    public boolean isTestCountry() {
        boolean result = true;
        String local = GoHttpHeadUtil.getLocal(mContext);
        if (local.equalsIgnoreCase("HK")
                || local.equalsIgnoreCase("ID")
                || local.equalsIgnoreCase("TH")
                || local.equalsIgnoreCase("MY")
                || local.equalsIgnoreCase("SG")
                || local.equalsIgnoreCase("TW")
                || local.equalsIgnoreCase("VN")) {
            // 香港，印度尼西亚，泰国，马来西亚，新加坡，台湾，越南
            result = false;
        }
        return result;
    }

    public boolean isHKAndTWUser() {
        boolean result = false;
        String local = GoHttpHeadUtil.getLocal(mContext);
        if (local.equalsIgnoreCase("HK")
                || local.equalsIgnoreCase("TW")) {
            // 香港,台湾
            result = true;
        }
        return result;
    }

    public boolean isHKAndTWUserInPromotions() {
        boolean result = false;
        if (isHKAndTWUser() && isValidPromotionsDate(HK_AND_TW_PROMOTIONS_START_TIME, HK_AND_TW_PROMOTIONS_END_TIME)) {
            result = true;
        }
        return result;
    }

    public boolean isEastSouthAsiaUser() {
        boolean result = false;
        String local = GoHttpHeadUtil.getLocal(mContext);
        if (local.equalsIgnoreCase("ID")
                || local.equalsIgnoreCase("TH")
                || local.equalsIgnoreCase("MY")
                || local.equalsIgnoreCase("SG")
                || local.equalsIgnoreCase("VN")) {
            // 印度尼西亚，泰国，马来西亚，新加坡，越南
            result = true;
        }
        return result;
    }

    public boolean isEastSouthAsiaUserInPromotions() {
        boolean result = false;
        if (isEastSouthAsiaUser() && isValidPromotionsDate(EAST_SOUTH_ASIA_PROMOTIONS_START_TIME, EAST_SOUTH_ASIA_PROMOTIONS_END_TIME)) {
            result = true;
        }
        return result;
    }

    private boolean isValidPromotionsDate(String start, String end) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        df.setTimeZone(TimeZone.getDefault());
        try {
            Date before = df.parse(start);
            Date now = new Date();
            Date after = df.parse(end);
            if (now.before(after) && now.after(before)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}