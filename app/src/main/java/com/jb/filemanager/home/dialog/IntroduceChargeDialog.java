package com.jb.filemanager.home.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.ui.dialog.FMBaseDialog;

/**
 * Created by nieyh on 17-7-24.
 */

public class IntroduceChargeDialog extends FMBaseDialog {

    private TextView mOk;

    private ImageView mSwitcher;

    public IntroduceChargeDialog(Activity act) {
        super(act, true);
        View root = LayoutInflater.from(act).inflate(R.layout.dialog_introduce_charge, null);
        mOk = (TextView) root.findViewById(R.id.dialog_introduce_charge_ok);
        mSwitcher = (ImageView) root.findViewById(R.id.dialog_introduce_charge_iv);
        setContentView(root);
        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mSwitcher.setSelected(SharedPreferencesManager.getInstance(TheApplication.getAppContext()).getBoolean(IPreferencesIds.KEY_SMART_CHARGE_ENABLE, false));
        mSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSelect = SharedPreferencesManager.getInstance(TheApplication.getAppContext()).getBoolean(IPreferencesIds.KEY_SMART_CHARGE_ENABLE, false);
                isSelect = !isSelect;
                SharedPreferencesManager.getInstance(TheApplication.getAppContext()).commitBoolean(IPreferencesIds.KEY_SMART_CHARGE_ENABLE, isSelect);
                v.setSelected(isSelect);
                if (isSelect) {
                    Toast.makeText(mActivity, R.string.toast_smart_charge_switch_enable, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, R.string.toast_smart_charge_switch_disable, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
