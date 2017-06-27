package com.jb.filemanager.util.file;

import android.text.TextUtils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 通配符拓展名文件过滤器
 *
 * @author chenbenbin
 */
public class WildcardExtensionFileNameFilter implements FilenameFilter {

    private final String[] mExtension;

    public WildcardExtensionFileNameFilter(String... extension) {
        mExtension = extension;
    }

    @Override
    public boolean accept(File dir, String filename) {
        boolean accept = false;
        String extension = FileUtil.getExtension(filename).toLowerCase();
        if (TextUtils.isEmpty(extension)) {
            return false;
        }
        for (String s : mExtension) {
            accept |= extension.matches(s);
        }
        return accept;
    }
}

