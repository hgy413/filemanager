package com.jb.filemanager.manager;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.docmanager.DocGroupBean;
import com.jb.filemanager.function.image.modle.ImageModle;
import com.jb.filemanager.function.recent.bean.BlockBean;
import com.jb.filemanager.function.samefile.FileInfo;
import com.jb.filemanager.function.samefile.GroupList;
import com.jb.filemanager.function.zipfile.bean.ZipFileItemBean;
import com.jb.filemanager.util.StorageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    private BroadcastReceiver mReceiver;
    // 数据存储区 : 压缩文件 视频文件 音频文件 图片文件 ---- 最近文件 下载文件 文档文件

    // 压缩文件
    private List<ZipFileItemBean> mZipFileList = new ArrayList<>();
    // 图片文件
    private List<ImageModle> mImageList = new ArrayList<>();
    // 视频文件
    private GroupList<String,FileInfo> mVideoList = new GroupList<String,FileInfo>();
    // 音频文件
    private GroupList<String,FileInfo> mAudioList = new GroupList<String,FileInfo>();

    // 最近文件
    private List<BlockBean> mRecentList = new ArrayList<>();
    // 下载文件
    private GroupList<String,FileInfo> mDownloadList = new GroupList<String,FileInfo>();
    // 下载文件
    private List<DocGroupBean> mDocumentList = new ArrayList<>();

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

    public void onApplicationCreate() {
//        initAllData();
    }

    // MediaStore.Audio Video Images Files
    // http://blog.csdn.net/ifmylove2011/article/details/51425921
    private void initAllData() {
        long start = System.currentTimeMillis();
        Log.e("time", "start=" + start);
        ContentResolver resolver = mContext.getContentResolver();
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Uri audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri filesUri = MediaStore.Files.getContentUri("external");

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
        Log.e("time", "time=" + (System.currentTimeMillis() - start));
    }

    /**
     * 根据文件扩展名获取MIME_TYPE
     *
     * @param extension e
     * @return s
     */
    public String getMimeType(String extension) {
        if (!TextUtils.isEmpty(extension)) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return null;
    }

    public void sendUpdateBroadcast(File f, String[] mimeType) {
        if (isSupportUpdateDirect()) { // 判断SDK版本是不是4.4或者高于4.4
            String[] paths = getExternalPaths();

            MediaScannerConnection.scanFile(mContext, paths, mimeType, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    Log.e("global", "4.4以及4.4以上扫描完成" + path);
                }
            });
        } else {
            registerMediaScannerReceiver();
            final Intent intent;
            if (f.isDirectory()) {
                intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
                intent.setClassName("com.android.providers.media", "com.android.providers.media.MediaScannerReceiver");
                intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
                Log.e("global", "directory changed, send broadcast:" + intent.toString());
            } else {
                intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(f));
                Log.e("global", "file changed, send broadcast:" + intent.toString());
            }
            mContext.sendBroadcast(intent);
        }
    }

    private String[] getExternalPaths() {
        Set<String> allExternalPaths = StorageUtil.getAllExternalPaths(mContext);
        String[] paths = new String[allExternalPaths.size()];
        allExternalPaths.toArray(paths);
        return paths;
    }

    /**
     * 是否支持不通过广播的方式直接刷新
     *
     * @return true if support
     */
    private boolean isSupportUpdateDirect() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    private void registerMediaScannerReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("global", "MediaScannerReceiver");
                String action = intent.getAction();
                if (Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action)) {
                    Log.e("global", "MediaScannerReceiver ACTION_MEDIA_SCANNER_STARTED");
                } else if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {
                    Log.e("global", "MediaScannerReceiver ACTION_MEDIA_SCANNER_FINISHED");
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        mContext.registerReceiver(mReceiver, filter);
    }

    public void onApplicationTerminate() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
    }
}
