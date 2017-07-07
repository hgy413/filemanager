package com.jb.filemanager.function.search.event;

/**
 * Created by nieyh on 17-7-7.
 * 文件删除事件
 * 其他位置删除数据 发送这个Event 搜索列表将会及时更新
 */

public class FileDeleteEvent {

    public String mAbsolutePath;

    public FileDeleteEvent(String absolutePath) {
        mAbsolutePath = absolutePath;
    }
}
