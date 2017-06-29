package com.jb.filemanager.function.trashignore.presenter;

import android.content.Context;

import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreGroupBean;
import com.jb.filemanager.function.trashignore.contract.Contract;
import com.jb.filemanager.function.trashignore.model.db.TrashIgnoreSupport;

import java.util.List;

/**
 * Created by xiaoyu on 2017/2/28 13:52.
 */

public class TrashIgnorePresenter implements Contract.Presenter {

    private Contract.View mView;
    private Contract.Support mSupport;

    public TrashIgnorePresenter(Contract.View view, Context context) {
        this.mView = view;
        this.mSupport = new TrashIgnoreSupport(context);
    }

    @Override
    public void onEnterActivity() {
        mSupport.startObtainData(new Runnable() {
            @Override
            public void run() {
                List<CleanIgnoreGroupBean> dataFromDb = mSupport.getDataFromDb();
                if (dataFromDb.size() > 0) {
                    mView.showSuitableView(true);
                    mView.setListData(dataFromDb);
                } else {
                    mView.showSuitableView(false);
                }
            }
        });
    }

    @Override
    public void onExitActivity() {
        if (mSupport != null) {
            mSupport.onExitFromDb();
        }
    }
}
