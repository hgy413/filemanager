package com.jb.filemanager.function.apkmanager.searchresult;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/3 17:09
 */

public class SearchResultBean implements Parcelable{
    public String mAppName;
    public String mPackageName;

    public SearchResultBean() {
    }

    public SearchResultBean(String appName, String packageName) {
        this.mAppName = appName;
        this.mPackageName = packageName;
    }

    protected SearchResultBean(Parcel in) {
        mAppName = in.readString();
        mPackageName = in.readString();
    }

    public static final Creator<SearchResultBean> CREATOR = new Creator<SearchResultBean>() {
        @Override
        public SearchResultBean createFromParcel(Parcel in) {
            return new SearchResultBean(in);
        }

        @Override
        public SearchResultBean[] newArray(int size) {
            return new SearchResultBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mAppName);
        parcel.writeString(mPackageName);
    }
}
