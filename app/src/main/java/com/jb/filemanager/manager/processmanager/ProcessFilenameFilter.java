package com.jb.filemanager.manager.processmanager;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/**
 * 文件名过滤器
 * @author zhanghuijun
 *
 */
final class ProcessFilenameFilter implements FilenameFilter {
	/**
	 * 匹配模式，只要数字
	 */
	private Pattern mPattern = Pattern.compile("^[0-9]+$");

	@Override
	public boolean accept(File dir, String filename) {
		return mPattern.matcher(filename).matches();
	}
}
