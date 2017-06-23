package com.jb.filemanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

/**
 * Created by bill wang on 2017/6/21.
 */

public abstract class BaseBroadcastReceiver extends BroadcastReceiver {

    private boolean isRegistered = false;

    /**
     *desc: 注册广播的方法
     *author lqf
     *email: liqf@m15.cn
     *created 2016/12/27 14:24
     */
    public void register(Context context, IntentFilter filter) {
        if (!isRegistered && context != null && filter != null) {
            context.registerReceiver(this, filter);
            isRegistered = true;
        }
    }

    /**
     *desc: 解除注册的方法
     *author lqf
     *email: liqf@m15.cn
     *created 2016/12/27 14:24
     */
    public void unregister(Context context) {
        if (isRegistered && context != null) {
            context.unregisterReceiver(this);  // edited
            isRegistered = false;
        }
    }

    /**
     *desc: 返回是否注册过
     *@return
     *author lqf
     *email: liqf@m15.cn
     *created 2016/12/27 14:32
     */
    public boolean isRegistered() {
        return isRegistered;
    }

}
