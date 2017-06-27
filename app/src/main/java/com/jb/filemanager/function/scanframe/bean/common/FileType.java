package com.jb.filemanager.function.scanframe.bean.common;

import android.os.Parcel;
import android.os.Parcelable;

import com.jb.filemanager.R;


/**
 * Created by xiaoyu on 2016/10/25.
 */

public enum FileType implements Parcelable {
    /**
     * 视频
     */
    VIDEO("VIDEO", R.string.filetype_video),
    /**
     * 音频
     */
    MUSIC("MUSIC", R.string.filetype_music),
    /**
     * 文档
     */
    DOCUMENT("DOCUMENT", R.string.filetype_document),
    /**
     * 安装包
     */
    APK("APK", R.string.filetype_apk),
    /**
     * 图片
     */
    IMAGE("IMAGE", R.string.filetype_image),
    /**
     * 压缩包
     */
    COMPRESSION("COMPRESSION", R.string.filetype_compression),
    /**
     * 其他
     */
    OTHER("OTHER", R.string.common_others);

    private String mName;
    private int mRes;

    private FileType(String name, int res) {
        mName = name;
        mRes = res;
    }

    public String getName() {
        return mName;
    }

    public int getRes() {
        return mRes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeInt(mRes);
    }

    private static FileType getFileType(Parcel in) {
        String name = in.readString();
        FileType[] values = FileType.values();
        for (FileType value : values) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null; // should not happen
    }

    public static final Creator<FileType> CREATOR = new Creator<FileType>() {
        public FileType createFromParcel(Parcel in) {
            return getFileType(in);
        }

        public FileType[] newArray(int size) {
            return new FileType[size];
        }
    };

}
