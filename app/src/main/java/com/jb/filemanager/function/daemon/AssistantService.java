package com.jb.filemanager.function.daemon;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.jiubang.commerce.daemon.DaemonClient;
import com.jiubang.commerce.daemon.NotificationAssistService;

/**
 * 此处不要做任何操作<br/>
 * <p/>
 * Created by nieyh on 8/22/16.
 */
public class AssistantService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();

        //设置为前台服务，降低被杀几率。参数里的两个服务必须配置在同一进程内。
//        DaemonClient.getInstance().setForgroundService(this, InnerAssistantNotifService.class);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /**
         * 返回{@link Service#START_STICKY}，kill命令杀进程后可快速启动
         */
        return Service.START_STICKY;
    }

    public static class InnerAssistantNotifService extends NotificationAssistService {

    }
}
