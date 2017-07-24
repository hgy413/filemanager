package com.jb.filemanager.manager;

import android.content.Context;

import com.jb.filemanager.TheApplication;

/**
 * Created by xiaoyu on 2017/7/21 10:36.
 * <p>
 * 该类设计架构应考虑到以下几点:
 * <ol>
 * <li>app启动时全盘<b>全类型</b>扫描一次</li>
 * <li>将不同类型文件的扫描分开</li>
 * <li>定时刷新扫描(<i>待考虑</i>)</li>
 * <li>可<b>单独</b>扫描某个类型的文件</li>
 * <li>可<b>单独</b>扫描某个路径的文件</li>
 * </ol>
 * </p>
 * <p>
 * <p>
 * 文件类型 <b>是否直接采用系统数据库数据或是全部类型都通过扫描获取待定</b>
 * <ul>
 * <li>图片</li>
 * <li>视频</li>
 * <li>音乐</li>
 * <li>文档(txt doc pdf)</li>
 * <li>压缩包(zip rar)</li>
 * </ul>
 * 除了以上这几种具体文件, 还有另外两个概括类型
 * <ul>
 * <li>下载(特定目录下: pdf txt zip music video document other</li>
 * <li>最近文件(所有目录: 只按照目录和最后修改时间排序)</li>
 * </ul>
 * 额外的一项<br>
 * <note>SearchManager, 将数据扫出来存到数据库中</note>
 * </p>
 * <p>
 * <b>刷新系统数据库MediaStore</b>
 * MediaScannerReceiver 会在任何的 ACTION_BOOT_COMPLETED（开机启动完成）, ACTION_MEDIA_MOUNTED（sd卡挂载上）
 * 或 ACTION_MEDIA_SCANNER_SCAN_FILE（文件扫描） 意图（ intent ）发出的时候启动。因为解析媒体文件的元
 * 数据或许会需要很长时间，所以MediaScannerReceiver会启动 MediaScannerService 。
 * MediaScannerService 调用一个公用类 MediaScanner 去处理真正的工作。 MediaScannerReceiver 维持两种扫描
 * 目录：一种是内部卷（ internal volume ）指向 $(ANDROID_ROOT)/media. 另一种是外部卷（ external volume ）
 * 指向 $(EXTERNAL_STORAGE).
 * </p>
 */

public class GlobalFileManager {

    private static GlobalFileManager sInstance;
    private Context mContext;
    // 数据存储区


    private GlobalFileManager() {
        mContext = TheApplication.getAppContext();
    }

    public static GlobalFileManager getInstance() {
        if (sInstance == null) {
            synchronized (GlobalFileManager.class) {
                if (sInstance == null)
                    sInstance = new GlobalFileManager();
            }
        }
        return sInstance;
    }

    // MediaStore.Audio Video Images Files
    // http://blog.csdn.net/ifmylove2011/article/details/51425921
    public void onApplicationCreate() {
       /* long start = System.currentTimeMillis();
        Log.e("time", "start=" + start);
        ContentResolver resolver = mContext.getContentResolver();

        Cursor cursor = resolver.query(
                MediaStore.Files.getContentUri("external"),
                null,
                MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                        + MediaStore.Files.FileColumns.MIME_TYPE + "=?",
                new String[]{MimeTypeMap.getSingleton().getMimeTypeFromExtension("zip"),
                        MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf")},
                null);
//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
//                Log.e("image", "data=" + data);
//            }
//        }
        Log.e("time", "time=" + (System.currentTimeMillis() - start));*/
    }

    public void onApplicationTerminate() {

    }
}
