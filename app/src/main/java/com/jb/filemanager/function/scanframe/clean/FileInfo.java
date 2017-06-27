package com.jb.filemanager.function.scanframe.clean;

import com.swift.boost.function.scanframe.bean.common.FileType;
import com.swift.boost.util.FileTypeUtil;

/**
 * SD卡文件信息
 */
public class FileInfo {
	
	public boolean mIsDirectory;
	public String mFileName;
	public String mPath;
	public String mTime;
	public long mSize;
	public String mExtension;
	
	/**
	 * 是否是文件
	 * @return
	 */
	public boolean isFile() {
		return !mIsDirectory;
	}
	
	/**
	 * 获取文件类型
	 * @return 类型：{@link FileType}
	 */
	public FileType getFileType() {
		return FileTypeUtil.getFileTypeFromPostfix(mExtension);
	}
}
