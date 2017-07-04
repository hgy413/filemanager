package com.jb.filemanager.function.video;

import android.content.ContentResolver;
import android.content.Context;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.musics.MusicInfo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by bool on 17-7-4.
 * 视频文件加载
 */

public class VideoSupport implements VideoContract.Support{
    private Context mContext;
    private ContentResolver mResolver;

    public VideoSupport() {
        this.mContext = TheApplication.getInstance();
        this.mResolver = mContext.getContentResolver();
    }

    @Override

    public Map<String, ArrayList<MusicInfo>> getAllVideoInfo() {
        return null;
    }
}
