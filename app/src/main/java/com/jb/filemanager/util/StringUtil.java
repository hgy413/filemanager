package com.jb.filemanager.util;

import android.text.SpannableString;
import android.text.style.LeadingMarginSpan;

/**
 * 字符类辅助工具类
 *
 * @author chenhewen
 */
public class StringUtil {

    /**
     * 首行缩进
     *
     * @param text            要展示的文本
     * @param marginFirstLine 首行缩进值
     * @param marginNextLines 其余行缩进值
     * @return result
     */
    public static SpannableString createIndentedText(String text, int marginFirstLine, int marginNextLines) {
        SpannableString result = new SpannableString(text);
        result.setSpan(new LeadingMarginSpan.Standard(marginFirstLine, marginNextLines), 0,
                text.length(), 0);
        return result;
    }

    /**
     * 去除字符串中多余的空格, 只保留一个<br>
     * 如"abc  de f", 处理结果"abc de f"
     *
     * @param str string
     * @return result
     */
    public static String trimExtraSpace(String str) {
        String strTrim = str.trim();
        char lastChar = ' ';
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strTrim.length(); i++) {
            char c = strTrim.charAt(i);
            if (c == lastChar && c == ' ') {
                continue;
            } else {
                sb.append(c);
            }
            lastChar = c;
        }
        return sb.toString();
    }
}