package com.jb.filemanager.function.rate.presenter;

import com.jb.filemanager.function.rate.RateManager;

/**
 * Created by nieyh on 17-7-19.
 */

public class RateSupport implements RateContract.Support {

    @Override
    public void setRateSuccess() {
        RateManager.getsInstance().commitRateSuccess();
    }

    @Override
    public boolean isCanShow() {
        return RateManager.getsInstance().isReachTriggerCondition();
    }
}
