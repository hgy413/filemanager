package com.jiubang.commerce.utils;

import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    private static final String LOG_TAG = "appcenter_file";

    public static void mkDir(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
            }
        }
    }

    public static File createNewFile(String path, boolean append) {
        File newFile = new File(path);
        if (!append) {
            if (newFile.exists()) {
                newFile.delete();
            } else {
                File prePngFile = new File(path + ".png");
                if (prePngFile != null && prePngFile.exists()) {
                    prePngFile.delete();
                }
            }
        }
        if (!newFile.exists()) {
            try {
                File parent = newFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                newFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return newFile;
    }

    public static boolean createFile(String destFileName, boolean replace) {
        File file = new File(destFileName);
        if (file.exists()) {
            if (replace) {
                file.delete();
            } else if (!LogUtils.isShowLog()) {
                return false;
            } else {
                LogUtils.d(LOG_TAG, "创建单个文件" + destFileName + "失败，目标文件已存在！");
                return false;
            }
        }
        if (!destFileName.endsWith(File.separator)) {
            if (!file.getParentFile().exists()) {
                if (LogUtils.isShowLog()) {
                    LogUtils.d(LOG_TAG, "目标文件所在路径不存在，准备创建。。。");
                }
                if (!file.getParentFile().mkdirs()) {
                    if (!LogUtils.isShowLog()) {
                        return false;
                    }
                    LogUtils.d(LOG_TAG, "创建目录文件所在的目录失败！");
                    return false;
                }
            }
            try {
                if (file.createNewFile()) {
                    if (LogUtils.isShowLog()) {
                        LogUtils.d(LOG_TAG, "创建单个文件" + destFileName + "成功！");
                    }
                    return true;
                } else if (!LogUtils.isShowLog()) {
                    return false;
                } else {
                    LogUtils.d(LOG_TAG, "创建单个文件" + destFileName + "失败！");
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (!LogUtils.isShowLog()) {
                    return false;
                }
                LogUtils.d(LOG_TAG, "创建单个文件" + destFileName + "失败！");
                return false;
            }
        } else if (!LogUtils.isShowLog()) {
            return false;
        } else {
            LogUtils.d(LOG_TAG, "创建单个文件" + destFileName + "失败，目标不能是目录！");
            return false;
        }
    }

    public static boolean saveStringToSDFile(String string, String fileName) {
        if (TextUtils.isEmpty(string)) {
            return false;
        }
        try {
            return saveByteToSDFile(string.getBytes("UTF-8"), fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean saveByteToSDFile(byte[] byteData, String filePathName) {
        if (byteData == null || TextUtils.isEmpty(filePathName)) {
            return false;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(createNewFile(filePathName, false));
            fileOutputStream.write(byteData);
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (SecurityException e2) {
            e2.printStackTrace();
            return false;
        } catch (IOException e3) {
            e3.printStackTrace();
            return false;
        } catch (Exception e4) {
            e4.printStackTrace();
            return false;
        }
    }

    public static boolean saveInputStreamToSDFile(InputStream inputStream, String filePathName) {
        boolean result = false;
        OutputStream os = null;
        try {
            OutputStream os2 = new FileOutputStream(createNewFile(filePathName, false));
            try {
                byte[] buffer = new byte[4096];
                while (true) {
                    int len = inputStream.read(buffer);
                    if (len == -1) {
                        break;
                    }
                    os2.write(buffer, 0, len);
                }
                os2.flush();
                result = true;
                try {
                    os2.close();
                    OutputStream outputStream = os2;
                } catch (Exception e) {
                    e.printStackTrace();
                    OutputStream outputStream2 = os2;
                }
            } catch (Exception e2) {
                e = e2;
                os = os2;
                try {
                    e.printStackTrace();
                    try {
                        os.close();
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                    return result;
                } catch (Throwable th) {
                    th = th;
                    try {
                        os.close();
                    } catch (Exception e4) {
                        e4.printStackTrace();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                os = os2;
                os.close();
                throw th;
            }
        } catch (Exception e5) {
            e = e5;
            e.printStackTrace();
            os.close();
            return result;
        }
        return result;
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x0052 A[SYNTHETIC, Splitter:B:31:0x0052] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0057 A[SYNTHETIC, Splitter:B:34:0x0057] */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0064 A[SYNTHETIC, Splitter:B:41:0x0064] */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0069 A[SYNTHETIC, Splitter:B:44:0x0069] */
    /* JADX WARNING: Removed duplicated region for block: B:63:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void copyFile(java.lang.String r12, java.lang.String r13) {
        /*
            java.io.File r9 = new java.io.File
            r9.<init>(r12)
            boolean r10 = r9.exists()
            if (r10 != 0) goto L_0x000c
        L_0x000b:
            return
        L_0x000c:
            java.io.File r1 = new java.io.File
            r1.<init>(r13)
            boolean r10 = r1.exists()
            if (r10 != 0) goto L_0x0021
            java.io.File r8 = r1.getParentFile()
            r8.mkdirs()
            r1.createNewFile()     // Catch:{ Exception -> 0x0044 }
        L_0x0021:
            r3 = 0
            r6 = 0
            java.io.FileInputStream r4 = new java.io.FileInputStream     // Catch:{ Exception -> 0x007c, all -> 0x0061 }
            r4.<init>(r9)     // Catch:{ Exception -> 0x007c, all -> 0x0061 }
            java.io.FileOutputStream r7 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x007e, all -> 0x0075 }
            r7.<init>(r1)     // Catch:{ Exception -> 0x007e, all -> 0x0075 }
            r10 = 4096(0x1000, float:5.74E-42)
            byte[] r0 = new byte[r10]     // Catch:{ Exception -> 0x004d, all -> 0x0078 }
        L_0x0031:
            int r5 = r4.read(r0)     // Catch:{ Exception -> 0x004d, all -> 0x0078 }
            if (r5 > 0) goto L_0x0049
            if (r4 == 0) goto L_0x003c
            r4.close()     // Catch:{ Exception -> 0x006d }
        L_0x003c:
            if (r7 == 0) goto L_0x0081
            r7.close()     // Catch:{ Exception -> 0x005d }
            r6 = r7
            r3 = r4
            goto L_0x000b
        L_0x0044:
            r2 = move-exception
            r2.printStackTrace()
            goto L_0x000b
        L_0x0049:
            r7.write(r0)     // Catch:{ Exception -> 0x004d, all -> 0x0078 }
            goto L_0x0031
        L_0x004d:
            r10 = move-exception
            r6 = r7
            r3 = r4
        L_0x0050:
            if (r3 == 0) goto L_0x0055
            r3.close()     // Catch:{ Exception -> 0x006f }
        L_0x0055:
            if (r6 == 0) goto L_0x000b
            r6.close()     // Catch:{ Exception -> 0x005b }
            goto L_0x000b
        L_0x005b:
            r10 = move-exception
            goto L_0x000b
        L_0x005d:
            r10 = move-exception
            r6 = r7
            r3 = r4
            goto L_0x000b
        L_0x0061:
            r10 = move-exception
        L_0x0062:
            if (r3 == 0) goto L_0x0067
            r3.close()     // Catch:{ Exception -> 0x0071 }
        L_0x0067:
            if (r6 == 0) goto L_0x006c
            r6.close()     // Catch:{ Exception -> 0x0073 }
        L_0x006c:
            throw r10
        L_0x006d:
            r10 = move-exception
            goto L_0x003c
        L_0x006f:
            r10 = move-exception
            goto L_0x0055
        L_0x0071:
            r11 = move-exception
            goto L_0x0067
        L_0x0073:
            r11 = move-exception
            goto L_0x006c
        L_0x0075:
            r10 = move-exception
            r3 = r4
            goto L_0x0062
        L_0x0078:
            r10 = move-exception
            r6 = r7
            r3 = r4
            goto L_0x0062
        L_0x007c:
            r10 = move-exception
            goto L_0x0050
        L_0x007e:
            r10 = move-exception
            r3 = r4
            goto L_0x0050
        L_0x0081:
            r6 = r7
            r3 = r4
            goto L_0x000b
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.utils.FileUtils.copyFile(java.lang.String, java.lang.String):void");
    }

    public static byte[] readByteFromSDFile(String filePathName) {
        byte[] bs = null;
        try {
            File newFile = new File(filePathName);
            FileInputStream fileInputStream = new FileInputStream(newFile);
            BufferedInputStream inPutStream = new BufferedInputStream(new DataInputStream(fileInputStream));
            bs = new byte[((int) newFile.length())];
            inPutStream.read(bs);
            fileInputStream.close();
            return bs;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return bs;
        } catch (SecurityException e2) {
            e2.printStackTrace();
            return bs;
        } catch (IOException e3) {
            e3.printStackTrace();
            return bs;
        } catch (Exception e4) {
            e4.printStackTrace();
            return bs;
        }
    }

    public static String readFileToString(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        try {
            return readInputStream(new FileInputStream(file), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String readInputStream(InputStream in, String charset) throws IOException {
        if (in == null) {
            return "";
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buf = new byte[1024];
            while (true) {
                int len = in.read(buf);
                if (len <= 0) {
                    break;
                }
                out.write(buf, 0, len);
            }
            byte[] data = out.toByteArray();
            if (TextUtils.isEmpty(charset)) {
                charset = "UTF-8";
            }
            String str = new String(data, charset);
            if (in != null) {
                in.close();
            }
            if (out == null) {
                return str;
            }
            out.close();
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            return null;
        } catch (Throwable th) {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            throw th;
        }
    }

    public static String readInputStreamWithLength(InputStream in, String charset, int length) throws IOException {
        if (in == null) {
            return "";
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buf = new byte[1024];
            int i = 0;
            while (true) {
                int len = in.read(buf);
                if (len <= 0 || i >= length) {
                    byte[] data = out.toByteArray();
                } else {
                    out.write(buf, 0, len);
                    i++;
                }
            }
            byte[] data2 = out.toByteArray();
            if (TextUtils.isEmpty(charset)) {
                charset = "UTF-8";
            }
            String str = new String(data2, charset);
            if (in != null) {
                in.close();
            }
            if (out == null) {
                return str;
            }
            out.close();
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            return null;
        } catch (Throwable th) {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            throw th;
        }
    }

    public static boolean deleteDirectory(String sPath) {
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (!files[i].isFile()) {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            } else {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag || !dirFile.delete()) {
            return false;
        }
        return true;
    }

    public static boolean deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static boolean isFileExist(String filePath) {
        try {
            return new File(filePath).exists();
        } catch (Exception e) {
            return false;
        }
    }

    public static long getFileSize(String path) {
        if (path != null) {
            return new File(path).length();
        }
        return 0;
    }
}
