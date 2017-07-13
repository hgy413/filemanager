package com.jb.filemanager.function.applock.view;

import android.view.View;

import java.util.List;

/**
 * Created by nieyh on 17-7-12.
 */

public interface IFloatAppLockerViewEvtListener {
    void onBackPress();

    void onForgetClick(View v);

    void onDonLockApp(View v);

    void onInputCompleted(List<PatternView.Cell> cellList, String[] numbers);

    void onHomeClick();
}
