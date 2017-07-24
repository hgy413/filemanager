package com.jb.filemanager.home.dialog;

import android.app.Activity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.ui.dialog.FMBaseDialog;

/**
 * Created by nieyh on 17-7-24.
 */

public class IntroduceChargeDialog extends FMBaseDialog {

    private TextView mOk;

    public IntroduceChargeDialog(Activity act) {
        super(act, true);
        View root = LayoutInflater.from(act).inflate(R.layout.dialog_introduce_charge, null);
        mOk = (TextView) root.findViewById(R.id.dialog_introduce_charge_ok);
        setContentView(root);
//        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getContext().getResources().getDisplayMetrics());
//        setSize(getContext().getResources().getDisplayMetrics().widthPixels - 2 * margin, WindowManager.LayoutParams.WRAP_CONTENT);
        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
