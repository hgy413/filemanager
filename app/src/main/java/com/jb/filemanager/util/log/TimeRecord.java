/*
 * 文 件 名:  TimeRecord.java
 * 版    权:  3G
 * 描    述:  <描述>
 * 修 改 人:  liguoliang
 * 修改时间:  2012-9-29
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.jb.filemanager.util.log;

import android.text.TextUtils;
import android.util.Log;

import com.jb.filemanager.util.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <br>
 * 类描述: <br>
 * 功能详细描述:
 * 
 * @author liguoliang
 * @date [2012-9-29]
 */
public class TimeRecord {
	private static boolean sDebug = Logger.DEBUG;

	private String mTag;
	private long mLastTimeStamp = -1;
	private String mLastMessage = null;
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd hh:mm:ss,SSS");

	private boolean mRecord = true;

	private static final String BEGIN = "begin";
	private static final String END = "end";

	private long mBeginTimeStamp = -1;

	private static long sLastTimestamp = -1L;

	// public static void enableDebug(boolean debug) {
	// sDebug = debug;
	// }

	public TimeRecord(String tag) {
		mTag = tag;
	}

	public TimeRecord(String tag, boolean record) {
		mTag = tag;
		mRecord = record;
	}

	public void begin() {
		if (sDebug && mRecord) {
			Date now = new Date();
			String nowStr = FORMAT.format(now);
			String log = "[" + BEGIN + "]" + " at: " + nowStr;
			Log.d(mTag, log);

			mBeginTimeStamp = mLastTimeStamp = now.getTime();
			mLastMessage = BEGIN;
		}
	}

	public void end() {
		if (sDebug && mRecord) {
			Date now = new Date();
			String nowStr = FORMAT.format(now);
			String log = "[" + END + "]" + " at: " + nowStr;
			Log.d(mTag, log);

			Log.d(mTag, "totalTime：" + (now.getTime() - mBeginTimeStamp));
		}
	}

	public void mark(String msg) {
		if (sDebug && mRecord) {
			Date now = new Date();
			String nowStr = FORMAT.format(now);
			String log = "[" + msg + "]" + " at: " + nowStr;
			Log.d(mTag, log);
			if (!TextUtils.isEmpty(mLastMessage) && mLastTimeStamp != -1) {
				log = "interval：" + (now.getTime() - mLastTimeStamp)
						+ " millisecond";
				Log.d(mTag, log);
			}

			mLastTimeStamp = now.getTime();
			mLastMessage = msg;
		}
	}

	public static void mark(String tag, String msg) {
		if (sDebug) {
			Date now = new Date();
			String nowStr = FORMAT.format(now);
			String log = "[" + msg + "]" + " at: " + nowStr;
			Log.d(tag, log);
			if (sLastTimestamp != -1) {
				log = "interval：" + (now.getTime() - sLastTimestamp)
						+ " millisecond";
				Log.d(tag, log);
			}

			sLastTimestamp = now.getTime();
		}
	}
}
