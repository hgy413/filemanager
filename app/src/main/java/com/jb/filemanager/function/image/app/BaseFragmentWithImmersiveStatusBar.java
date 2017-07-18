package com.jb.filemanager.function.image.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.jb.filemanager.BaseFragment;
import com.jb.filemanager.util.DrawUtils;
import com.jb.filemanager.util.WindowUtil;
import com.jb.filemanager.util.device.Machine;

/**
 * Created by bill wang on 16/9/21.
 *
 */

public class BaseFragmentWithImmersiveStatusBar extends BaseFragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setImmersiveStatusBar(view);
    }

    /**
     * 设置沉浸式状态栏
     */
    protected void setImmersiveStatusBar(View root) {
        //沉浸式状态栏 fragment不能使用fitSystemWindows
        if (Machine.HAS_SDK_KITKAT) {
            //设置顶部内边距 分隔开内容与顶部
            int paddingTop;
            try {
                paddingTop = WindowUtil.getStatusBarHeight();
            } catch (Exception ex) {
                paddingTop = DrawUtils.dip2px(25);
            }
            root.setPadding(0, paddingTop, 0, 0);
        }
    }
}
