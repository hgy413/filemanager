package com.jb.filemanager.function.zipfile;

import com.jb.filemanager.function.zipfile.bean.ZipFileGroup;
import com.jb.filemanager.function.zipfile.bean.ZipFileItem;

import java.util.List;

/**
 * Created by xiaoyu on 2017/6/30 10:10.
 */

public interface ZipActivityContract {
    interface View {
        void setWidgetsState(boolean isLoading);
        void setListData(List<ZipFileGroup> data);
        void notifyDataSetChanged();
        void showOperationDialog(ZipFileItem fileItem);
    }
    interface Presenter{
        void onCreate();
        void onItemClick(int groupPosition, int childPosition);
        void extractZipFile(ZipFileItem fileItem);
        void onDestroy();
    }
}
