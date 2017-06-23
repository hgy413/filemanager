package com.jb.filemanager.util;

import android.os.SystemClock;
import android.util.SparseArray;

/**
 * Created by bill wang on 16/8/19.
 *
 */
public class QuickClickGuard {

    /**
     * 默认的快速点击定义的时间间隔值500毫秒
     */
    private final static long DEFAULT_LIMIT_TIME = 500;

    /**
     * 被定义为快速点击的时间间隔.<br>
     * 若两次点击之间时间小于这这值则认为是快速点击<br>
     * (单位毫秒)默认值 {@link #DEFAULT_LIMIT_TIME}
     */
    private long mLimitTime = DEFAULT_LIMIT_TIME;

    /**
     * 被点击对象的点击时间
     */
    private SparseArray<Long> mClickTimes = new SparseArray<>();
//    private ArrayMap<View, Long> mClickViewTimes = new ArrayMap<>();

    public QuickClickGuard() {

    }

    public QuickClickGuard(long limitTime) {
        mLimitTime = limitTime;
    }

    /**
     * 被定义为快速点击的时间间隔.<br>
     * 若两次点击之间时间小于这这值则认为是快速点击<br>
     * (单位毫秒)默认值 {@link #DEFAULT_LIMIT_TIME}
     */
    public long getLimitTime() {
        return mLimitTime;
    }

    /**
     * 被定义为快速点击的时间间隔.<br>
     * 若两次点击之间时间小于这这值则认为是快速点击<br>
     * (单位毫秒)默认值 {@link #DEFAULT_LIMIT_TIME}
     */
    public void setLimitTime(long limitTime) {
        mLimitTime = limitTime;
    }

    /**
     * 判断一个对象是否被快速点击了.<br>
     * 这个方法必须在点击触发时调用,因为其中会记录当前点击的时间<br>
     *
     * @param clickObjectId 在一定范围内一个对象的唯一id,通常对于view,使用view id作为clickObjectId是个不错的选择,其他情况hashCode是个不错的选择
     */
    public boolean isQuickClick(int clickObjectId) {
        boolean isQuickClick = false;
        long last = mClickTimes.get(clickObjectId, 0L);
        long current = SystemClock.elapsedRealtime();
        if ((last != 0) && (current - last < mLimitTime)) {
            isQuickClick = true;
        }
        mClickTimes.put(clickObjectId, current);
        return isQuickClick;
    }
}