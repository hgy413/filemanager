package com.jb.filemanager.home.event;

/**
 * Created by bill wang on 2017/7/21.
 * 文件浏览器当前路径变动事件
 */

public class CurrentPathChangeEvent {

    public String mCurrentPath;

    public CurrentPathChangeEvent(String currentPath) {
        mCurrentPath = currentPath;
    }
}
