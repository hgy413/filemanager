package com.jiubang.commerce.ad.install;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.jiubang.commerce.receiver.AppBroadcastReceiver;
import java.util.ArrayList;
import java.util.List;

public class InstallBroadcaster {
    private static InstallBroadcaster sInstance;
    private Context mContext;
    private List<IInstallListener> mInstallListeners = new ArrayList();
    private InstallReceiver mInstallReceiver;

    public interface IInstallListener {
        void onPackageInstalled(String str, Intent intent);
    }

    private InstallBroadcaster(Context context) {
        this.mContext = context;
    }

    public static synchronized InstallBroadcaster getInstance(Context context) {
        InstallBroadcaster installBroadcaster;
        synchronized (InstallBroadcaster.class) {
            if (sInstance == null) {
                sInstance = new InstallBroadcaster(context);
            }
            installBroadcaster = sInstance;
        }
        return installBroadcaster;
    }

    public void registerListener(IInstallListener listener) {
        if (listener != null && !this.mInstallListeners.contains(listener)) {
            if (this.mInstallListeners.isEmpty()) {
                registerInstallReceiver();
            }
            this.mInstallListeners.add(listener);
        }
    }

    public void unregisterListener(IInstallListener listener) {
        if (this.mInstallListeners.contains(listener)) {
            this.mInstallListeners.remove(listener);
            if (this.mInstallListeners.isEmpty()) {
                unregisterInstallReceiver();
            }
        }
    }

    private void registerInstallReceiver() {
        if (this.mInstallReceiver == null) {
            this.mInstallReceiver = new InstallReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addDataScheme(AppBroadcastReceiver.DATA_SCHEME);
        this.mContext.registerReceiver(this.mInstallReceiver, filter);
    }

    private void unregisterInstallReceiver() {
        if (this.mInstallReceiver != null) {
            this.mContext.unregisterReceiver(this.mInstallReceiver);
            this.mInstallReceiver = null;
        }
    }

    /* access modifiers changed from: private */
    public void informListeners(String packageName, Intent intent) {
        for (IInstallListener listener : this.mInstallListeners) {
            if (listener != null) {
                listener.onPackageInstalled(packageName, intent);
            }
        }
    }

    private class InstallReceiver extends BroadcastReceiver {
        private InstallReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.PACKAGE_ADDED".equals(intent.getAction())) {
                String packageName = intent.getDataString();
                int index = packageName.indexOf("package:");
                if (index >= 0) {
                    packageName = packageName.substring("package:".length() + index);
                }
                InstallBroadcaster.this.informListeners(packageName, intent);
            }
        }
    }
}
