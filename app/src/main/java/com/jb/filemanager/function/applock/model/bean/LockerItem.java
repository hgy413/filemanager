package com.jb.filemanager.function.applock.model.bean;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.jb.filemanager.database.table.LockerSceneItemTable;
import com.jb.filemanager.database.table.LockerTable;
import com.jb.filemanager.function.applock.model.IDatabaseObject;
import com.jb.filemanager.manager.PackageManagerLocker;

/**
 * 应用锁主界面锁信息
 * Created by makai on 15-6-9.
 */
public class LockerItem implements IDatabaseObject, Parcelable {

	private String mTitle;

	public String descript;

	private ResolveInfo mResolveInfo;

	public boolean isChecked = false;

	public ComponentName componentName;
	
	public String mPackageName = null;

	public LockerItem() {
	}


	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public String getTitle() {
		if (TextUtils.isEmpty(mTitle) && null != mResolveInfo) {
			mTitle = mResolveInfo.loadLabel(PackageManagerLocker.getInstance().getPackageManager()).toString();
		}
		return mTitle;
	}

	public void setResolveInfo(ResolveInfo mResolveInfo) {
		this.mResolveInfo = mResolveInfo;
	}

	public void setComponentName(String pkgName, String clssName) {
		componentName = new ComponentName(pkgName, clssName);
		mPackageName = pkgName;
	}

	public Drawable getIcon() {
		if (mResolveInfo != null) {
			return mResolveInfo.loadIcon(PackageManagerLocker.getInstance().getPackageManager());
		}
		return null;
	}

	public void setComponentName(ResolveInfo resolveInfo) {
		setComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
	}

	public String getPackageName() {
		return mPackageName;
	}

	@Override
	public void writeObject(ContentValues values, String table) {
		if (LockerSceneItemTable.TABLE_NAME.equals(table)) {
			values.put(LockerSceneItemTable.COMPONENTNAME, componentNameToString(componentName));
		} else if (LockerTable.TABLE_NAME.equals(table)) {
			values.put(LockerTable.COMPONENTNAME, componentNameToString(componentName));
		}
	}

	@Override
	public void readObject(Cursor cursor, String table) {
		if (LockerSceneItemTable.TABLE_NAME.equals(table)) {
			String component = cursor.getString(cursor.getColumnIndex(LockerSceneItemTable.COMPONENTNAME));
			componentName = stringToComponentName(component);
		} else if (LockerTable.TABLE_NAME.equals(table)) {
			String component = cursor.getString(cursor.getColumnIndex(LockerTable.COMPONENTNAME));
			componentName = stringToComponentName(component);
		}
	}

	/**
	 * 将字符转换为ComponentName
	 * @param component
	 * @return
	 */
	public static ComponentName stringToComponentName(String component) {
		return ComponentName.unflattenFromString(component);
	}

	/**
	 * 将ComponentName转换为字符
	 * @param componentName
	 * @return
	 */
	public static String componentNameToString(ComponentName componentName) {
		return componentName.flattenToString();
	}

	@Override
	protected LockerItem clone() {
		LockerItem lockerItem = new LockerItem();
		lockerItem.setResolveInfo(mResolveInfo);
		lockerItem.mTitle = mTitle;
		lockerItem.descript = descript;
		lockerItem.isChecked = isChecked;
		lockerItem.componentName = componentName;
		return lockerItem;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.mTitle);
		dest.writeString(this.descript);
		dest.writeParcelable(this.mResolveInfo, 0);
		dest.writeByte(isChecked ? (byte) 1 : (byte) 0);
		dest.writeParcelable(this.componentName, 0);
	}

	protected LockerItem(Parcel in) {
		this.mTitle = in.readString();
		this.descript = in.readString();
		this.mResolveInfo = in.readParcelable(ResolveInfo.class.getClassLoader());
		this.isChecked = in.readByte() != 0;
		this.componentName = in.readParcelable(ComponentName.class.getClassLoader());
	}

	public static final Creator<LockerItem> CREATOR = new Creator<LockerItem>() {
		public LockerItem createFromParcel(Parcel source) {
			return new LockerItem(source);
		}

		public LockerItem[] newArray(int size) {
			return new LockerItem[size];
		}
	};
}
