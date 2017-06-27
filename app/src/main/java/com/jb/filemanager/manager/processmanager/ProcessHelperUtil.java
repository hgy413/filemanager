package com.jb.filemanager.manager.processmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Process;
import android.text.TextUtils;

import com.jb.filemanager.manager.PackageManagerLocker;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.file.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 帮助5.0以上系统找到正在运行的应用列表和栈顶应用
 * @author zhanghuijun
 *
 */
public class ProcessHelperUtil {

	/**
	 * proc路径
	 */
	public static final String PATH_PROC = "/proc/";
	/**
	 * 进程文件名过滤器
	 */
	private static ProcessFilenameFilter sProcessFilenameFilter = new ProcessFilenameFilter(); 
	/**
	 * 需要过滤的UID进程
	 */
	private static List<Integer> sProcessUidFilter = new ArrayList<Integer>() {
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = -1976930146266829164L;

		{
			add(0);		// ROOT_UID
			add(Process.SYSTEM_UID);
			add(Process.PHONE_UID);
			add(2000);	// SHELL_UID 
			add(1007);	// LOG_UID
			add(1010);	// WIFI_UID
			add(1013);	// MEDIA_UID
			add(1019);	// DRM_UID
			add(1016);	// VPN_UID
			add(1027);	// NFC_UID
			add(1002);	// BLUETOOTH_UID
		}
	};
    // This is the process running the current foreground app.  We'd really
    // rather not kill it!
    public static final int FOREGROUND_APP_ADJ = 0;
    /**
     * 比较器
     */
    private static Comparator<ProcessHelperBean> sOomAdjComparator = new Comparator<ProcessHelperBean>() {

		@Override
		public int compare(ProcessHelperBean lhs, ProcessHelperBean rhs) {
			// 先按oom_score_adj倒序排，再按oom_score顺序排
			// 这里说明下为什么oom_score这个要顺序
			// oom_score_adj越小说明进行越重要，可以认为当oom_score_adj == 0时，该进行显示在前台；
			// 但是部分应用用了其他黑科技（例如一个像素的悬浮窗，我猜）也会导致它的oom_score_adj == 0，所以这部分要排除；
			// 而oom_score越大，说明该应用占用内存越大，也就是它极有可能是真正的前台应用；
			if (lhs.getOomScoreAdj() == rhs.getOomScoreAdj()) {
				return rhs.getOomScore() - lhs.getOomScore();
			}
			return lhs.getOomScoreAdj() - rhs.getOomScoreAdj();
		}
	};
	
	/**
	 * 获取桌面进程信息相关
	 */
	private static final long INTERVAL_GET_LAUNCHER_INFO = 30 * 1000;		// 30s更新一次桌面进程信息列表
	private static long sLastGetLauncherInfoTime = 0l;	// 上次获取进程信息的时间
	private static List<String> sLauncherFilenameList = new ArrayList<String>();	// 桌面进程信息的文件
	
	
	/**
	 * 获取/proc/路径下的进程文件夹
	 * @return
	 */
	public static List<String> getProcFilenameList() {
		File localFile = new File(PATH_PROC);
		List<String> filenameList = null;
		if (!localFile.isDirectory()) {
			Logger.e("zhanghuijun", PATH_PROC + " is not directory");
			filenameList = new ArrayList<String>();
		} else {
			String[] fileString = localFile.list(sProcessFilenameFilter);
			if (fileString != null) {
//				for (int j = 0; j < fileString.length; j++) {
//					filenameList.add(PATH_PROC + fileString[j] + "/");
//				}
				filenameList = Arrays.asList(fileString);
			} else {
				filenameList = new ArrayList<String>();
			}
		}
		return filenameList;
	}
	
	/**
	 * 获取进程Cmdline文件内容
	 * @return
	 */
	public static String getProcCmdline(String filePath) {
		String content = FileUtil.readFileToString(filePath + "cmdline");
		if (!TextUtils.isEmpty(content)) {
			return content.trim().split("\00")[0];
		} 
		return "";
	}
	
