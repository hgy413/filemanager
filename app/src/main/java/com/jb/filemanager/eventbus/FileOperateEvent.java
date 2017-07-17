package com.jb.filemanager.eventbus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

/**
 * Desc: 底部栏对文件进行了操作后的事件
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/14 18:19
 */

public class FileOperateEvent {
    public enum OperateType {
        COPY, CUT, DELETE, RENAME, ERROR
    }

    public OperateType mOperateType;
    public File mOldFile;
    public File mNewFile;

    /**
     * @param oldFile 操作之前的文件
     * @param newFile 操作之后的文件 (粘贴的时候是粘贴结果的那个文件,剪切的时候传剪切后的文件,
     *                删除传null,重命名传命名后的文件)
     * @param type    操作类型
     */
    public FileOperateEvent(@Nullable File oldFile, @Nullable File newFile, @NonNull OperateType type) {
        this.mOldFile = oldFile;
        this.mNewFile = newFile;
        this.mOperateType = type;
    }
}
