package com.jb.filemanager.function.daemon;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.jb.filemanager.BuildConfig;
import com.jiubang.commerce.daemon.DaemonClient;
import com.jiubang.commerce.daemon.NotificationAssistService;

/**
 * Created by nieyh on 2016/10/12.
 *
 */
public class DaemonService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Toast.makeText(this, "DaemonService", Toast.LENGTH_SHORT).show();
        }
        //设置为前台服务，降低被杀几率。参数里的两个服务必须配置在同一进程内。
//        DaemonClient.getInstance().setForgroundService(this, InnerDaemonNotifService.class);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        //统计守护效果
        // DaemonClient.getInstance().statisticsDaemonEffect(this, intent);

        /**
         * 返回{@link Service#START_STICKY}，kill命令杀进程后可快速启动
         */
        return START_STICKY;
    }

    /**
     * 内部服务，用于设置前台进程
     */
    public static class InnerDaemonNotifService extends NotificationAssistService {
    }
}
