package com.jb.filemanager.function.applock.model.bean;

import java.util.Comparator;

/**
 * 推荐加锁的应用的使用信息<br>
 * 
 * @author laojiale
 *
 */
public class RecommendLockAppUsageBean {

	/**
	 * 应用包名
	 */
	private String mPackageName;

	/**
	 * 界面打开的次数
	 */
	private int mActivityOnTopCount;

	public String getPackageName() {
		return mPackageName;
	}

	public void setPackageName(String packageName) {
		mPackageName = packageName;
	}

	public int getActivityOnTopCount() {
		return mActivityOnTopCount;
	}

	public void setActivityOnTopCount(int activityOnTopCount) {
		mActivityOnTopCount = activityOnTopCount;
	}

	/**
	 * 比较器
	 * @author laojiale
	 *
	 */
	public static class RecommendLockAppUsageBeanComparator implements
			Comparator<RecommendLockAppUsageBean> {

		/**
		 * 由小到大排列的
		 */
		@Override
		public int compare(RecommendLockAppUsageBean lApp,
				RecommendLockAppUsageBean rApp) {
			int lLevel = 0;
			int rLevel = 0;

			// 次数多的放到最后面
			if (lApp.getActivityOnTopCount() > rApp.getActivityOnTopCount()) {
				lLevel++;
			} else if (lApp.getActivityOnTopCount() < rApp
					.getActivityOnTopCount()) {
				rLevel++;
			}

			int result = 0;
			if (lLevel < rLevel) {
				result = -1;
			} else if (lLevel > rLevel) {
				result = 1;
			} else {
				result = 0;
			}
			return result;
		}
	}

}