	/**
	 * 获取进程oom_adj文件内容
	 * @param filePath
	 * @return
	 */
	public static int getProcOomadj(String filePath) {
		String content = FileUtil.readFileToString(filePath + "oom_adj");
		if (!TextUtils.isEmpty(content)) {
			content = content.trim().replace("\"", "");
			try {
				int i = Integer.parseInt(content);
				return i;
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return -99;
			}
		} 
		return -99;
	}
	
	/**
	 * 获取进程oom_score文件内容
	 * @param filePath
	 * @return
	 */
	public static int getProcOomScore(String filePath) {
		String content = FileUtil.readFileToString(filePath + "oom_score");
		if (!TextUtils.isEmpty(content)) {
			content = content.trim().replace("\"", "");
			try {
				int i = Integer.parseInt(content);
				return i;
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return -99;
			}
		} 
		return -99;
	}
	
	/**
	 * 获取进程oom_score_adj文件内容
	 * @param filePath
	 * @return
	 */
	public static int getProcOomScoreAdj(String filePath) {
		String content = FileUtil.readFileToString(filePath + "oom_score_adj");
		if (!TextUtils.isEmpty(content)) {
			content = content.trim().replace("\"", "");
			try {
				int i = Integer.parseInt(content);
				return i;
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return -99;
			}
		} 
		return -99;
	}
	
	/**
	 * 获取进程PPID
	 * @param filePath
	 * @return
	 */
	public static int getProcPPid(String filePath) {
		String content = FileUtil.readFileToString(filePath + "status");
		if (!TextUtils.isEmpty(content)) {
			String[] contentArr = content.split(System.getProperty("line.separator"));
			String tempStr = "";
			String[] tempStrArr = null;
			for (int i = 0; i < contentArr.length; i++) {
				tempStr = contentArr[i];
				if (!tempStr.contains("PPid:")) {
					continue;
				}
				tempStrArr = tempStr.split("\\s+");
				if (tempStrArr.length != 2) {
					continue;
				}
				try {
					return Integer.parseInt(tempStrArr[1]);
				} catch (NumberFormatException e) {
					e.printStackTrace();
					return -1;
				}
			}
		}
		return -1;
	}
	
	/**
	 * 获取进程UID
	 * @param filePath
	 * @return
	 */
	public static int getProcUid(String filePath) {
		String content = FileUtil.readFileToString(filePath + "status");
		if (!TextUtils.isEmpty(content)) {
			String[] contentArr = content.split(System.getProperty("line.separator"));
			String tempStr = "";
			String[] tempStrArr = null;
			for (int i = 0; i < contentArr.length; i++) {
				tempStr = contentArr[i];
				if (!tempStr.contains("Uid:")) {
					continue;
				}
				tempStrArr = tempStr.split("\\s+");
				if (tempStrArr.length < 2) {
					continue;
				}
				try {
					return Integer.parseInt(tempStrArr[1]);
				} catch (NumberFormatException e) {
					e.printStackTrace();
					return -1;
				}
			}
		}
		return -1;
	}
	
	/**
	 * 获取运行状态
	 * @param filePath
	 * @return
	 */
	public static String getProcRunningState(String filePath) {
		String content = FileUtil.readFileToString(filePath + "status");
		if (!TextUtils.isEmpty(content)) {
			String[] contentArr = content.split(System.getProperty("line.separator"));
			String tempStr = "";
			String[] tempStrArr = null;
			for (int i = 0; i < contentArr.length; i++) {
				tempStr = contentArr[i];
				if (!tempStr.contains("State:")) {
					continue;
				}
				tempStrArr = tempStr.split("\\s+");
				if (tempStrArr.length != 3) {
					continue;
				}
				return tempStrArr[2];
			}
		}
		return "";
	}
	
