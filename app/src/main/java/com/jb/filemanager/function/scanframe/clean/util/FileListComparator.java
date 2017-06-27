package com.jb.filemanager.function.scanframe.clean.util;


import java.io.File;
import java.util.Comparator;

/**
 * 文件排靠前，文件夹靠后
 *
 * @author chenbenbin
 */
public class FileListComparator implements Comparator<File> {

    @Override
    public int compare(File lhs, File rhs) {
        boolean lIsFolder = lhs.isDirectory();
        boolean rIsFolder = rhs.isDirectory();
        if (rIsFolder && !lIsFolder) {
            return -1;
        } else if (lIsFolder && !rIsFolder) {
            return 1;
        } else {
            return 0;
        }
    }
}