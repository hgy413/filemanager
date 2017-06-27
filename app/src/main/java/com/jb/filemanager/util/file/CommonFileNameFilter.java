package com.jb.filemanager.util.file;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 通用文件名过滤器
 *
 * @author chenbenbin
 */
public class CommonFileNameFilter implements FilenameFilter {

    private final String[] mExtension;

    public CommonFileNameFilter(String... extension) {
        mExtension = extension;
    }

    @Override
    public boolean accept(File dir, String filename) {
        boolean accept = false;
        String extension = FileUtil.getExtension(filename).toLowerCase();
        for (String s : mExtension) {
            accept |= s.equals(extension);
        }
        return accept;
    }
}
