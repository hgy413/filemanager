package com.jb.filemanager.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by xiaoyu on 2016/10/17.
 */

public class ConvertUtils {

    private static final String[] FILE_SIZE_UNITS_BYTES = {"B", "KB", "MB", "GB", "TB"};
    private static final String[] FILE_SIZE_UNITS_BIT = {"b", "Kb", "Mb", "Gb", "Tb"};


    static public int boolean2int(boolean booleanValue) {
        if (booleanValue) {
            return 1;
        }
        return 0;
    }

    /**
     * 将传入的数据转化成其他单位的流量</p>
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
        if (traffic > 1024) {
            traffic /= 1024;
            unit = "KB";
            fraction = 1;
            if (traffic > 100) {
                fraction = 0;
            }
        }
        if (traffic > 1024) {
            traffic /= 1024;
            unit = "MB";
            fraction = 1;
            if (traffic > 100) {
                fraction = 0;
            }
        }
        if (traffic > 1024) {
            traffic /= 1024;
            unit = "GB";
            fraction = 2;
            if (traffic > 10) {
                fraction = 1;
            }
            if (traffic > 100) {
                fraction = 0;
            }
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
     * 格式转换  产生一个不大于999的数字   防止出现文字过长的问题
     * @param traffic
     * @return
     */
    public static String[] getFormatterTrafficForShorter(double traffic) {
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
        if (traffic > 999) {
            traffic /= 999;
            unit = "KB";
            fraction = 1;
            if (traffic > 100) {
                fraction = 0;
            }
        }
        if (traffic > 999) {
            traffic /= 999;
            unit = "MB";
            fraction = 1;
            if (traffic > 100) {
                fraction = 0;
            }
        }
        if (traffic > 999) {
            traffic /= 999;
            unit = "GB";
            fraction = 2;
            if (traffic > 10) {
                fraction = 1;
            }
            if (traffic > 100) {
                fraction = 0;
            }
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

    public static String formatFileSize(long size) {
        String[] result = getFormatterTraffic(size);
        return result[0] + result[1];
    }

    /**
     * 将传入的数据转化成其他单位的存储</p>
     *
     * @param storage 要转化的数据, 单位为B
     * @return 包含数据和单位的String数组
     */
    public static String[] getFormatterStorage(double storage) {
        if (storage == 0) {
            return new String[]{"0", "B"};
        }
        boolean isNegative = false;
        int fraction = 0;
        String unit = "B";
        if (storage < 0) {
            isNegative = true;
            storage = Math.abs(storage);
        }
        if (storage > 1024) {
            storage /= 1024;
            unit = "KB";
            fraction = 2;
            if (storage > 100) {
                fraction = 0;
            }
        }
        if (storage > 1024) {
            storage /= 1024;
            unit = "MB";
            fraction = 2;
            if (storage > 100) {
                fraction = 0;
            }
        }
        if (storage > 1024) {
            storage /= 1024;
            unit = "GB";
            fraction = 2;
            if (storage > 100) {
                fraction = 0;
            }
        }
        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
        format.setMaximumFractionDigits(fraction);
        format.setMinimumFractionDigits(fraction);
        String trafficStr = format.format(storage);
        String[] result = new String[]{trafficStr, unit};
        if (isNegative) {
            result[0] = "-" + result[0];
        }
        return result;
    }

    public static String formatStorageSize(long size) {
        String[] result = getFormatterStorage(size);
        return result[0] + result[1];
    }

    /**
     * 将Integer类型的ArrayList转化为int数组
     *
     * @param intObjects
     * @return
     */
    public static int[] toIntArray(ArrayList<Integer> intObjects) {
        final int size = intObjects.size();
        final int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = intObjects.get(i).intValue();
        }
        return array;
    }

    /**
     * 将传入的数据转换成浮点型数值
     *
     * @param value
     * @return
     */
    public static String integer2Float(float value) {
        DecimalFormat format = new DecimalFormat(".0");
        String result = format.format(value / 1000);
        return result.replace(",", ".");
    }

    public static String getReadableSize(long sizeBytes) {
        if (sizeBytes <= 0) return "0 B";
        int digitGroups = (int) (Math.log10(sizeBytes) / Math.log10(1024));
        return String.format(Locale.ENGLISH, "%.1f", sizeBytes / Math.pow(1024, digitGroups)) + " " + FILE_SIZE_UNITS_BYTES[digitGroups];
    }

    public static String getReadableSizeNoSpace(long sizeBytes) {
        if (sizeBytes <= 0) return "0B";
        int digitGroups = (int) (Math.log10(sizeBytes) / Math.log10(1024));
        return String.format(Locale.ENGLISH, "%.1f", sizeBytes / Math.pow(1024, digitGroups)) + FILE_SIZE_UNITS_BYTES[digitGroups];
    }

    public static String getReadableSizeBit(long sizeByte) {
        if (sizeByte <= 0) return "0 b";
        long sizeBit = sizeByte * 8;
        int digitGroups = (int) (Math.log10(sizeBit) / Math.log10(1024));
        return String.format(Locale.ENGLISH, "%.1f", sizeBit / Math.pow(1024, digitGroups)) + " " + FILE_SIZE_UNITS_BIT[digitGroups];
    }

    public static String getFormatterValue(double value) {
        if (value == 0) {
            return String.valueOf(0);
        }
        DecimalFormat decimalFormat = new DecimalFormat(".0");
        return decimalFormat.format(value);
    }

    public static String formatFileSize(double traffic) {
        return getFormatterTraffic(traffic)[0] + getFormatterTraffic(traffic)[1];
    }
}
