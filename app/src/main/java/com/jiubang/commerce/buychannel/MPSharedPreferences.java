package com.jiubang.commerce.buychannel;

import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.DeadObjectException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MPSharedPreferences extends ContentProvider implements SharedPreferences {
    private static final int APPLY = 8;
    private static final int COMMIT = 9;
    private static final int CONTAINS = 7;
    private static final Object CONTENT = new Object();
    public static final boolean DEBUG = false;
    private static final int GET_ALL = 1;
    private static final int GET_BOOLEAN = 6;
    private static final int GET_FLOAT = 5;
    private static final int GET_INT = 3;
    private static final int GET_LONG = 4;
    private static final int GET_STRING = 2;
    private static final String KEY = "value";
    private static final String KEY_NAME = "name";
    private static final String PATH_APPLY = "apply";
    private static final String PATH_COMMIT = "commit";
    private static final String PATH_CONTAINS = "contains";
    private static final String PATH_GET_ALL = "getAll";
    private static final String PATH_GET_BOOLEAN = "getBoolean";
    private static final String PATH_GET_FLOAT = "getFloat";
    private static final String PATH_GET_INT = "getInt";
    private static final String PATH_GET_LONG = "getLong";
    private static final String PATH_GET_STRING = "getString";
    private static final String PATH_REGISTER_ON_SHARED_PREFERENCE_CHANGE_LISTENER = "registerOnSharedPreferenceChangeListener";
    private static final String PATH_UNREGISTER_ON_SHARED_PREFERENCE_CHANGE_LISTENER = "unregisterOnSharedPreferenceChangeListener";
    private static final String PATH_WILDCARD = "*/";
    private static final int REGISTER_ON_SHARED_PREFERENCE_CHANGE_LISTENER = 10;
    private static final String TAG = "MPSharedPreferences";
    private static final int UNREGISTER_ON_SHARED_PREFERENCE_CHANGE_LISTENER = 11;
    private static String sAuthoriry;
    /* access modifiers changed from: private */
    public static volatile Uri sAuthorityUrl;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public boolean mIsSafeMode;
    /* access modifiers changed from: private */
    public HashMap<SharedPreferences.OnSharedPreferenceChangeListener, Object> mListeners;
    private HashMap<String, Integer> mListenersCount;
    /* access modifiers changed from: private */
    public int mMode;
    /* access modifiers changed from: private */
    public String mName;
    private BroadcastReceiver mReceiver;
    private UriMatcher mUriMatcher;

    private static class ReflectionUtil {
        private ReflectionUtil() {
        }

        public static ContentValues contentValuesNewInstance(HashMap<String, Object> values) {
            try {
                Constructor<ContentValues> c = ContentValues.class.getDeclaredConstructor(new Class[]{HashMap.class});
                c.setAccessible(true);
                return c.newInstance(new Object[]{values});
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e2) {
                throw new RuntimeException(e2);
            } catch (InvocationTargetException e3) {
                throw new RuntimeException(e3);
            } catch (NoSuchMethodException e4) {
                throw new RuntimeException(e4);
            } catch (InstantiationException e5) {
                throw new RuntimeException(e5);
            }
        }

        public static SharedPreferences.Editor editorPutStringSet(SharedPreferences.Editor editor, String key, Set<String> values) {
            try {
                Class<?> cls = editor.getClass();
                Class[] clsArr = new Class[MPSharedPreferences.GET_STRING];
                clsArr[0] = String.class;
                clsArr[1] = Set.class;
                Method method = cls.getDeclaredMethod("putStringSet", clsArr);
                Object[] objArr = new Object[MPSharedPreferences.GET_STRING];
                objArr[0] = key;
                objArr[1] = values;
                return (SharedPreferences.Editor) method.invoke(editor, objArr);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e2) {
                throw new RuntimeException(e2);
            } catch (InvocationTargetException e3) {
                throw new RuntimeException(e3);
            } catch (NoSuchMethodException e4) {
                throw new RuntimeException(e4);
            }
        }

        public static void editorApply(SharedPreferences.Editor editor) {
            try {
                editor.getClass().getDeclaredMethod(MPSharedPreferences.PATH_APPLY, new Class[0]).invoke(editor, new Object[0]);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e2) {
                throw new RuntimeException(e2);
            } catch (InvocationTargetException e3) {
                throw new RuntimeException(e3);
            } catch (NoSuchMethodException e4) {
                throw new RuntimeException(e4);
            }
        }
    }

    private boolean isSafeMode(Context context) {
        try {
            return context.getPackageManager().isSafeMode();
        } catch (RuntimeException e) {
            if (isPackageManagerHasDied(e)) {
                return false;
            }
            throw e;
        }
    }

    /* access modifiers changed from: private */
    public void checkInitAuthority(Context context) {
        if (sAuthorityUrl == null) {
            synchronized (this) {
                if (sAuthorityUrl == null) {
                    PackageInfo packageInfos = null;
                    try {
                        packageInfos = context.getPackageManager().getPackageInfo(context.getPackageName(), APPLY);
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                    if (packageInfos != null) {
                        if (packageInfos.providers != null) {
                            ProviderInfo[] providerInfoArr = packageInfos.providers;
                            int length = providerInfoArr.length;
                            int i = 0;
                            while (true) {
                                if (i >= length) {
                                    break;
                                }
                                ProviderInfo providerInfo = providerInfoArr[i];
                                if (providerInfo.name.equals(MPSharedPreferences.class.getName())) {
                                    sAuthoriry = providerInfo.authority;
                                    break;
                                }
                                i++;
                            }
                        }
                    }
                    if (sAuthoriry == null) {
                        throw new IllegalArgumentException("'AUTHORITY' initialize failed, Unable to find explicit provider class " + MPSharedPreferences.class.getName() + "; have you declared this provider in your AndroidManifest.xml?");
                    }
                    sAuthorityUrl = Uri.parse("content://" + sAuthoriry);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean isPackageManagerHasDied(Exception e) {
        return (e instanceof RuntimeException) && e.getMessage() != null && e.getMessage().contains("Package manager has died") && e.getCause() != null && (e.getCause() instanceof DeadObjectException);
    }

    public static SharedPreferences getSharedPreferences(Context context, String name, int mode) {
        return new MPSharedPreferences(context.getApplicationContext(), name, mode);
    }

    @Deprecated
    public MPSharedPreferences() {
    }

    private MPSharedPreferences(Context context, String name, int mode) {
        this.mContext = context;
        this.mName = name;
        this.mMode = mode;
        this.mIsSafeMode = isSafeMode(this.mContext);
    }

    public Map<String, ?> getAll() {
        Map<String, ?> v = (Map) getValue(PATH_GET_ALL, (String) null, (Object) null);
        return v != null ? v : new HashMap<>();
    }

    public String getString(String key, String defValue) {
        return (String) getValue(PATH_GET_STRING, key, defValue);
    }

    public Set<String> getStringSet(String key, Set<String> defValues) {
        return (Set) getValue(PATH_GET_STRING, key, defValues);
    }

    public int getInt(String key, int defValue) {
        return ((Integer) getValue(PATH_GET_INT, key, Integer.valueOf(defValue))).intValue();
    }

    public long getLong(String key, long defValue) {
        return ((Long) getValue(PATH_GET_LONG, key, Long.valueOf(defValue))).longValue();
    }

    public float getFloat(String key, float defValue) {
        return ((Float) getValue(PATH_GET_FLOAT, key, Float.valueOf(defValue))).floatValue();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return ((Boolean) getValue(PATH_GET_BOOLEAN, key, Boolean.valueOf(defValue))).booleanValue();
    }

    public boolean contains(String key) {
        return ((Boolean) getValue(PATH_CONTAINS, key, (Object) null)).booleanValue();
    }

    public SharedPreferences.Editor edit() {
        return new EditorImpl();
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        synchronized (this) {
            if (this.mListeners == null) {
                this.mListeners = new HashMap<>();
            }
            Boolean result = (Boolean) getValue(PATH_REGISTER_ON_SHARED_PREFERENCE_CHANGE_LISTENER, (String) null, false);
            if (result != null && result.booleanValue()) {
                this.mListeners.put(listener, CONTENT);
                if (this.mReceiver == null) {
                    this.mReceiver = new BroadcastReceiver() {
                        public void onReceive(Context context, Intent intent) {
                            String name = intent.getStringExtra(MPSharedPreferences.KEY_NAME);
                            List<String> keysModified = (List) intent.getSerializableExtra(MPSharedPreferences.KEY);
                            if (MPSharedPreferences.this.mName.equals(name) && keysModified != null) {
                                Set<SharedPreferences.OnSharedPreferenceChangeListener> listeners = new HashSet<>(MPSharedPreferences.this.mListeners.keySet());
                                for (int i = keysModified.size() - 1; i >= 0; i--) {
                                    String key = keysModified.get(i);
                                    for (SharedPreferences.OnSharedPreferenceChangeListener listener : listeners) {
                                        if (listener != null) {
                                            listener.onSharedPreferenceChanged(MPSharedPreferences.this, key);
                                        }
                                    }
                                }
                            }
                        }
                    };
                    this.mContext.registerReceiver(this.mReceiver, new IntentFilter(makeAction(this.mName)));
                }
            }
        }
    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        synchronized (this) {
            getValue(PATH_UNREGISTER_ON_SHARED_PREFERENCE_CHANGE_LISTENER, (String) null, false);
            if (this.mListeners != null) {
                this.mListeners.remove(listener);
                if (this.mListeners.isEmpty() && this.mReceiver != null) {
                    this.mContext.unregisterReceiver(this.mReceiver);
                }
            }
        }
    }

    public final class EditorImpl implements SharedPreferences.Editor {
        private boolean mClear = false;
        private final Map<String, Object> mModified = new HashMap();

        public EditorImpl() {
        }

        public SharedPreferences.Editor putString(String key, String value) {
            synchronized (this) {
                this.mModified.put(key, value);
            }
            return this;
        }

        public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
            synchronized (this) {
                this.mModified.put(key, values == null ? null : new HashSet(values));
            }
            return this;
        }

        public SharedPreferences.Editor putInt(String key, int value) {
            synchronized (this) {
                this.mModified.put(key, Integer.valueOf(value));
            }
            return this;
        }

        public SharedPreferences.Editor putLong(String key, long value) {
            synchronized (this) {
                this.mModified.put(key, Long.valueOf(value));
            }
            return this;
        }

        public SharedPreferences.Editor putFloat(String key, float value) {
            synchronized (this) {
                this.mModified.put(key, Float.valueOf(value));
            }
            return this;
        }

        public SharedPreferences.Editor putBoolean(String key, boolean value) {
            synchronized (this) {
                this.mModified.put(key, Boolean.valueOf(value));
            }
            return this;
        }

        public SharedPreferences.Editor remove(String key) {
            synchronized (this) {
                this.mModified.put(key, (Object) null);
            }
            return this;
        }

        public SharedPreferences.Editor clear() {
            synchronized (this) {
                this.mClear = true;
            }
            return this;
        }

        public void apply() {
            setValue(MPSharedPreferences.PATH_APPLY);
        }

        public boolean commit() {
            return setValue(MPSharedPreferences.PATH_COMMIT);
        }

        private boolean setValue(String pathSegment) {
            boolean result = false;
            if (MPSharedPreferences.this.mIsSafeMode) {
                return false;
            }
            try {
                MPSharedPreferences.this.checkInitAuthority(MPSharedPreferences.this.mContext);
                String[] selectionArgs = new String[MPSharedPreferences.GET_STRING];
                selectionArgs[0] = String.valueOf(MPSharedPreferences.this.mMode);
                selectionArgs[1] = String.valueOf(this.mClear);
                synchronized (this) {
                    try {
                        if (MPSharedPreferences.this.mContext.getContentResolver().update(Uri.withAppendedPath(Uri.withAppendedPath(MPSharedPreferences.sAuthorityUrl, MPSharedPreferences.this.mName), pathSegment), ReflectionUtil.contentValuesNewInstance((HashMap) this.mModified), (String) null, selectionArgs) > 0) {
                            result = true;
                        } else {
                            result = false;
                        }
                    } catch (IllegalArgumentException e) {
                    } catch (RuntimeException e2) {
                        if (MPSharedPreferences.this.isPackageManagerHasDied(e2)) {
                            return false;
                        }
                        throw e2;
                    }
                }
                boolean z = result;
                return result;
            } catch (RuntimeException e3) {
                if (MPSharedPreferences.this.isPackageManagerHasDied(e3)) {
                    return false;
                }
                throw e3;
            }
        }
    }

    private Object getValue(String pathSegment, String key, Object defValue) {
        Object defValue2;
        String str = null;
        Object v = null;
        if (this.mIsSafeMode) {
            return defValue;
        }
        try {
            checkInitAuthority(this.mContext);
            Uri uri = Uri.withAppendedPath(Uri.withAppendedPath(sAuthorityUrl, this.mName), pathSegment);
            String[] selectionArgs = new String[GET_INT];
            selectionArgs[0] = String.valueOf(this.mMode);
            selectionArgs[1] = key;
            if (defValue != null) {
                str = String.valueOf(defValue);
            }
            selectionArgs[GET_STRING] = str;
            Cursor cursor = null;
            try {
                cursor = this.mContext.getContentResolver().query(uri, (String[]) null, (String) null, selectionArgs, (String) null);
            } catch (SecurityException e) {
            } catch (RuntimeException e2) {
                if (isPackageManagerHasDied(e2)) {
                    return defValue;
                }
                throw e2;
            }
            if (cursor != null) {
                Bundle bundle = null;
                try {
                    bundle = cursor.getExtras();
                } catch (RuntimeException e3) {
                }
                if (bundle != null) {
                    v = bundle.get(KEY);
                    bundle.clear();
                }
                cursor.close();
                defValue2 = v;
            } else {
                defValue2 = null;
            }
            if (defValue2 != null) {
                return defValue2;
            }
            return defValue;
        } catch (RuntimeException e4) {
            if (isPackageManagerHasDied(e4)) {
                return defValue;
            }
            throw e4;
        }
    }

    private String makeAction(String name) {
        Object[] objArr = new Object[GET_STRING];
        objArr[0] = MPSharedPreferences.class.getName();
        objArr[1] = name;
        return String.format("%1$s_%2$s", objArr);
    }

    public boolean onCreate() {
        checkInitAuthority(getContext());
        this.mUriMatcher = new UriMatcher(-1);
        this.mUriMatcher.addURI(sAuthoriry, "*/getAll", 1);
        this.mUriMatcher.addURI(sAuthoriry, "*/getString", GET_STRING);
        this.mUriMatcher.addURI(sAuthoriry, "*/getInt", GET_INT);
        this.mUriMatcher.addURI(sAuthoriry, "*/getLong", GET_LONG);
        this.mUriMatcher.addURI(sAuthoriry, "*/getFloat", 5);
        this.mUriMatcher.addURI(sAuthoriry, "*/getBoolean", GET_BOOLEAN);
        this.mUriMatcher.addURI(sAuthoriry, "*/contains", CONTAINS);
        this.mUriMatcher.addURI(sAuthoriry, "*/apply", APPLY);
        this.mUriMatcher.addURI(sAuthoriry, "*/commit", COMMIT);
        this.mUriMatcher.addURI(sAuthoriry, "*/registerOnSharedPreferenceChangeListener", REGISTER_ON_SHARED_PREFERENCE_CHANGE_LISTENER);
        this.mUriMatcher.addURI(sAuthoriry, "*/unregisterOnSharedPreferenceChangeListener", UNREGISTER_ON_SHARED_PREFERENCE_CHANGE_LISTENER);
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String name = uri.getPathSegments().get(0);
        int mode = Integer.parseInt(selectionArgs[0]);
        String key = selectionArgs[1];
        String defValue = selectionArgs[GET_STRING];
        Bundle bundle = new Bundle();
        switch (this.mUriMatcher.match(uri)) {
            case 1:
                bundle.putSerializable(KEY, (HashMap) getContext().getSharedPreferences(name, mode).getAll());
                break;
            case GET_STRING /*2*/:
                bundle.putString(KEY, getContext().getSharedPreferences(name, mode).getString(key, defValue));
                break;
            case GET_INT /*3*/:
                bundle.putInt(KEY, getContext().getSharedPreferences(name, mode).getInt(key, Integer.parseInt(defValue)));
                break;
            case GET_LONG /*4*/:
                bundle.putLong(KEY, getContext().getSharedPreferences(name, mode).getLong(key, Long.parseLong(defValue)));
                break;
            case 5:
                bundle.putFloat(KEY, getContext().getSharedPreferences(name, mode).getFloat(key, Float.parseFloat(defValue)));
                break;
            case GET_BOOLEAN /*6*/:
                bundle.putBoolean(KEY, getContext().getSharedPreferences(name, mode).getBoolean(key, Boolean.parseBoolean(defValue)));
                break;
            case CONTAINS /*7*/:
                bundle.putBoolean(KEY, getContext().getSharedPreferences(name, mode).contains(key));
                break;
            case REGISTER_ON_SHARED_PREFERENCE_CHANGE_LISTENER /*10*/:
                checkInitListenersCount();
                Integer countInteger = this.mListenersCount.get(name);
                int count = (countInteger == null ? 0 : countInteger.intValue()) + 1;
                this.mListenersCount.put(name, Integer.valueOf(count));
                Integer countInteger2 = this.mListenersCount.get(name);
                bundle.putBoolean(KEY, count == (countInteger2 == null ? 0 : countInteger2.intValue()));
                break;
            case UNREGISTER_ON_SHARED_PREFERENCE_CHANGE_LISTENER /*11*/:
                checkInitListenersCount();
                Integer countInteger3 = this.mListenersCount.get(name);
                int count2 = (countInteger3 == null ? 0 : countInteger3.intValue()) - 1;
                if (count2 > 0) {
                    this.mListenersCount.put(name, Integer.valueOf(count2));
                    Integer countInteger4 = this.mListenersCount.get(name);
                    bundle.putBoolean(KEY, count2 == (countInteger4 == null ? 0 : countInteger4.intValue()));
                    break;
                } else {
                    this.mListenersCount.remove(name);
                    bundle.putBoolean(KEY, !this.mListenersCount.containsKey(name));
                    break;
                }
            default:
                throw new IllegalArgumentException("This is Unknown Uri：" + uri);
        }
        return new BundleCursor(bundle);
    }

    public String getType(Uri uri) {
        throw new UnsupportedOperationException("No external call");
    }

    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("No external insert");
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("No external delete");
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int result = 0;
        String name = uri.getPathSegments().get(0);
        SharedPreferences preferences = getContext().getSharedPreferences(name, Integer.parseInt(selectionArgs[0]));
        int match = this.mUriMatcher.match(uri);
        switch (match) {
            case APPLY /*8*/:
            case COMMIT /*9*/:
                boolean hasListeners = (this.mListenersCount == null || this.mListenersCount.get(name) == null || this.mListenersCount.get(name).intValue() <= 0) ? false : true;
                ArrayList<String> keysModified = null;
                Map<String, ?> map = null;
                if (hasListeners) {
                    keysModified = new ArrayList<>();
                    map = preferences.getAll();
                }
                SharedPreferences.Editor editor = preferences.edit();
                if (Boolean.parseBoolean(selectionArgs[1])) {
                    if (hasListeners && !map.isEmpty()) {
                        for (Map.Entry<String, ?> entry : map.entrySet()) {
                            keysModified.add(entry.getKey());
                        }
                    }
                    editor.clear();
                }
                for (Map.Entry<String, Object> entry2 : values.valueSet()) {
                    String k = entry2.getKey();
                    Object v = entry2.getValue();
                    if ((v instanceof EditorImpl) || v == null) {
                        editor.remove(k);
                        if (hasListeners && map.containsKey(k)) {
                            keysModified.add(k);
                        }
                    } else if (hasListeners && (!map.containsKey(k) || (map.containsKey(k) && !v.equals(map.get(k))))) {
                        keysModified.add(k);
                    }
                    if (v instanceof String) {
                        editor.putString(k, (String) v);
                    } else if (v instanceof Set) {
                        ReflectionUtil.editorPutStringSet(editor, k, (Set) v);
                    } else if (v instanceof Integer) {
                        editor.putInt(k, ((Integer) v).intValue());
                    } else if (v instanceof Long) {
                        editor.putLong(k, ((Long) v).longValue());
                    } else if (v instanceof Float) {
                        editor.putFloat(k, ((Float) v).floatValue());
                    } else if (v instanceof Boolean) {
                        editor.putBoolean(k, ((Boolean) v).booleanValue());
                    }
                }
                if (!hasListeners || !keysModified.isEmpty()) {
                    switch (match) {
                        case APPLY /*8*/:
                            ReflectionUtil.editorApply(editor);
                            result = 1;
                            notifyListeners(name, keysModified);
                            break;
                        case COMMIT /*9*/:
                            if (editor.commit()) {
                                result = 1;
                                notifyListeners(name, keysModified);
                                break;
                            }
                            break;
                    }
                } else {
                    result = 1;
                }
                values.clear();
                return result;
            default:
                throw new IllegalArgumentException("This is Unknown Uri：" + uri);
        }
    }

    private void checkInitListenersCount() {
        if (this.mListenersCount == null) {
            this.mListenersCount = new HashMap<>();
        }
    }

    private void notifyListeners(String name, ArrayList<String> keysModified) {
        if (keysModified != null && !keysModified.isEmpty()) {
            Intent intent = new Intent();
            intent.setAction(makeAction(name));
            intent.setPackage(getContext().getPackageName());
            intent.putExtra(KEY_NAME, name);
            intent.putExtra(KEY, keysModified);
            getContext().sendBroadcast(intent);
        }
    }

    private static final class BundleCursor extends MatrixCursor {
        private Bundle mBundle;

        public BundleCursor(Bundle extras) {
            super(new String[0], 0);
            this.mBundle = extras;
        }

        public Bundle getExtras() {
            return this.mBundle;
        }

        public Bundle respond(Bundle extras) {
            this.mBundle = extras;
            return this.mBundle;
        }
    }
}
