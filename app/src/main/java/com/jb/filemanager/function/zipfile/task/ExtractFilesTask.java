package com.jb.filemanager.function.zipfile.task;

import android.os.AsyncTask;
import android.os.Process;
import android.text.TextUtils;

import com.jb.filemanager.function.zipfile.bean.ZipPreviewFileBean;
import com.jb.filemanager.function.zipfile.listener.ExtractingFilesListener;
import com.jb.filemanager.function.zipfile.util.CloseUtils;
import com.jb.filemanager.function.zipfile.util.FileUtils;
import com.jb.filemanager.util.StorageUtil;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.exception.RarException;
import de.innosystec.unrar.rarfile.FileHeader;

/**
 * Created by xiaoyu on 2017/7/4 17:40.
 */

public class ExtractFilesTask extends AsyncTask<Object, Float, Boolean> {

    private ExtractingFilesListener mListener;

    public void setListener(ExtractingFilesListener listener) {
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mListener != null) {
            mListener.onPreExtractFiles();
        }
    }

    // 若为加密文件, 则是zip格式
    // 若不是加密文件, 则是zip或者rar格式
    @Override
    protected final Boolean doInBackground(Object... params) {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        String packFilePath = (String) params[0];
        String password = (String) params[1];
        List<ZipPreviewFileBean> data = (List<ZipPreviewFileBean>) params[2];

        // 创建存储目录
        String dir = FileUtils.getParentPath(packFilePath) + File.separator
                + FileUtils.getFileNameNoExtension(packFilePath);
        File saveDir = new File(dir);
        for (int i = 1; ; i++) {
            if (saveDir.exists() && saveDir.isDirectory()) {
                saveDir = new File(dir + "(" + i + ")");
            } else {
                break;
            }
        }
        boolean mkdir = saveDir.mkdir();
        if (!mkdir) return false;

        long pathFreeSize = StorageUtil.getPathFreeSize(saveDir.getPath());
        long totalSize = 0;
        for (ZipPreviewFileBean bean : data) {
            totalSize += bean.getSize();
        }
        if (pathFreeSize - totalSize < 100 * 1024 * 1024) {
            // 当解压后的空间小于100M时, 结束。
            return false;
        }

        float totalCount = data.size();
        String extension = FileUtils.getFileExtension(packFilePath);
        if ("zip".equalsIgnoreCase(extension)) {
            try {
                ZipFile zipFile = new ZipFile(packFilePath);
                if (!TextUtils.isEmpty(password)) {
                    zipFile.setPassword(password);
                }
                for (int i = 0; i < data.size(); i++) {
                    ZipPreviewFileBean bean = data.get(i);
                    if (isCancelled()) return true;
                    String fullPath = bean.getFullPath();
                    publishProgress(i / totalCount);
                    zipFile.extractFile(fullPath, saveDir.getPath());
                }
                return true;
            } catch (ZipException e) {
                e.printStackTrace();
                return false;
            }
        } else if ("rar".equalsIgnoreCase(extension)) {
            Archive archive = null;
            try {
                archive = new Archive(new File(packFilePath));
                List<FileHeader> fileHeaders = archive.getFileHeaders();
                for (int i = 0; i < fileHeaders.size(); i++) {
                    FileHeader header = fileHeaders.get(i);
                    short headCRC = header.getHeadCRC();
                    for (ZipPreviewFileBean bean : data) {
                        if (isCancelled()) return true;
                        if (bean.getCrc() == headCRC) {
                            publishProgress(i / totalCount);
                            File out = new File(FileUtils.removeEdgeSeparatorIfExist(saveDir.getPath())
                                    + File.separator + bean.getFileName());
                            archive.extractFile(header, new FileOutputStream(out));
                        }
                    }
                }
                return true;
            } catch (RarException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                CloseUtils.closeIO(archive);
            }
        } else {
            return false;
        }
    }

    @Override
    protected void onProgressUpdate(Float... values) {
        super.onProgressUpdate(values);
        if (mListener != null) {
            mListener.onExtractingFile(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
        if (mListener != null) {
            if (isSuccess) {
                mListener.onPostExtractFiles();
            } else {
                mListener.onExtractError();
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (mListener != null) {
            mListener.onCancelExtractFiles();
        }
    }
}
