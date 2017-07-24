package com.jb.filemanager.function.tip.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.trash.CleanTrashActivity;
import com.jb.filemanager.home.MainActivity;

/**
 * Created by nieyh on 17-7-17.
 */

public class UsbStateTipLayer extends TipLayer {

    public UsbStateTipLayer(@NonNull Context context) {
        super(context, TipLayer.USB_STATE_TIP_LAYER);
        setIcon(R.drawable.ic_usb_tip);
        setContentTxt(R.string.tip_usb_state_content);
        setBtuTxt(R.string.tip_usb_state_btu);
        setBtuActionListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
//                Intent intent = new Intent(getContext(), MainActivity.class);
                //方案 1
                //直接跳转首页 并移除首页之上的其他页面
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //No_Animation 只能去除Activity之间的跳转的动画 不能去除New_Task创建动画
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                TheApplication.getAppContext().startActivity(intent);

                //方案 2
                //直接打开历史记录栈顶的页面 -- 拉起应用
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setClass(TheApplication.getAppContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                TheApplication.getAppContext().startActivity(intent);
            }
        });
    }
}
