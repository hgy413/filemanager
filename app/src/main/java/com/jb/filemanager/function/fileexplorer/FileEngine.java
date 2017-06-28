package com.jb.filemanager.function.fileexplorer;


import com.jb.filemanager.function.scanframe.clean.FileInfo;
import com.jb.filemanager.util.file.FileUtil;
import com.jb.filemanager.util.TimeUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * 
 * @author lishen
 */
public class FileEngine {

	/**
	 * 列出指定路径下的所有文件夹和文件(不做递归深入)
	 * 
	 * @param path
	 * @return
	 */
	public static ArrayList<FileInfo> listFiles(String path) {
		ArrayList<FileInfo> files = new ArrayList<FileInfo>();
		File file = null;
		try {
			file = new File(path);
		} catch (Exception e) {
		}
		if ((file != null) && file.exists() && file.isDirectory()) {
			File[] filelist = file.listFiles();
			if (filelist != null) {
				for (File subFile : filelist) {
					FileInfo fi = buildFileInfo(subFile);
					files.add(fi);
				}
			}
		}
		return files;
	}

	/**
	 * 获取指定文件的文件信息
	 * 
	 * @param file
	 * @return
	 */
	public static FileInfo buildFileInfo(File file) {
		FileInfo fi = null;
		if (file != null) {
			fi = new FileInfo();
			fi.mFileName = file.getName();
			fi.mExtension = FileUtil.getExtension(fi.mFileName);
			fi.mPath = file.getPath();
			fi.mIsDirectory = file.isDirectory();
			fi.mSize = file.length();
			fi.mTime = TimeUtil.getTime(file.lastModified());
		}
		return fi;
	}
}
