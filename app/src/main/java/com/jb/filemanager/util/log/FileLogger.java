package com.jb.filemanager.util.log;

import android.os.Environment;
import android.os.Process;
import android.text.format.Time;

import com.jb.filemanager.Const;
import com.jb.filemanager.util.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 用于打印日志到文件, 方便测试, 记录程序运行情况.<br>
 * 
 * @author wenjiaming
 * @date [2012-8-6]
 */
public class FileLogger {

	public final static String LOG_FILE_DEFAULT = "runtime_log.txt";
	public final static String LOG_FILE_AB_TEST = "ab_test.txt";
	public final static String LOG_FILE_ACCESSIBILITY_BOOST = "accessibility_boost.txt";
	public final static String LOG_FILE_ACCESSIBILITY_BOOST_REPORT = "accessibility_boost_report.txt";

	private static final String RUNTIME_LOG_PATH = Const.LOG_DIR;

	private final Time mTime;
	private final static long FILE_SIZE = 512 * 1024;
	//	public final static boolean ISWRITE = Logger.DEBUG;
	private final ExecutorService mThreadPool;

	private static FileLogger sInstance = new FileLogger();

	private static boolean isWrite() {
		return Logger.DEBUG;
	}

	private FileLogger() {
		mThreadPool = Executors.newFixedThreadPool(1);
		mTime = new Time();
	}

	private static FileLogger getInstance() {
		if (null == sInstance) {
			sInstance = new FileLogger();
		}
		return sInstance;
	}

	/**
	 * 功能简述:往SD卡特定的目录写日志 功能详细描述: 注意:
	 * 
	 * @param text
	 */
	public static void writeFileLogger(String text) {
		if (!isWrite()) {
			return;
		}
		getInstance().writeFileLoggerPrivate(text);
	}

	private void writeFileLoggerPrivate(String text) {
		mThreadPool.execute(new LoggerContent(text, LOG_FILE_DEFAULT));
	}

	/**
	 * 功能简述:往SD卡特定的目录写日志 功能详细描述: 注意:
	 * 
	 * @param text
	 */
	public static void writeFileLogger(String text, String fileName) {
		if (!isWrite()) {
			return;
		}
		getInstance().writeFileLoggerPrivate(text, fileName);
	}

	private void writeFileLoggerPrivate(String text, String fileName) {
		mThreadPool.execute(new LoggerContent(text, fileName));
	}

	/**
	 * 
	 * 类描述:写文件的任务类 功能详细描述:
	 * 
	 * @author wenjiaming
	 * @date [2012-9-4]
	 */
	private class LoggerContent implements Runnable {

		private final String mText;
		private final String mFileName;

		public LoggerContent(String text, String fileName) {
			mText = text;
			mFileName = fileName;
		}

		@Override
		public void run() {
			Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);
			mTime.setToNow();
			String time = mTime.format2445();
			String logger = time + " : " + mText + "\n";
			String state = Environment.getExternalStorageState();
			if (state.equals(Environment.MEDIA_MOUNTED)) {
				String path = RUNTIME_LOG_PATH;
				File file = new File(path);
				if (!file.exists()) {
					file.mkdirs();
				}
				if (file.exists()) {
					long length = file.length();
					FileOutputStream os = null;
					if (length > FILE_SIZE) {
						try {
							os = new FileOutputStream(path + mFileName, false);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					} else {
						try {
							os = new FileOutputStream(path + mFileName, true);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
					if (os != null) {
						try {
							os.write(logger.getBytes());
							os.flush();
							os.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

}
