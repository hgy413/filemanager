package com.jb.filemanager.function.zipfile.task;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.jb.filemanager.function.zipfile.bean.ZipPreviewFileBean;
import com.jb.filemanager.function.zipfile.listener.ExtractingFilesListener;
import com.jb.filemanager.function.zipfile.util.CloseUtils;
import com.jb.filemanager.function.zipfile.util.FileUtils;

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

public class ExtractFilesTask extends AsyncTask<Object, String, Boolean> {

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

        String extension = FileUtils.getFileExtension(packFilePath);
        if ("zip".equalsIgnoreCase(extension)) {
            try {
                ZipFile zipFile = new ZipFile(packFilePath);
                if (!TextUtils.isEmpty(password)) {
                    zipFile.setPassword(password);
                }
                for (ZipPreviewFileBean bean : data) {
                    String fullPath = bean.getFullPath();
                    publishProgress(fullPath);
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
                for (FileHeader header : fileHeaders) {
                    short headCRC = header.getHeadCRC();
                    for (ZipPreviewFileBean bean : data) {
                        if (bean.getCrc() == headCRC) {
                            publishProgress(bean.getFullPath());
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
    protected void onProgressUpdate(String... values) {
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
