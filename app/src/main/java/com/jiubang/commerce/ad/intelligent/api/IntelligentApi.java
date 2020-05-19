package com.jiubang.commerce.ad.intelligent.api;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import com.jb.ga0.commerce.util.thread.CustomThreadExecutorProxy;
import com.jiubang.commerce.ad.bean.AdInfoBean;
import com.jiubang.commerce.dyload.manager.DyManager;
import com.jiubang.commerce.dyload.manager.IPluginLoadListener;

public class IntelligentApi {
    public static final String COMMAND = "command";
    public static final String COMMAND_EXIT = "exit";
    public static final String COMMAND_GOMOAD_CLICK = "com_gomoad_click";
    public static final String COMMAND_NATIVE_AD_PRESOLVE = "native_ad_presolve";
    public static final String COMMAND_ON_GP_CLOSE = "onGPClose";
    public static final String COMMAND_ON_GP_OPEN = "onGPOpen";
    public static final String COMMAND_PARAM = "command_param";
    public static final String COMMAND_SELF_OPEN_GP = "self_gp";
    public static final String COMMAND_SET_GAID = "set_gaid";
    public static final String PACKAGE_NAME = "com.jiubang.commerce.ad.intelligent";
    public static final String PROCESS_SUFFIX = ":com.jiubang.commerce.service.IntelligentPreloadService";
    public static final String TAG = "IntelligentPreload";
    private static String sIntellProcessName = null;
    private static PluginLoadListener sPluginLoadListener = null;
    /* access modifiers changed from: private */
    public static boolean sTestServer = false;

    public static void init(Context context, String cId, String entranceId, String dataChannel, String channel, String googleId) {
        final Context context2 = context;
        final String str = cId;
        final String str2 = entranceId;
        final String str3 = dataChannel;
        final String str4 = channel;
        final String str5 = googleId;
        CustomThreadExecutorProxy.getInstance().execute(new Runnable() {
            public void run() {
                IntelligentApi.initDyLoad(context2);
                final IIntelligentPreload impl = IntelligentApi.toInterface(context2);
                if (impl == null) {
                    LogUtils.e(IntelligentApi.TAG, "impl is null");
                    IntelligentApi.checkPluginLoadListener(context2, str, str2, str3, str4, str5);
                    return;
                }
                LogUtils.e(IntelligentApi.TAG, "impl is ok");
                if (LogUtils.isShowLog()) {
                    LogUtils.e(IntelligentApi.TAG, "open log");
                    impl.enableLog();
                }
                impl.setServer(IntelligentApi.sTestServer);
                CustomThreadExecutorProxy.getInstance().runOnMainThread(new Runnable() {
                    public void run() {
                        impl.init(context2, str, str2, str3, str4, str5);
                    }
                });
                IntelligentApi.checkPluginLoadListener(context2, str, str2, str3, str4, str5);
            }
        });
    }

    public static void startServiceWithCommand(final Context context, final String command, final String[] param) {
        if (sPluginLoadListener != null && COMMAND_SET_GAID.equals(command) && param != null && param.length > 0 && !TextUtils.isEmpty(param[0])) {
            sPluginLoadListener.refreshGoogleId(context, param[0]);
        }
        CustomThreadExecutorProxy.getInstance().execute(new Runnable() {
            public void run() {
                IIntelligentPreload impl = IntelligentApi.toInterface(context);
                if (impl != null) {
                    impl.startServiceWithCommand(context, command, param);
                } else {
                    NativePreHelper.passDyStartServiceWithCommand(context, command, param);
                }
            }
        });
    }

    public static void startNativeAdPresolve(Context context, String title, String adPos) {
        NativePreHelper.startNativeAdPresolve(context, title, adPos);
    }

    public static void informGomoAdClick(Context context, AdInfoBean bean) {
        if (context != null && bean != null && 35 == bean.getAdvDataSource()) {
            startServiceWithCommand(context, COMMAND_GOMOAD_CLICK, new String[]{bean.getPackageName(), bean.getAdUrl()});
        }
    }

    public static void configIntelligentPreload(final Context context, final boolean enable) {
        CustomThreadExecutorProxy.getInstance().execute(new Runnable() {
            public void run() {
                IIntelligentPreload impl = IntelligentApi.toInterface(context);
                if (impl != null) {
                    impl.configIntelligentPreload(context, enable);
                }
            }
        });
    }

