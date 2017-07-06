package com.jb.filemanager.function.search.modle;

import android.os.Parcel;
import android.os.Parcelable;

import com.jb.filemanager.function.search.util.FilePlus;

import java.io.File;

/**
 * Created by nieyh on 17-7-5.
 * 文件信息详情 {
 *     1. 文件名
 *     2. 绝对路径
 *     3. 文件类型
 *     4. 修改时间
 * }
 */

public class FileInfo implements Parcelable{

    //文件名
    public String mFileName;
    //绝对路径
    public String mFileAbsolutePath;
    //文件类型
    public int mFileType;
    //linux中不存在创建时间的概念
    // 所以只能使用修改时间
    public long mModificateTime;

    public FileInfo(File file) {
        if (file == null) {
            return;
        }
        mFileName = file.getName();
        mFileAbsolutePath = file.getAbsolutePath();
        mFileType = FilePlus.getFileType(file);
        mModificateTime = file.lastModified();
    }

    public FileInfo() {}


    protected FileInfo(Parcel in) {
        mFileName = in.readString();
        mFileAbsolutePath = in.readString();
        mFileType = in.readInt();
        mModificateTime = in.readLong();
    }

    public static final Creator<FileInfo> CREATOR = new Creator<FileInfo>() {
        @Override
        public FileInfo createFromParcel(Parcel in) {
            return new FileInfo(in);
        }

        @Override
        public FileInfo[] newArray(int size) {
            return new FileInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFileName);
        dest.writeString(mFileAbsolutePath);
        dest.writeInt(mFileType);
        dest.writeLong(mModificateTime);
    }
}