	/**
	 * @return
	 */
	private static List<ProcessHelperBean> getProcBeans(Context context, List<String> filenameList) {
		List<ProcessHelperBean> procBeans = new ArrayList<ProcessHelperBean>();
		String filepath = "";
		for (int i = 0; i < filenameList.size(); i++) {
			try {
				filepath = PATH_PROC + filenameList.get(i) +  "/";
				int pid = Integer.parseInt(filenameList.get(i));
				ProcessHelperBean bean = new ProcessHelperBean();
				bean.setPid(pid);
				bean.setProcessName(getProcCmdline(filepath));
				bean.setPPid(getProcPPid(filepath));
				bean.setUid(getProcUid(filepath));
				bean.setPkgLists(PackageManagerLocker.getInstance().getPackagesForUid(ProcessHelperUtil.getProcUid(filepath)));
				bean.setOomAdj(getProcOomadj(filepath));
				bean.setOomScore(getProcOomScore(filepath));
				bean.setOomScoreAdj(getProcOomScoreAdj(filepath));
				bean.setProcFilename(filenameList.get(i));
				procBeans.add(bean);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				continue;
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		return procBeans;
	}
	
	/**
	 * 获取/proc/路径下的进程信息（包含部分系统进程）
	 * 注意：此方法获取的是/proc/下面的进程信息，其中有可能包含非正在运行中的应用，所以可以根据下面两个点进行过滤：
	 * 1、使用AppUtils.isAppStop()方法过滤已经停止运行的应用；
	 * 2、根据getProcRunningState获取进程运行状态，如果运行状态是D，也就是dead，即可过滤
	 * 本方法没有进行以上两点过滤，有需要可自行过滤
	 * @return
	 */
	public static List<ProcessHelperBean> getProcBeans(Context context) {
		List<String> filenameList = getProcFilenameList();
		return getProcBeans(context, filenameList);
	}
	
	/**
	 * 获取正在运行的应用（包含部分系统应用）
	 * 注意：此方法获取的是/proc/下面的进程信息，其中有可能包含非正在运行中的应用，所以可以根据下面两个点进行过滤：
	 * 1、使用AppUtils.isAppStop()方法过滤已经停止运行的应用；
	 * 2、根据getProcRunningState获取进程运行状态，如果运行状态是D，也就是dead，即可过滤
	 * 本方法没有进行以上两点过滤，有需要可自行过滤
	 * 
	 * @return 返回一个以包名为key，该包名应用正在运行的进程id组(pid组)为value的Map
	 */
	@SuppressLint("DefaultLocale")
	public static Map<String, List<Integer>> getRunningAppProcesses(Context context) {
		List<ProcessHelperBean> procBeans = getProcBeans(context);
		Map<String, List<Integer>> runningAppProcessesMap = new HashMap<String, List<Integer>>();
		for (int i = 0; i < procBeans.size(); i++) {
			ProcessHelperBean bean = procBeans.get(i);
			if (bean != null) {
				if (sProcessUidFilter.contains(bean.getUid())) {	// 过滤一些系统UID的应用
					continue;
				}
				String[] pkgList = bean.getPkgLists();
				if (pkgList == null) {
					continue;
				}
				for (int j = 0; j < pkgList.length; j++) {
					List<Integer> mPids = null;
					if (!bean.getProcessName().toLowerCase().contains(pkgList[j].toLowerCase())) {
						// 过滤一些很有可能没用的包名
						continue;
					}
					if (runningAppProcessesMap.containsKey(pkgList[j])) {
						mPids = runningAppProcessesMap.get(pkgList[j]);
					} else {
						mPids = new ArrayList<Integer>();
					}
					mPids.add(bean.getPid());
					runningAppProcessesMap.put(pkgList[j], mPids);
				}
			}
		}
		return runningAppProcessesMap;
	}
	
	/**
	 * 获取栈顶应用
	 * @return 
	 */
	public static ProcessHelperBean getTopApp(Context context) {
		List<ProcessHelperBean> topApps = new ArrayList<ProcessHelperBean>();
		List<ProcessHelperBean> procBeans = getProcBeans(context);
		for (int i = 0; i < procBeans.size(); i++) {
			ProcessHelperBean bean = procBeans.get(i);
			if (bean != null) {
//				if (sProcessUidFilter.contains(bean.getUid())) {	// 过滤一些系统UID的应用
//					continue;
//				}
				String[] pkgList = bean.getPkgLists();
				if (pkgList == null /*|| pkgList.length > 3*/) {
					// 一般不会超过三个应用同时在同一个Uid中，就算有可以认为是系统应用或者非有界面的进程
					continue;
				}
//				for (int j = 0; j < pkgList.length; j++) {
//					if (!AppUtils.isAppExist(context, pkgList[j])) {
//						// 不是应用
////						Log.d("zhanghuijun", pkgList[j] + "不是应用");
//						continue;
//					}
//					if (!TextUtils.isEmpty(bean.getProcessName().trim()) && bean.getOomAdj() == FOREGROUND_APP_ADJ) {
//						topApps.add(bean);
////						Log.d("zhanghuijun", pkgList[j] + " " + bean.getOomScore() + "  " + bean.getOomScoreAdj());
//						break;
//					}
//				}
				if (!TextUtils.isEmpty(bean.getProcessName().trim()) && bean.getOomAdj() == FOREGROUND_APP_ADJ) {
					topApps.add(bean);
//					Log.d("zhanghuijun", pkgList[j] + " " + bean.getOomScore() + "  " + bean.getOomScoreAdj());
				}
			}
		}
		Collections.sort(topApps, sOomAdjComparator);
		if (topApps.size() <= 0) {
			return null;
		}
		return topApps.get(0);
	}
	
	/**
	 * 获取桌面相关进程信息文件列表
	 */
	private static List<String> getLauncherFilenameList(Context context) {
		long now = System.currentTimeMillis();
		if (sLastGetLauncherInfoTime == 0l || now - sLastGetLauncherInfoTime > INTERVAL_GET_LAUNCHER_INFO) {
			sLauncherFilenameList.clear();
			List<ProcessHelperBean> allProcBeans = getProcBeans(context);
			List<String> launcherPackageNames = AppUtils.getLauncherPackageNames(context);
			for (int i = 0; i < allProcBeans.size(); i++) {
				ProcessHelperBean bean = allProcBeans.get(i);
				String[] pkgList = bean.getPkgLists();
				if (pkgList == null) {
					continue;
				}
				for (int j = 0; j < pkgList.length; j++) {
					for (int j2 = 0; j2 < launcherPackageNames.size(); j2++) {
						if (pkgList[j].equals(launcherPackageNames.get(j2))) {
							// 找到对应的进程信息文件
							sLauncherFilenameList.add(bean.getProcFilename());
							break;
						}
					}
				}
			}
			sLastGetLauncherInfoTime = now;
		}
		return sLauncherFilenameList;
	}
	
	/**
	 * 获取栈顶应用，只获取跟桌面相关的
	 * @return 
	 */
	public static ProcessHelperBean getTopAppOnlyLauncher(Context context) {
		List<ProcessHelperBean> topApps = new ArrayList<ProcessHelperBean>();
		List<String> launcherFileList = getLauncherFilenameList(context);
		List<ProcessHelperBean> procBeans = getProcBeans(context, launcherFileList);
		for (int i = 0; i < procBeans.size(); i++) {
			ProcessHelperBean bean = procBeans.get(i);
			if (bean != null) {
				String[] pkgList = bean.getPkgLists();
				if (pkgList == null) {
					continue;
				}
//				Logger.d("zhanghuijun", bean.getProcessName() + "  " + bean.getOomAdj() + " " + bean.getOomScore() + " " + bean.getOomScoreAdj());
				if (!TextUtils.isEmpty(bean.getProcessName().trim()) && bean.getOomAdj() == FOREGROUND_APP_ADJ &&
						bean.getOomScoreAdj() == FOREGROUND_APP_ADJ && bean.getOomScore() > 5) {
					topApps.add(bean);
				}
			}
		}
		Collections.sort(topApps, sOomAdjComparator);
		if (topApps.size() <= 0) {
			return null;
		}
		return topApps.get(0);
	}
}