    public static void setServer(final Context context, final boolean test) {
        CustomThreadExecutorProxy.getInstance().execute(new Runnable() {
            public void run() {
                boolean unused = IntelligentApi.sTestServer = test;
                IIntelligentPreload impl = IntelligentApi.toInterface(context);
                if (impl != null) {
                    impl.setServer(test);
                }
            }
        });
    }

    static void initDyLoad(Context context) {
        try {
            DyManager.getInstance(context).init();
        } catch (Throwable thr) {
            LogUtils.w("wbq", "initDyLoad", thr);
        }
    }

    static IIntelligentPreload toInterface(Context context) {
        try {
            Object obj = DyManager.getInstance(context).getPluginEntrance(PACKAGE_NAME);
            if (obj instanceof IIntelligentPreload) {
                return (IIntelligentPreload) obj;
            }
        } catch (Throwable thr) {
            LogUtils.w("wbq", "toInterface", thr);
        }
        return null;
    }

    static void checkPluginLoadListener(Context context, String cId, String entranceId, String dataChannel, String channel, String googleId) {
        try {
            if (sPluginLoadListener == null) {
                synchronized (IntelligentApi.class) {
                    if (sPluginLoadListener == null) {
                        sPluginLoadListener = new PluginLoadListener();
                        sPluginLoadListener.refreshParam(context, cId, entranceId, dataChannel, channel, googleId);
                        DyManager.getInstance(context).addPluginListener(sPluginLoadListener);
                    }
                }
            }
        } catch (Throwable thr) {
            LogUtils.w("wbq", "checkPluginLoadListener", thr);
        }
    }

    static void tryKillMyIntelligentProcess(Context context) {
        LogUtils.i(TAG, "tryKillMyIntelligentProcess");
        if (TextUtils.isEmpty(sIntellProcessName)) {
            sIntellProcessName = context.getPackageName() + PROCESS_SUFFIX;
        }
        try {
            for (ActivityManager.RunningServiceInfo info : ((ActivityManager) context.getSystemService("activity")).getRunningServices(Integer.MAX_VALUE)) {
                if (sIntellProcessName.equals(info.process) && Process.myPid() != info.pid) {
                    LogUtils.d(TAG, "myIntelligentProcess killed!");
                    Process.killProcess(info.pid);
                    return;
                }
            }
        } catch (Throwable e) {
            LogUtils.e(TAG, "killMyIntelligentProcess Error:", e);
        }
    }

    static class PluginLoadListener implements IPluginLoadListener {
        String mCId;
        String mChannel;
        Context mContext;
        String mDataChannel;
        String mEntranceId;
        String mGoogleId;

        PluginLoadListener() {
        }

        public void onPluginLoadStart(String s) {
        }

        public void onPluginLoadSuccess(String s) {
            LogUtils.d(IntelligentApi.TAG, "onPluginLoadSuccess=" + s);
            IntelligentApi.tryKillMyIntelligentProcess(this.mContext);
            if (IntelligentApi.PACKAGE_NAME.equals(s)) {
                CustomThreadExecutorProxy.getInstance().runOnMainThread(new Runnable() {
                    public void run() {
                        IntelligentApi.init(PluginLoadListener.this.mContext, PluginLoadListener.this.mCId, PluginLoadListener.this.mEntranceId, PluginLoadListener.this.mDataChannel, PluginLoadListener.this.mChannel, PluginLoadListener.this.mGoogleId);
                    }
                });
            }
        }

        public void onSdcardPluginFileError(String s, int i, String s1) {
        }

        public void onPluginLoadFailed(String s, int i, String s1) {
        }

        public void onAutoLoadPluginsStart() {
        }

        public void onAutoLoadPluginsFinish() {
        }

        /* access modifiers changed from: package-private */
        public void refreshParam(Context context, String cId, String entranceId, String dataChannel, String channel, String googleId) {
            this.mCId = cId;
            this.mEntranceId = entranceId;
            this.mDataChannel = dataChannel;
            this.mChannel = channel;
            refreshGoogleId(context, googleId);
        }

        /* access modifiers changed from: package-private */
        public void refreshGoogleId(Context context, String googleId) {
            if (this.mContext != null) {
                context = this.mContext;
            }
            this.mContext = context;
            this.mGoogleId = googleId;
        }
    }
}
