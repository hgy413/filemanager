package com.jb.filemanager.globaldata;

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
import com.jb.filemanager.globaldata.bean.BaseDataBean;
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

public final class GlobalFileManager implements GlobalScanDBTask.GlobalScanDBListener {

    private static GlobalFileManager sInstance;
    private Context mContext;
    private BroadcastReceiver mReceiver;
    // 数据存储区 : 压缩文件 视频文件 音频文件 图片文件 ---- 最近文件 下载文件 文档文件

    // 压缩文件
    private List<ZipFileItemBean> mZipFileList = new ArrayList<>();
    // 图片文件
    private List<ImageModle> mImageList = new ArrayList<>();
    // 视频文件
    private GroupList<String, FileInfo> mVideoList = new GroupList<String, FileInfo>();
    // 音频文件
    private GroupList<String, FileInfo> mAudioList = new GroupList<String, FileInfo>();

    // 最近文件
    private List<BlockBean> mRecentList = new ArrayList<>();
    // 下载文件 Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    private GroupList<String, FileInfo> mDownloadList = new GroupList<String, FileInfo>();
    // 文档文件
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

    public void loadData() {
        GlobalScanDBTask globalScanDBTask = new GlobalScanDBTask(this);
        globalScanDBTask.execute();
    }

    public void initAllData() {
        long start = System.currentTimeMillis();
        Log.e("time", "start=" + start);
        ContentResolver resolver = mContext.getContentResolver();
//        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//        Uri audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        Uri filesUri = MediaStore.Files.getContentUri("external");
//        String data = MediaStore.Files.FileColumns.DATA;
//        String mimeType = MediaStore.Files.FileColumns.MIME_TYPE;
//        String parent = MediaStore.Files.FileColumns.PARENT;
//        String title = MediaStore.Files.FileColumns.TITLE;
//        String count = MediaStore.Files.FileColumns._COUNT;
//        String id = MediaStore.Files.FileColumns._ID;
//        String dateAdded = MediaStore.Files.FileColumns.DATE_ADDED;
//        String dateModified = MediaStore.Files.FileColumns.DATE_MODIFIED;
        String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        Cursor cursor = resolver.query(
                MediaStore.Files.getContentUri("external"),
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
//                MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
//                        + MediaStore.Files.FileColumns.MIME_TYPE + "=?",
                null,
//                new String[]{MimeTypeMap.getSingleton().getMimeTypeFromExtension("png"),
//                        MimeTypeMap.getSingleton().getMimeTypeFromExtension("jpg")},
                null,
                null);
        Cursor cur = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
        );
        int cc = 0;
//        while (cur.moveToNext()) {
//            cc ++;
//            cur.getString(cur.getColumnIndex(MediaStore.Images.ImageColumns.))
//        }
        if (cursor != null) {
           /* String[] names = cursor.getColumnNames();
            StringBuilder sb = new StringBuilder();
            for (String name : names) {
                sb.append(name);
                sb.append("--");
            }
            Log.e("global", sb.toString());*/
            int c = 0;
            while (cursor.moveToNext()) {
                c++;
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                int parent = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns.PARENT));
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
//                int count = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns._COUNT));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
                String display = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME));
                long addTime = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED));
                long lastModify = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));
                String format = cursor.getString(cursor.getColumnIndex("format"));
                int mediaType = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE));
//                Log.e("global", "id=" + id + ";title=" + title
//                        + ";display=" + display + ";parent=" + parent + ";path=" + data);
//                File file = new File(data);
//                if (file.isDirectory()) {
//                    Log.e("global", data);
//                }
                Log.e("global", "id=" + id + ";mediaTYpe=" + mediaType + ";parent=" + parent + ";path=" + data);
            }

            Log.e("time", c + ";;time=" + (System.currentTimeMillis() - start));
        }
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
            // 低版本使用广播
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

    //////////////////////////////////////////////////////////
    ////////////////扫描系统数据库接口开始//////////////////////
    /////////////////////////////////////////////////////////
    @Override
    public void onPreExecute() {
        Log.e("global", "开始系统数据库数据扫描");
    }

    @Override
    public void onProgressUpdate() {

    }

    @Override
    public void onPostExecute(List<BaseDataBean> data) {
        Log.e("global", "完成系统数据库数据扫描" + data.size());
//        for (BaseDataBean bean : data) {
//            if (bean.isFlag(BaseDataBean.FLAG_IMAGE)) {
//                Log.e("global", bean.toString());
//            }
//        }
    }

    @Override
    public void onCancelled() {
        Log.e("global", "取消系统数据库数据扫描");
    }
    ////////////////扫描系统数据库接口结束//////////////////////
}