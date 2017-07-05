package com.jb.filemanager.function.zipfile.task;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.jb.filemanager.function.zipfile.bean.ZipPreviewFileBean;
import com.jb.filemanager.function.zipfile.listener.LoadZipInnerFilesListener;
import com.jb.filemanager.function.zipfile.util.CloseUtils;
import com.jb.filemanager.function.zipfile.util.FileUtils;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.exception.RarException;

/**
 * Created by xiaoyu on 2017/7/3 16:21.
 */

public class LoadZipInnerFilesTask extends AsyncTask<String, Integer, List<ZipPreviewFileBean>> {

    private LoadZipInnerFilesListener mListener = null;

    public void setListener(LoadZipInnerFilesListener listener) {
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mListener != null) {
            mListener.onPreLoad();
        }
    }

    /**
     * @param params 0为压缩文件路径, 1为获取文件的指定路径, 2为密码(无密码为null或""空字符串)
     * @return result
     */
    @Override
    protected List<ZipPreviewFileBean> doInBackground(String... params) {
        File file = new File(params[0]);
        String targetParentPath = params[1];
        String password = params[2];

        List<ZipPreviewFileBean> result = new ArrayList<>();
        if (file == null || !file.exists() || !file.isFile() || file.isDirectory()) return null;
        String extension = FileUtils.getFileExtension(file);
        if ("zip".equalsIgnoreCase(extension)) {
            if (!TextUtils.isEmpty(password)) {
                try {
                    net.lingala.zip4j.core.ZipFile zipFile = new net.lingala.zip4j.core.ZipFile(file);
                    zipFile.setPassword(password);
                    List<FileHeader> headers = zipFile.getFileHeaders();
                    for (FileHeader header : headers) {
                        if (isCancelled()) return result;
                        String parentPath = FileUtils.getParentPath(header.getFileName());
                        if (targetParentPath.equals(parentPath)) {
                            result.add(new ZipPreviewFileBean(header));
                        }
                    }
                    zipFile = null;
                } catch (ZipException e) {
                    e.printStackTrace();
                    return null;
                }
                return result;
            } else {
                ZipFile zipFile = null;
                try {
                    zipFile = new ZipFile(file);
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while (entries.hasMoreElements()) {
                        if (isCancelled()) return result;
                        ZipEntry zipEntry = entries.nextElement();
                        String parentPath = FileUtils.getParentPath(zipEntry.getName());
                        if (targetParentPath.equals(parentPath)) {
                            result.add(new ZipPreviewFileBean(zipEntry));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    CloseUtils.closeIO(zipFile);
                    zipFile = null;
                }
                return result;
            }
        } else if ("rar".equalsIgnoreCase(extension)) {
            Archive archive = null;
            try {
                archive = new Archive(file);
                List<de.innosystec.unrar.rarfile.FileHeader> headers = archive.getFileHeaders();
                for (de.innosystec.unrar.rarfile.FileHeader header : headers) {
                    if (isCancelled()) return result;
                    String name = FileUtils.formatterRarFileNameCode(header);
                    String parentPath = FileUtils.getParentPath(name);
                    if (targetParentPath.equals(parentPath)) {
                        result.add(new ZipPreviewFileBean(header));
                    }
                }
                return result;
            } catch (RarException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                CloseUtils.closeIO(archive);
                archive = null;
            }
        } else {
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (mListener != null) {
            mListener.onLoading(values[0]);
        }
    }

    @Override
    protected void onPostExecute(List<ZipPreviewFileBean> result) {
        if (mListener != null) {
            mListener.onPosLoad(result);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (mListener != null) {
            mListener.onCanceled();
        }
    }
}
