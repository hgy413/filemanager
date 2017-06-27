package com.jb.filemanager.util.file;

import java.io.File;
import java.io.FileFilter;

/**
 * 文件过滤器:只接受文件,不接受文件夹&其他类型
 *
 * @author chenbenbin
 */
public class FileAcceptFilter implements FileFilter {
    @Override
    public boolean accept(File pathname) {
        return pathname.isFile();
    }
}
