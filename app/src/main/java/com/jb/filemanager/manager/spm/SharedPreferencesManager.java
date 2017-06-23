package com.jb.filemanager.manager.spm;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by bill wang on 2017/6/20.
 */

public class SharedPreferencesManager {
    /**
     * @author nieyh
     * @date 2016-12-20
     * 通过阅读系统源码得知：
     * android系统 SharedPreferences 中提供了一层缓存机制
     * 缓存机制描述：
     *  1、SharedPreferencesImpl在构造函数中就读取一遍本地sp文件，保存到 Map文件中。
     *  2、当有其他进程改变了当前sp文件，则重新读取sp文件到Map中。
     *  3、其他情况下，不会重新读取sp文件。
     * commit 与 apply 的比较：
     *  commit将同步的将数据写到preferences；
     *  apply立即更改内存中的SharedPreferences，然后再开始异步提交到磁盘中。
     * 所以当执行完Apply后 先在SharedPreferencesImpl的Map中保存数据，所以通过get是可以直接从Map中读取到的。
     * */

    private static final String DEFAULT_SHARE_PREFERENCES_FILE = "default_cfg";

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private HashMap<String, Object> mSPData = new HashMap<>();

    private static SharedPreferencesManager sInstance;

    public static SharedPreferencesManager getInstance(Context context) {
        synchronized (SharedPreferencesManager.class) {
            if (sInstance == null) {
                sInstance = new SharedPreferencesManager(context);
            }
            return sInstance;
        }
    }

    public SharedPreferencesManager(Context context) {
        try {
            mPreferences = context.getSharedPreferences(DEFAULT_SHARE_PREFERENCES_FILE, Context.MODE_APPEND);
            mEditor = mPreferences.edit();
        } catch (Exception e) {
            mPreferences = null;
            mEditor = null;
            e.printStackTrace();
        }
    }

    public boolean getBoolean(String key, boolean defValue) {
        synchronized (SharedPreferencesManager.class) {
            boolean value = defValue;
            if (mSPData.containsKey(key)) {
                value = (Boolean) mSPData.get(key);
            } else {
                if (mPreferences != null) {
                    value = mPreferences.getBoolean(key, defValue);
                    mSPData.put(key, value);
                }
            }
            return value;
        }
    }

    public boolean getBooleanFromSP(String key, boolean defValue) {
        synchronized (SharedPreferencesManager.class) {
            boolean value = defValue;
            if (mPreferences != null) {
                value = mPreferences.getBoolean(key, defValue);
            }
            return value;
        }
    }

    public float getFloat(String key, float defValue) {
        synchronized (SharedPreferencesManager.class) {
            float value = defValue;
            if (mSPData.containsKey(key)) {
                value = (Float) mSPData.get(key);
            } else {
                if (mPreferences != null) {
                    value = mPreferences.getFloat(key, defValue);
                    mSPData.put(key, value);
                }
            }
            return value;
        }
    }

    public int getInt(String key, int defValue) {
        synchronized (SharedPreferencesManager.class) {
            int value = defValue;
            if (mSPData.containsKey(key)) {
                value = (Integer) mSPData.get(key);
            } else {
                if (mPreferences != null) {
                    value = mPreferences.getInt(key, defValue);
                    mSPData.put(key, value);
                }
            }
            return value;
        }
    }

    public int getIntFromSP(String key, int defValue) {
        synchronized (SharedPreferencesManager.class) {
            int value = defValue;
            if (mPreferences != null) {
                value = mPreferences.getInt(key, defValue);
            }
            return value;
        }
    }

    public long getLong(String key, long defValue) {
        synchronized (SharedPreferencesManager.class) {
            long value = defValue;
            if (mSPData.containsKey(key)) {
                value = (Long) mSPData.get(key);
            } else {
                if (mPreferences != null) {
                    value = mPreferences.getLong(key, defValue);
                    mSPData.put(key, value);
                }
            }
            return value;
        }
    }

    public long getLongFromSP(String key, long defValue) {
        synchronized (SharedPreferencesManager.class) {
            long value = defValue;
            if (mPreferences != null) {
                value = mPreferences.getLong(key, defValue);
            }
            return value;
        }
    }

    public String getString(String key, String defValue) {
        synchronized (SharedPreferencesManager.class) {
            String value = defValue;
            if (mSPData.containsKey(key)) {
                value = (String) mSPData.get(key);
            } else {
                if (mPreferences != null) {
                    value = mPreferences.getString(key, defValue);
                    mSPData.put(key, value);
                }
            }
            return value;
        }
    }

    public void commitBoolean(String key, boolean value) {
        synchronized (SharedPreferencesManager.class) {
            if (mPreferences != null && mEditor != null) {
                mEditor.putBoolean(key, value);
                mEditor.apply();
                mSPData.put(key, value);
            }
        }
    }

    public void commitInt(String key, int value) {
        synchronized (SharedPreferencesManager.class) {
            if (mPreferences != null && mEditor != null) {
                mEditor.putInt(key, value);
                mEditor.apply();
                mSPData.put(key, value);
            }
        }
    }

    public void commitFloat(String key, float value) {
        synchronized (SharedPreferencesManager.class) {
            if (mPreferences != null && mEditor != null) {
                mEditor.putFloat(key, value);
                mEditor.apply();
                mSPData.put(key, value);
            }
        }
    }

    public void commitLong(String key, long value) {
        synchronized (SharedPreferencesManager.class) {
            if (mPreferences != null && mEditor != null) {
                mEditor.putLong(key, value);
                mEditor.apply();
                mSPData.put(key, value);
            }
        }
    }

    public void commitString(String key, String value) {
        synchronized (SharedPreferencesManager.class) {
            if (mPreferences != null && mEditor != null) {
                mEditor.putString(key, value);
                mEditor.apply();
                mSPData.put(key, value);
            }
        }
    }

    public boolean contains(String key) {
        synchronized (SharedPreferencesManager.class) {
            return mPreferences != null && mPreferences.contains(key);
        }
    }

    public void remove(String key) {
        synchronized (SharedPreferencesManager.class) {
            if (mPreferences != null && mEditor != null) {
                mEditor.remove(key);
                mEditor.apply();
                mSPData.remove(key);
            }
        }
    }

    public long getLongIndirect(String key, long defValue) {
        synchronized (SharedPreferencesManager.class) {
            long value = defValue;
            if (mPreferences != null) {
                value = mPreferences.getLong(key, defValue);
            }
            return value;
        }
    }

}
