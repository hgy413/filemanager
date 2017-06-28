package com.jb.filemanager.function.applock.model.bean;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.jb.filemanager.function.applock.model.IDatabaseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用锁主界面组信息
 * Created by makai on 15-6-9.
 */
@SuppressLint("NewApi")
public class LockerGroup implements IDatabaseObject, Parcelable, Cloneable {

	public String tag;

	private String mGroupTitle;

	private List<LockerItem> mLockerItems;

	public LockerGroup() {
		mLockerItems = new ArrayList<LockerItem>();
	}

	public void addLockerItem(LockerItem item) {
		mLockerItems.add(item);
	}

	public void setLockerItems(List<LockerItem> mLockerItems) {
		this.mLockerItems = mLockerItems;
	}

	public List<LockerItem> getLockerItems() {
		return mLockerItems;
	}

	public void setGroupTitle(String mGroupTitle) {
		this.mGroupTitle = mGroupTitle;
	}

	public String getGroupTitle() {
		return mGroupTitle;
	}

	public LockerGroup clone() {
		LockerGroup lockerGroup = new LockerGroup();
		lockerGroup.mGroupTitle = mGroupTitle;
		if (null != mLockerItems) {
			List<LockerItem> list = new ArrayList<LockerItem>();
			for (LockerItem lockerItem : mLockerItems) {
				list.add(lockerItem.clone());
			}
			lockerGroup.mLockerItems = list;
		}
		return lockerGroup;
	}

	public List<LockerItem> getCheckedLockerItems() {
		if (null != mLockerItems) {
			List<LockerItem> list = new ArrayList<LockerItem>();
			for (LockerItem lockerItem : mLockerItems) {
				if (lockerItem.isChecked) {
					list.add(lockerItem);
				}
			}
			return list.isEmpty() ? null : list;
		}
		return null;
	}

    public int getSize() {
        return mLockerItems.size();
    }

	@Override
	public void writeObject(ContentValues values, String table) {
		if (null != mLockerItems) {
			for (LockerItem lockerItem : mLockerItems) {
				lockerItem.writeObject(values, table);
			}
		}
	}

	@Override
	public void readObject(Cursor cursor, String table) {
		if (null != mLockerItems) {
			for (LockerItem lockerItem : mLockerItems) {
				lockerItem.readObject(cursor, table);
			}
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(mGroupTitle);
		if (mLockerItems == null) {
			dest.writeInt(0);
			return;
		}
		dest.writeInt(mLockerItems.size());
		for (LockerItem item : mLockerItems) {
			dest.writeParcelable(item, flags);
		}
	}

	public static final Creator<LockerGroup> CREATOR = new ClassLoaderCreator<LockerGroup>() {

		@Override
		public LockerGroup createFromParcel(Parcel source) {
			return new LockerGroup(source, null);
		}

		@Override
		public LockerGroup[] newArray(int size) {
			return new LockerGroup[size];
		}

		@Override
		public LockerGroup createFromParcel(Parcel source, ClassLoader loader) {
			return new LockerGroup(source, loader);
		}
	};

	private LockerGroup(Parcel source, ClassLoader loader) {
		mGroupTitle = source.readString();
		mLockerItems = new ArrayList<LockerItem>();
		final int size = source.readInt();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				LockerItem item = source.readParcelable(loader);
				mLockerItems.add(item);
			}
		}
	}
}
