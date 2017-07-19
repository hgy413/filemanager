package com.jb.filemanager.function.recent.presenter;

import com.jb.filemanager.function.recent.bean.BlockBean;

import java.util.List;

/**
 * Created by xiaoyu on 2017/7/17 14:28.
 */

public interface RecentFileContract {
    interface View {
        void setListViewData(List<BlockBean> data);
        void notifyListDataChanged();
        void switchSelectMode(boolean isToSelectMode);
        void setSearchTitleSelectBtnState(int state);
        void setSearchTitleSelectCount(int count);
    }
    interface Presenter {
        void onCreate();
        void onDestroy();
        void onTitleCancelBtnClick();
        void onTitleSelectBtnClick();
    }
}
