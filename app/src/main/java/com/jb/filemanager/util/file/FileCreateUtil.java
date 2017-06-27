package com.jb.filemanager.util.file;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件创建工具类
 *
 * @author chenbenbin
 */
public class FileCreateUtil {

    public static void createFileFromString(File file, String value) {
        createFileFromByte(file, value.getBytes());
    }

    public static void createFileFromByte(File file, byte[] bytes) {
        FileOutputStream fileOutputStream = null;
        try {
            //判断是否装有SD卡
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                if (!file.exists()) {
                    File parentFile = file.getParentFile();
                    if (!parentFile.exists()) {
                        parentFile.mkdirs();
                    }
                    //创建文件
                    file.createNewFile();
                }
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
