package com.jb.filemanager.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtil {

    public static final SimpleDateFormat DEFAULT_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
    public static final SimpleDateFormat DATE_FORMATTER_DATE = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    public static final SimpleDateFormat DATE_FORMATTER_HOUR = new SimpleDateFormat("mm:ss", Locale.US);
    public static final SimpleDateFormat DATE_FORMATTER_SHORT = new SimpleDateFormat("HH:mm", Locale.US);
    public static final SimpleDateFormat DATA_SHORT_FORMATTER_DATE = new SimpleDateFormat("yyyyMMdd", Locale.US);
    public static final SimpleDateFormat DATE_FORMATTER_MM_SS = new SimpleDateFormat("mm:ss", Locale.US);
    public static final SimpleDateFormat DATE_FORMATTER_FILE_LAST_MODIFY = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);


    public static final long MILLIS_MINUTE = 60 * 1000L;
    public static final long MILLIS_HOUR = 60 * MILLIS_MINUTE;
    public static final int SECONDS_IN_DAY = 60 * 60 * 24;
    public static final long MILLIS_IN_DAY = 1000L * SECONDS_IN_DAY;

    private TimeUtil() {
        throw new AssertionError();
    }

    public static boolean isSameDayOfMillis(final long ms1, final long ms2) {
        final long interval = ms1 - ms2;
        return interval < MILLIS_IN_DAY
                && interval > -1L * MILLIS_IN_DAY
                && toDay(ms1) == toDay(ms2);
    }

    private static long toDay(long millis) {
        return (millis + TimeZone.getDefault().getOffset(millis)) / MILLIS_IN_DAY;
    }

    /**
     * long time to string
     *
     * @param timeInMillis time in ms
     * @param dateFormat   format
     * @return time string format by dateFormat
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    /**
     * long time to string, format is {@link #DEFAULT_DATE_FORMATTER}
     *
     * @param timeInMillis time in ms
     * @return time string format by {@link #DEFAULT_DATE_FORMATTER}
     */
    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, DEFAULT_DATE_FORMATTER);
    }

    /**
     * get current time in milliseconds
     *
     * @return current time in ms
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    /**
     * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMATTER}
     *
     * @return time string format by {@link #DEFAULT_DATE_FORMATTER}
     */
    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    /**
     * get current time in milliseconds
     *
     * @return time string format by dateFormat
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }

    //1.12

    /**
     * 以此来计算两个时间相差天数
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 相差天数  0代表同一天
     */
    public static int calcDifferenceDays(long startTime, long endTime) throws Exception {
        if (startTime < 0 || endTime < 0)
            throw new Exception("startTime or endTime < 0");
        return Math.abs((int) (toDay(startTime) - toDay(endTime)));
    }

    /**
     * 将输入的日期对象装换成,yyyy-MM-dd, 然后返回对应的毫秒值
     *
     * @param date date
     * @return result
     */
    public static long getLongWithYMD(Date date) {
        long result = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formatDate = sdf.format(date);
        try {
            Date parseDate = sdf.parse(formatDate);
            result = parseDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取传入date的前一天的Date对象
     * 即, 传入2016-08-26 00:00:00  返回2016-08-25 00:00:00
     *
     * @param date date
     * @return result
     */
    public static Date getPreviousDay(Date date) {
        return getPreviousDayByIndex(date, 1);
    }

    /**
     * 根据传入的开始计数日期, 和查询时的日期, 返回
     *
     * @param startOfPeriod 开始计数日
     * @param currentDate   此时日期
     * @return 要查询数据的日期的范围
     */
    public static long[] getTwoEdgeDays(int startOfPeriod, Date currentDate) {
        long[] result = new long[2];
        result[1] = getLongWithYMD(currentDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        int todayIndex = calendar.get(Calendar.DAY_OF_MONTH);
        if (startOfPeriod > todayIndex) {
            // 定位到上个月, 判断
            int currMonth = calendar.get(Calendar.MONTH);
            calendar.set(Calendar.MONTH, currMonth - 1);
            calendar.set(Calendar.DAY_OF_MONTH, startOfPeriod);
            int dayAfter = calendar.get(Calendar.DAY_OF_MONTH);
            if (dayAfter != startOfPeriod) {
                // 如果上月没有startOfPeriod, 则定位到上月最后一天
                calendar.set(Calendar.MONTH, currMonth);
                calendar.set(Calendar.DAY_OF_MONTH, 0);
            }
            Date time = calendar.getTime();
            result[0] = getLongWithYMD(time);
            return result;
        } else if (startOfPeriod <= todayIndex) {
            // 本月肯定存在startOfPeriod
            calendar.set(Calendar.DAY_OF_MONTH, startOfPeriod);
            Date time = calendar.getTime();
            result[0] = getLongWithYMD(time);
            return result;
        }
        return null;
    }

    /**
     * 获取计数周期内的剩余天数
     * 查到renew的前一天
     *
     * @param startDay startDay
     * @param date date
     */
    public static int getRemainDays(int startDay, Date date) {
        int result = -1;
        long fromDay = getLongWithYMD(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int currDayInt = calendar.get(Calendar.DAY_OF_MONTH);
        int currMonth = calendar.get(Calendar.MONTH);
        if (startDay <= currDayInt) {
            calendar.set(Calendar.MONTH, currMonth + 1);
            calendar.set(Calendar.DAY_OF_MONTH, startDay);
            Date time = calendar.getTime();
            long toDay = getLongWithYMD(time);
            result = (int) ((toDay - fromDay) / (1000 * 60 * 60 * 24));
        } else if (startDay > currDayInt) {
            calendar.set(Calendar.DAY_OF_MONTH, startDay);
            Date time = calendar.getTime();
            long toDay = getLongWithYMD(time);
            result = (int) ((toDay - fromDay) / (1000 * 60 * 60 * 24));
        }
        return result;
    }

    /*
     * 获取格式短的时间
     * */
    public static String getShortTime() {
        return getCurrentTimeInString(DATE_FORMATTER_SHORT);
    }


    /**
     * 获取格式为"yyyy/MM/dd"的日期
     *
     * @param date date
     */
    public static String getYandMandD(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * 获取格式为"2016/08/30 Tuesday"的日期
     *
     * @param date date
     * @return result
     */
    public static String getStringDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd E", Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * 根据i值, 返回后退的天数
     *
     * @param date date
     * @param i i
     * @return result
     */
    public static Date getPreviousDayByIndex(Date date, int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int preDay = calendar.get(Calendar.DAY_OF_MONTH) - i;
        calendar.set(Calendar.DAY_OF_MONTH, preDay);
        return calendar.getTime();
    }

    /**
     * 根据每月开始周期startDay, 和当前日期date, 获取开始日期date, 返回
     * 用于显示月排行的日期
     *
     * @param startDay startDay
     * @param date date
     * @return result
     */
    public static String getStartDayOfString(int startDay, Date date) {
        String result;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int currDay = calendar.get(Calendar.DAY_OF_MONTH);
        if (startDay <= currDay) {
            calendar.set(Calendar.DAY_OF_MONTH, startDay);
            result = getYandMandD(calendar.getTime());
        } else {
            int currMon = calendar.get(Calendar.MONTH);
            calendar.set(Calendar.MONTH, currMon - 1);
            calendar.set(Calendar.DAY_OF_MONTH, startDay);
            result = getYandMandD(calendar.getTime());
        }
        return result;
    }

    /**
     * 将SSL里的时间转换成long
     * Jul  9 18:19:22 2019 GMT
     *
     * @param certTime certTime
     * @return result
     */
    public static long getLongCertDate(String certTime) {
        long result = new Date().getTime();
        certTime = StringUtil.trimExtraSpace(certTime);
        String[] time = certTime.split(" ");
        String day = time[1]; // 日
        String timeInfo = time[2]; // 时间
        String year = time[3]; // 年
        String month = time[0];
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MMM-d-hh:mm:ss", new Locale("en"));
        try {
            Date parse = simpleDateFormat.parse(year + "-" + month + "-" + day + "-" + timeInfo);
            result = parse.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取时间区间, 如传入10.8, 返回10.1-10.31
     *
     * @param date date
     */
    public static String getPeriodTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = calendar.getTime();
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        Date endTime = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("MM.dd", Locale.getDefault());

        return format.format(startDate) + "-" + format.format(endTime);
    }

    /**
     * 获取两端的日期
     *
     * @param date date
     * @return result
     */
    public static String[] getTwoEdgeDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        String[] result = new String[2];
        result[0] = (month + 1) + ".1";
        if (month == 1) {
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
            calendar.set(Calendar.DAY_OF_MONTH, 0);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            result[1] = (month + 1) + "." + day;
        } else {
            result[1] = (month + 1) + ".29";
        }
        return result;
    }

    /**
     * 根据穿日日期获取当前的天的角标
     *
     * @param date date
     */
    public static int getCurrentDayIndex(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 根据穿日日期获取当前的天的角标
     *
     * @param date date
     */
    public static int getCurrentDayIndex(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(date));
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static String getDateByMillisecond(long millisecond) {
        return DATE_FORMATTER_SHORT.format(new Date(millisecond));
    }

    /**
     * 通过毫秒获取时间描述
     * */
    public static String getDescByMillisecond(long millisecond) {
        if (millisecond < 1000) {
            return "0s";
        } else {
            int second = (int) (millisecond / 1000);
            if (second > 60) {
                int minute = second / 60;
                if (minute > 60) {
                    int hour = minute / 60;
                    return hour + "h";
                } else {
                    return minute + "min";
                }
            } else {
                return second + "s";
            }
        }
    }

    public static long getStartMillsInDay(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    /*
     * 获取分秒时间
     * */
    public static String getMSTime(long time) {
        return getTime(time, DATE_FORMATTER_MM_SS);
    }

    /**
     * 获取传入时间的年月日的时间值
     * @param time t
     * @return l
     */
    public static long getYMDTime(long time) {
        String format = DATE_FORMATTER_DATE.format(new Date(time));
        try {
            Date parse = DATE_FORMATTER_DATE.parse(format);
            return parse.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}