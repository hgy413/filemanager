package com.jiubang.commerce.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import java.io.File;
import java.util.Map;

public final class PreferencesManager {
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mPreferences;

    public PreferencesManager(Context context, String name, int mode) {
        if (context != null) {
            try {
                this.mPreferences = context.getSharedPreferences(name, mode);
                this.mEditor = this.mPreferences.edit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void clear() {
        if (this.mEditor != null) {
            this.mEditor.clear().commit();
        } else if (this.mPreferences != null) {
            this.mEditor = this.mPreferences.edit();
            this.mEditor.clear().commit();
        }
    }

    public void remove(String key) {
        this.mPreferences.edit().remove(key).commit();
    }

    public Map<String, ?> getAll() {
        return this.mPreferences.getAll();
    }

    public boolean contains(String key) {
        return this.mPreferences.contains(key);
    }

    public boolean getBoolean(String key, boolean defValue) {
        if (this.mPreferences != null) {
            return this.mPreferences.getBoolean(key, defValue);
        }
        return defValue;
    }

    public float getFloat(String key, float defValue) {
        if (this.mPreferences != null) {
            return this.mPreferences.getFloat(key, defValue);
        }
        return defValue;
    }

    public int getInt(String key, int defValue) {
        if (this.mPreferences != null) {
            return this.mPreferences.getInt(key, defValue);
        }
        return defValue;
    }

    public long getLong(String key, long defValue) {
        if (this.mPreferences != null) {
            return this.mPreferences.getLong(key, defValue);
        }
        return defValue;
    }

    public String getString(String key, String defValue) {
        if (this.mPreferences != null) {
            return this.mPreferences.getString(key, defValue);
        }
        return defValue;
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        this.mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        this.mPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void putBoolean(String key, boolean b) {
        if (this.mEditor != null) {
            this.mEditor.putBoolean(key, b);
        }
    }

    public void putInt(String key, int i) {
        if (this.mEditor != null) {
            this.mEditor.putInt(key, i);
        }
    }

    public void putFloat(String key, float f) {
        if (this.mEditor != null) {
            this.mEditor.putFloat(key, f);
        }
    }

    public void putLong(String key, long l) {
        if (this.mEditor != null) {
            this.mEditor.putLong(key, l);
        }
    }

    public void putString(String key, String s) {
        if (this.mEditor != null) {
            this.mEditor.putString(key, s);
        }
    }

    public boolean commit(boolean isAsync) {
        boolean bRet;
        if (this.mEditor == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 9 || !isAsync) {
            bRet = this.mEditor.commit();
        } else {
            this.mEditor.apply();
            bRet = true;
        }
        boolean z = bRet;
        return bRet;
    }

    public boolean commit() {
        return commit(true);
    }

    public static boolean deleteSharedPreference(Context context, String preferencesName) {
        if (preferencesName == null) {
            return false;
        }
        return new File(Environment.getDataDirectory() + "/data/" + context.getPackageName() + "/shared_prefs/" + preferencesName + ".xml").delete();
    }

    public static boolean sharedPreferenceExists(Context context, String preferencesName) {
        if (preferencesName == null) {
            return false;
        }
        return new File(Environment.getDataDirectory() + "/data/" + context.getPackageName() + "/shared_prefs/" + preferencesName + ".xml").exists();
    }
}
