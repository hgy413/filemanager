package com.jb.filemanager.function.download;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.musics.GroupList;
import com.jb.filemanager.function.musics.MusicInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bool on 17-7-1.
 * 下载文件加载
 */

public class DownloadSupport implements DownloadContract.Support {
    // 缓存文件头信息-文件头信息
    public static final HashMap<String, String> mFileTypes = new HashMap<String, String>();
    static {
        // images
        mFileTypes.put("FFD8FF", "jpg");
        mFileTypes.put("89504E47", "png");
        mFileTypes.put("47494638", "gif");
        mFileTypes.put("49492A00", "tif");
        mFileTypes.put("424D", "bmp");
        //
        mFileTypes.put("41433130", "dwg"); // CAD
        mFileTypes.put("38425053", "psd"); // 可能为ps的文件
        mFileTypes.put("7B5C727466", "rtf"); // 日记本
        mFileTypes.put("3C3F786D6C", "xml");
        mFileTypes.put("68746D6C3E", "html");
        mFileTypes.put("44656C69766572792D646174653A", "eml"); // 邮件
        mFileTypes.put("D0CF11E0", "doc");
        mFileTypes.put("D0CF11E0", "xls");//excel2003版本文件
        mFileTypes.put("5374616E64617264204A", "mdb");
        mFileTypes.put("252150532D41646F6265", "ps");
        mFileTypes.put("255044462D312E", "pdf");
        mFileTypes.put("504B0304", "docx");
        mFileTypes.put("504B0304", "xlsx");//excel2007以上版本文件
        mFileTypes.put("52617221", "rar");
        mFileTypes.put("57415645", "wav");
        mFileTypes.put("41564920", "avi");
        mFileTypes.put("2E524D46", "rm");
        mFileTypes.put("000001BA", "mpg");
        mFileTypes.put("000001B3", "mpg");
        mFileTypes.put("6D6F6F76", "mov");
        mFileTypes.put("3026B2758E66CF11", "asf");
        mFileTypes.put("4D546864", "mid");
        mFileTypes.put("1F8B08", "gz");
    }
    private final String mDownPath;
    private Context mContext;
    private ContentResolver mResolver;

    public DownloadSupport() {
        this.mContext = TheApplication.getInstance();
        mResolver = mContext.getContentResolver();
        mDownPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

    }

    @Override
    public GroupList<String, MusicInfo> getAllDownloadInfo() {
        File download = new File(mDownPath);
        List<File> fileList;
        if (download != null && download.exists() && download.isDirectory()) {
            fileList = getFile(download);
            for (int i = 0; i < fileList.size(); i++) {
                File file = fileList.get(i);
                Log.i("文件名字", file.getName());
            }
        }

        return new GroupList<>();
    }

    public List<File> getFile(File file) {
        List<File> list = new ArrayList<>();
        File[] fileArray = file.listFiles();
        for (File f : fileArray) {
            if (f.isFile()) {
                list.add(f);
            } else {
                list.addAll(getFile(f));
            }
        }
        return list;
    }

    public MusicInfo getFileInfo(File file){
        MusicInfo info = new MusicInfo();
        info.mFullPath = file.getPath();
        info.mName = file.getName();
        info.mModified = file.lastModified();
        info.mSize = file.length();
        return info;
    }

    int getDownloadNum() {
        int musicCount = 0;

        return musicCount;
    }
}

