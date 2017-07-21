package com.jb.filemanager.function.docmanager;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/4 10:42
 */

public class DocChildBean implements Parcelable{

    public static final int TYPE_DOC = 0;
    public static final int TYPE_TXT = 1;
    public static final int TYPE_PDF = 2;
    public static final int TYPE_XLS = 3;
    public static final int TYPE_PPT = 4;

    public String mDocId;
    public int mFileType;
    public String mDocDate;
    public long mAddDate;
    public long mModifyDate;
    public String mDocPath;
    public String mDocName;
    public String mDocSize;
    public boolean mIsChecked;
    public DocChildBean() {
    }

    protected DocChildBean(Parcel in) {
        mDocId = in.readString();
        mFileType = in.readInt();
        mDocDate = in.readString();
        mAddDate = in.readLong();
        mModifyDate = in.readLong();
        mDocPath = in.readString();
        mDocName = in.readString();
        mDocSize = in.readString();
        mIsChecked = in.readByte() != 0;
    }

    public static final Creator<DocChildBean> CREATOR = new Creator<DocChildBean>() {
        @Override
        public DocChildBean createFromParcel(Parcel in) {
            return new DocChildBean(in);
        }

        @Override
        public DocChildBean[] newArray(int size) {
            return new DocChildBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mDocId);
        parcel.writeInt(mFileType);
        parcel.writeString(mDocDate);
        parcel.writeLong(mAddDate);
        parcel.writeLong(mModifyDate);
        parcel.writeString(mDocPath);
        parcel.writeString(mDocName);
        parcel.writeString(mDocSize);
        parcel.writeByte((byte) (mIsChecked ? 1 : 0));
    }
}
