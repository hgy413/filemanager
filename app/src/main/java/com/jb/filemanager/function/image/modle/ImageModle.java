package com.jb.filemanager.function.image.modle;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nieyh on 17-7-3.
 * 图片数据模块
 */

public class ImageModle implements Parcelable {
    //图片本地路径
    public String mImagePath;
    //图片的id 用于删除使用
    public int mImageId;
    //是否选上了
    public boolean isChecked;
    //时间
    public long mDate;

    public ImageModle(String imagePath, int imageId, boolean isChecked, long date) {
        mImagePath = imagePath;
        mImageId = imageId;
        this.isChecked = isChecked;
        mDate = date;
    }

    /**
     * 序列化
     * */

    protected ImageModle(Parcel in) {
        mImagePath = in.readString();
        mImageId = in.readInt();
        isChecked = in.readByte() != 0;
        mDate = in.readLong();
    }

    public static final Creator<ImageModle> CREATOR = new Creator<ImageModle>() {
        @Override
        public ImageModle createFromParcel(Parcel in) {
            return new ImageModle(in);
        }

        @Override
        public ImageModle[] newArray(int size) {
            return new ImageModle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mImagePath);
        dest.writeInt(mImageId);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeLong(mDate);
    }
}
