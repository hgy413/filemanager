package com.jb.filemanager.function.trashignore.contract;


import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreGroupBean;

import java.util.List;

/**
 * Created by xiaoyu on 2017/2/28 11:32.
 */

public interface Contract {
    interface View {
        void showSuitableView(boolean isHaveData);
        void setListData(List<CleanIgnoreGroupBean> data);
        void notifyListDataSetChanged();
    }
    interface Presenter {
        void onEnterActivity();
        void onExitActivity();
    }
    interface Support {
        void startObtainData(Runnable runnable);
        List<CleanIgnoreGroupBean> getDataFromDb();
        void onExitFromDb();
    }
}
