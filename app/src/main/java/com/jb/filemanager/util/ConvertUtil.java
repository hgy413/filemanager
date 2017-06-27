package com.jb.filemanager.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by bill wang on 16/8/16.
 * 转换工具类
 */
public class ConvertUtil {

    private static final String[] FILE_SIZE_UNITS_BYTES = {"B", "KB", "MB", "GB", "TB"};
    private static final String[] FILE_SIZE_UNITS_BIT = {"b", "Kb", "Mb", "Gb", "Tb"};

    static public int boolean2int(boolean booleanValue) {
        if (booleanValue) {
            return 1;
        }
        return 0;
    }

    public static BitmapDrawable createBitmap(Context contexts, byte[] data) {
        if (null == data || data.length <= 0) {
            return null;
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        Bitmap bitmap = BitmapFactory.decodeStream(bis);
        return new BitmapDrawable(contexts.getResources(), bitmap);
    }

    /**
     * 将Integer类型的ArrayList转化为int数组
     *
     * @param intObjects int list
     * @return result
     */
    public static int[] toIntArray(ArrayList<Integer> intObjects) {
        final int size = intObjects.size();
        final int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = intObjects.get(i);
        }
        return array;
    }

    public static String getReadableSize(long sizeBytes) {
        if (sizeBytes <= 0) return "0 B";
        int digitGroups = (int) (Math.log10(sizeBytes) / Math.log10(1024));
        return String.format(Locale.ENGLISH, "%.1f", sizeBytes / Math.pow(1024, digitGroups)) + " " + FILE_SIZE_UNITS_BYTES[digitGroups];
    }

    public static String getReadableSizeBit(long sizeByte) {
        if (sizeByte <= 0) return "0 b";
        long sizeBit = sizeByte * 8;
        int digitGroups = (int) (Math.log10(sizeBit) / Math.log10(1024));
        String result;
        try {
            result = String.format(Locale.ENGLISH, "%.1f", sizeBit / Math.pow(1024, digitGroups)) + " " + FILE_SIZE_UNITS_BIT[digitGroups];
        }catch (Exception e){
            result = "0 B";
        }
        return result;
    }

    /**
     * 将传入的long型B单位数据转化成其他单位的流量</p>
     *
     * @param traffic 要转化的数据, 单位为B
     * @return 包含数据和单位的String数组
     * @author xiaoyu
     * @date 2016/9/7
     */
    public static String[] getFormatterTraffic(double traffic) {
        if (traffic == 0) {
            return new String[]{"0", "B"};
        }
        boolean isNegative = false;
        int fraction = 0;
        String unit = "B";
        if (traffic < 0) {
            isNegative = true;
            traffic = Math.abs(traffic);
        }
        if (traffic > 900) {
            traffic /= 1024;
            unit = "KB";
            fraction = 1;
        }
        if (traffic > 900) {
            traffic /= 1024;
            unit = "MB";
            fraction = 1;
        }
        if (traffic > 900) {
            traffic /= 1024;
            unit = "GB";
            fraction = 1;
        }
        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
        format.setMaximumFractionDigits(fraction);
        format.setMinimumFractionDigits(fraction);
        String trafficStr = format.format(traffic);
        String[] result = new String[]{trafficStr, unit};
        if (isNegative) {
            result[0] = "-" + result[0];
        }
        return result;
    }

    /**
     * 将传入的long型b单位数据转化成其他单位的流量</p>
     *
     * @param traffic 要转化的数据, 单位为b 字节数
     * @return 包含数据和单位的String数组
     */
    public static String[] getFormatterTrafficSmallUnits(double traffic) {
        traffic = traffic * 8;
        if (traffic == 0) {
            return new String[]{"0", "b"};
        }
        boolean isNegative = false;
        int fraction = 0;
        String unit = "b";
        if (traffic < 0) {
            isNegative = true;
            traffic = Math.abs(traffic);
        }
        if (traffic > 900) {
            traffic /= 1024;
            unit = "Kb";
            fraction = 1;
        }
        if (traffic > 900) {
            traffic /= 1024;
            unit = "Mb";
            fraction = 1;
        }
        if (traffic > 900) {
            traffic /= 1024;
            unit = "Gb";
            fraction = 1;
        }
        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
        format.setMaximumFractionDigits(fraction);
        format.setMinimumFractionDigits(fraction);
        String trafficStr = format.format(traffic);
        String[] result = new String[]{trafficStr, unit};
        if (isNegative) {
            result[0] = "-" + result[0];
        }
        return result;
    }

    /**
     * 将传入的数据 取单位左边的数值
     */
    public static double getFormatterTrafficValue(double traffic) {
        if (traffic == 0) {
            return 0;
        }
        boolean isNegative = false;
        if (traffic < 0) {
            isNegative = true;
            traffic = Math.abs(traffic);
        }
        if (traffic > 900) {
            traffic /= 1024;
        }
        if (traffic > 900) {
            traffic /= 1024;
        }
        if (traffic > 900) {
            traffic /= 1024;
        }
        if (isNegative) {
            return -traffic;
        }
        return traffic;
    }

    public static String formatFileSize(double traffic) {
        return getFormatterTraffic(traffic)[0] + getFormatterTraffic(traffic)[1];
    }

    /**
     * 将数据转化成MB 或者是 GB
     * 返回 : 数据 + 单位
     * 0 或 负数返回 0MB
     *
     * @param totalTraffic
     * @return
     */
    public static String[] getFormatterTrafficForBR(double totalTraffic) {
        String[] result = new String[2];
        result[1] = "MB";
        int fraction = 0;
        if (totalTraffic <= 0) {
            result[0] = String.valueOf(0);
        } else {
            totalTraffic /= (1024 * 1024);
            if (totalTraffic > 900) {
                totalTraffic /= 1024;
                result[1] = "GB";
            }
            DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
            format.setMaximumFractionDigits(fraction);
            format.setMinimumFractionDigits(fraction);
            result[0] = format.format(totalTraffic);
        }
        return result;
    }


    /**
     * 将数据转化成MB
     * 返回 : 数据大小（单位MB）
     * 0 或 负数返回 0MB
     *
     * @param size 大小（单位KB）
     * @return result
     */
    public static String getReadableSizeMBFromKB(double size) {
        String result;
        int fraction = 0;
        if (size <= 0) {
            result = "0MB";
        } else {
            size /= 1024;
            DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
            format.setMaximumFractionDigits(fraction);
            format.setMinimumFractionDigits(fraction);
            result = format.format(size) + "MB";
        }
        return result;
    }

}