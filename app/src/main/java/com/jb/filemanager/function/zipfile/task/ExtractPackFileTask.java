package com.jb.filemanager.function.zipfile.task;

import android.os.AsyncTask;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

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
 * Created by xiaoyu on 2017/7/4 21:08.
 */

public class ExtractPackFileTask extends AsyncTask<String, Float, String> {

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

    @Override
    protected String doInBackground(String... params) {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        String packFilePath = params[0];
        String password = params[1];

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
        if (!mkdir) return null;

        long pathFreeSize = StorageUtil.getPathFreeSize(saveDir.getPath());

        String extension = FileUtils.getFileExtension(packFilePath);
        if ("zip".equalsIgnoreCase(extension)) {
            try {
                ZipFile zipFile = new ZipFile(packFilePath);
                if (!TextUtils.isEmpty(password)) {
                    zipFile.setPassword(password);
                }
                List<net.lingala.zip4j.model.FileHeader> headers = zipFile.getFileHeaders();
                long totalSize = 0;
                for (net.lingala.zip4j.model.FileHeader header : headers) {
                    totalSize += header.getUncompressedSize();
                }
                if (pathFreeSize - totalSize < 100 * 1024 * 1024) return null;

                float totalCount = headers.size();
                for (int i = 0; i < headers.size(); i++) {
                    if (isCancelled()) return saveDir.getPath();
                    net.lingala.zip4j.model.FileHeader header = headers.get(i);
                    publishProgress(i / totalCount);
                    Log.e("extract", "percent = " + (i / totalCount));
                    zipFile.extractFile(header, saveDir.getPath());
                }
                return  saveDir.getPath();
            } catch (ZipException e) {
                e.printStackTrace();
                return null;
            }
        } else if ("rar".equalsIgnoreCase(extension)) {
            Archive archive = null;
            try {
                archive = new Archive(new File(packFilePath));
                List<FileHeader> fileHeaders = archive.getFileHeaders();

                long totalSize = 0;
                for (FileHeader header : fileHeaders) {
                    totalSize += header.getFullUnpackSize();
                }
                if (pathFreeSize - totalSize < 100 * 1024 * 1024) return null;

                float totalCount = fileHeaders.size();
                for (int i = 0; i < fileHeaders.size(); i++) {
                    if (isCancelled()) break;
                    FileHeader header = fileHeaders.get(i);
                    String name = FileUtils.formatterRarFileNameCode(header);
                    File destFile = new File(saveDir.getPath() + File.separator + name);
                    publishProgress(i / totalCount);
                    Log.e("extract", "percent = " + (i / totalCount));
                    if (header.isDirectory()) {
                        destFile.mkdirs();
                    } else {
                        File parentFile = destFile.getParentFile();
                        if (packFilePath != null && !parentFile.exists()) {
                            parentFile.mkdirs();
                        }
                        FileOutputStream outputStream = new FileOutputStream(destFile);
                        try {
                            archive.extractFile(header, outputStream);
                        } catch (RarException e) {
//                            String nam = e.getType().name();
                            return null;
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                            return null;
                        } finally {
                            outputStream.close();
                        }
                    }
                }
                return saveDir.getPath();
            } catch (RarException e) {
                e.printStackTrace();
                Log.e("task", "rar exception");
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("task", "io exception");
                return null;
            } finally {
                CloseUtils.closeIO(archive);
            }
        } else {
            Log.e("task", "else");
            return null;
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
    protected void onPostExecute(String isSuccess) {
        super.onPostExecute(isSuccess);
        if (mListener != null) {
            if (!TextUtils.isEmpty(isSuccess)) {
                mListener.onPostExtractFiles(isSuccess);
            } else {
                mListener.onExtractError();
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mListener.onCancelExtractFiles();
    }
}
