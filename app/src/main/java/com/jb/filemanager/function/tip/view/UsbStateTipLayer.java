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
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                TheApplication.getAppContext().startActivity(intent);
            }
        });
    }
}
