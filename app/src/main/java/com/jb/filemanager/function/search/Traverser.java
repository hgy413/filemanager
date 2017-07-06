package com.jb.filemanager.function.search;

import android.animation.ValueAnimator;
import android.text.TextUtils;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.search.modle.FileInfo;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.StorageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by nieyh on 17-7-5.
 * 文件遍历器
 * 单纯只提供遍历所有路径
 */

public class Traverser {

    //文件夹深度 先指定深度 等待后续改变
    public static final int DEFAULT_DEEP_LIMIT = 6;
    //不限制深度
    public static final int INFINITE_DEEP_LIMIT = -1;

    private int mDeepLimit = DEFAULT_DEEP_LIMIT;

    public Traverser() {
        mDeepLimit = DEFAULT_DEEP_LIMIT;
    }

    public Traverser(int limit) {
        mDeepLimit = limit;
    }

    ArrayList<FileInfo> mFileInfoList;

    /**
     * 遍历外部文件系统 <br/>
     * @return 文件列表 {@link FileInfo}
     */
    public ArrayList<FileInfo> traverseExternalFileSystem() {
        mFileInfoList = new ArrayList<>();
        Set<String> storageSet = StorageUtil.getAllExternalPaths(TheApplication.getAppContext());
        Iterator<String> storageIterator = storageSet.iterator();
        while (storageIterator.hasNext()) {
            String path = storageIterator.next();
            if (!TextUtils.isEmpty(path)) {
                //遍历路径 发送遍历文件信息
                traverseFile(new File(path));
            }
        }
        //将扫描结果数据发送出去
        return mFileInfoList;
    }

    /**
     * 遍历指定文件
     *
     * @param file 路径
     */
    private void traverseFile(File file) {
        if (file == null) {
            return;
        }
        try {
            //隐藏文件不扫描
            if (file.isHidden()) {
                return;
            }
            FileInfo fileInfo = new FileInfo(file);
            Logger.w(SearchManager.TAG, fileInfo.mFileAbsolutePath);
            //将扫描到的数据添加到集合中
            mFileInfoList.add(fileInfo);
            if (file.isDirectory() && isNeedTraverse(file)) {
                //是文件夹 并且 需要遍历
                File[] files = file.listFiles();
                if (files == null || files.length == 0) {
                    //如果为空 则返回
                    return;
                }
                for (File tempFile : files) {
                    traverseFile(tempFile);
                }
            }
        } catch (SecurityException ex) {
            //如果没有读的权限 则直接略过文件夹
            ex.printStackTrace();
        }
    }

    /**
     * 是否需要遍历当前目录
     * */
    private boolean isNeedTraverse(File file) {
        if (file == null) {
            return false;
        }
        if (mDeepLimit == -1) {
            return true;
        }
        String [] pathNames = file.getAbsolutePath().split(File.separator);
        if (pathNames == null) {
            return true;
        }
        return pathNames.length <= mDeepLimit;
    }

}
