package com.jb.filemanager.function.zipfile.listener;

/**
 * Created by xiaoyu on 2017/7/4 17:43.
 */

public interface ExtractingFilesListener {
    void onPreExtractFiles();
    void onExtractingFile(float percent);
    void onPostExtractFiles();
    void onCancelExtractFiles();
    void onExtractError();
}
