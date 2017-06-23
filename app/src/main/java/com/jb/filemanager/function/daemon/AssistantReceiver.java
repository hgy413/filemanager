package com.jb.filemanager.function.daemon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 此处不要做任何操作<br/>
 * <p/>
 * Created by nieyh on 8/22/16.
 */
public class AssistantReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //统计守护效果
        //DaemonClient.getInstance().statisticsDaemonEffect(this, intent);
    }
}
