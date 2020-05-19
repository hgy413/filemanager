package com.jiubang.commerce.ad.manager;

import android.content.Context;
import android.text.TextUtils;
import com.jiubang.commerce.ad.AdSdkContants;
import com.jiubang.commerce.ad.http.AdSdkRequestDataUtils;
import com.jiubang.commerce.thread.AdSdkThread;
import com.jiubang.commerce.thread.AdSdkThreadExecutorProxy;
import com.jiubang.commerce.utils.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class UserManager {
    private static final String KEY_NEW_USER = "new_user";
    private static final String KEY_USER_TIME = "user_time";
    private static final long TIME_ONE_DAYS = 86400000;
    private static final long TIME_THREE_DAYS = 259200000;
    private static UserManager sInstance;
    /* access modifiers changed from: private */
    public boolean mHadInit = false;
    /* access modifiers changed from: private */
    public boolean mIsNewUser;
    /* access modifiers changed from: private */
    public long mLaunchTime;

    public interface IUserListener {
        void onUser(boolean z);
    }

    private UserManager(Context context) {
    }

    static String getFILE_PATH() {
        return AdSdkContants.getADVERT_CONFIG_PATH() + AdSdkRequestDataUtils.RESPONSE_JOSN_TAG_UFLAG_USER;
    }

    public static synchronized UserManager getInstance(Context context) {
        UserManager userManager;
        synchronized (UserManager.class) {
            if (sInstance == null) {
                sInstance = new UserManager(context);
            }
            userManager = sInstance;
        }
        return userManager;
    }

    public void isNewUser(final IUserListener listener) {
        if (listener != null) {
            new AdSdkThread(new Runnable() {
                public void run() {
                    if (!UserManager.this.mHadInit) {
                        UserManager.this.init();
                    }
                    if (UserManager.this.mIsNewUser && UserManager.this.beyond1Days(UserManager.this.mLaunchTime)) {
                        boolean unused = UserManager.this.mIsNewUser = UserManager.this.getIsNewUserFromFile();
                    }
                    AdSdkThreadExecutorProxy.runOnMainThread(new Runnable() {
                        public void run() {
                            listener.onUser(UserManager.this.mIsNewUser);
                        }
                    });
                }
            }).start();
        }
    }

    /* access modifiers changed from: private */
    public void init() {
        this.mLaunchTime = System.currentTimeMillis();
        this.mIsNewUser = getIsNewUserFromFile();
        this.mHadInit = true;
    }

    /* access modifiers changed from: private */
    public boolean getIsNewUserFromFile() {
        JSONObject json = getJsonFromFile();
        if (json != null) {
            boolean ret = json.optBoolean(KEY_NEW_USER);
            long time = json.optLong(KEY_USER_TIME, System.currentTimeMillis());
            if (!ret) {
                return ret;
            }
            saveJsonToFile(createJson(time));
            if (beyond3Days(time)) {
                return false;
            }
            return ret;
        }
        saveJsonToFile(createJson(System.currentTimeMillis()));
        return true;
    }

    private JSONObject createJson(long time) {
        JSONObject json = new JSONObject();
        try {
            if (beyond3Days(time)) {
                json.put(KEY_USER_TIME, time);
                json.put(KEY_NEW_USER, false);
            } else {
                json.put(KEY_USER_TIME, time);
                json.put(KEY_NEW_USER, true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private boolean beyond3Days(long time) {
        return System.currentTimeMillis() - time > TIME_THREE_DAYS;
    }

    /* access modifiers changed from: private */
    public boolean beyond1Days(long time) {
        return System.currentTimeMillis() - time > 86400000;
    }

    private JSONObject getJsonFromFile() {
        String str = FileUtils.readFileToString(getFILE_PATH());
        try {
            if (!TextUtils.isEmpty(str)) {
                return new JSONObject(str);
            }
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveJsonToFile(JSONObject json) {
        if (json != null && !TextUtils.isEmpty(json.toString())) {
            FileUtils.saveStringToSDFile(json.toString(), getFILE_PATH());
        }
    }
}
