package com.jb.filemanager.function.trashfiles.presenter;

/**
 * Created by xiaoyu on 2017/2/3 15:07.
 */

public interface Contract {

    interface ICleanMainView {
        /**
         * 正在扫描文件
         */
        void onFileScanning();

        /**
         * 文件扫描结束
         */
        void onFileScanFinish();

        /**
         * 更新列表
         */
        void notifyDataSetChanged();

        /**
         * 展开指定组
         *
         * @param index 组的下标
         */
        void expandGroup(int index);

        /**
         * 开始删除任务
         */
        void onDeleteStart();

        /**
         * 更新扫描进度条
         */
        void updateProgress(float process);

        /**
         * 删除任务完成
         */
        void onDeleteFinish();
    }

    interface Presenter {

    }
}
