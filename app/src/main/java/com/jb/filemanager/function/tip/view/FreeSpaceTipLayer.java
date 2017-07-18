package com.jb.filemanager.function.tip.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.trash.CleanTrashActivity;

/**
 * Created by nieyh on 17-7-17.
 */

public class FreeSpaceTipLayer extends TipLayer {

    public FreeSpaceTipLayer(@NonNull Context context) {
        super(context, TipLayer.FREE_SPACE_TIP_LAYER);
        setIcon(R.drawable.ic_free_space);
        setContentTxt(R.string.tip_free_space_content);
        setBtuTxt(R.string.home_button_clean);
        setBtuActionListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
                Intent intent = new Intent(getContext(), CleanTrashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                TheApplication.getAppContext().startActivity(intent);
            }
        });
    }

}
