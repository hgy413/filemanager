package com.jb.filemanager.function.scanframe.bean;

/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  chenhewen
 * @date  [2015年1月29日]
 */
public class ApkItemBean {
	
	private String mName;
	
	private String mPkgName;
	
	private String mBackupPath;

	private String mVertionName;
	
	private int mVertionCode;
	
	private boolean mInstalled;
	
	private long mSize;
	
	private String mBackupTime;
	
	private boolean mChecked;
	
	public String getPath() {
		return mBackupPath;
	}

	public void setPath(String mPath) {
		this.mBackupPath = mPath;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public String getPkgName() {
		return mPkgName;
	}

	public void setPkgName(String pkgName) {
		this.mPkgName = pkgName;
	}

	public String getVertionName() {
		return mVertionName;
	}

	public void setVertionName(String mVertionName) {
		this.mVertionName = mVertionName;
	}
	
	public int getVertionCode() {
		return mVertionCode;
	}

	public void setVertionCode(int mVertionCode) {
		this.mVertionCode = mVertionCode;
	}

	public boolean isInstalled() {
		return mInstalled;
	}

	public void setInstalled(boolean isInstalled) {
		this.mInstalled = isInstalled;
	}

	public long getSize() {
		return mSize;
	}

	public void setSize(long mSize) {
		this.mSize = mSize;
	}

	public String getBackupTime() {
		return mBackupTime;
	}

	public void setBackupTime(String mBackupTime) {
		this.mBackupTime = mBackupTime;
	}
	
	public boolean isChecked() {
		return mChecked;
	}

	public void setChecked(boolean isChecked) {
		this.mChecked = isChecked;
	}
	
}
