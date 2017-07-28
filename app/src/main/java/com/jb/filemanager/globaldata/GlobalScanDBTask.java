package com.jb.filemanager.globaldata;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Files.FileColumns;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.recent.util.RecentFileUtil;
import com.jb.filemanager.function.zipfile.util.FileUtils;
import com.jb.filemanager.globaldata.bean.BaseDataBean;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoyu on 2017/7/27 16:52.
 * <p>
 * 读取系统Media数据库数据.
 * </p>
 */

public class GlobalScanDBTask extends AsyncTask<Void, Void, List<BaseDataBean>> {

    private WeakReference<GlobalScanDBListener> mReference;
    private Context mContext;

    public GlobalScanDBTask(GlobalScanDBListener listener) {
        mReference = new WeakReference<GlobalScanDBListener>(listener);
        mContext = TheApplication.getAppContext();
    }

    @Override
    protected void onPreExecute() {
        if (mReference.get() != null) {
            mReference.get().onPreExecute();
        }
    }

    /**
     * _id--_data--_size--format--parent--date_added--date_modified--
     * mime_type--title--description--_display_name--picasa_id--
     * orientation--latitude--longitude--datetaken--mini_thumb_magic--
     * bucket_id--bucket_display_name--isprivate--title_key--artist_id--
     * album_id--composer--track--year--is_ringtone--is_music--is_alarm--
     * is_notification--is_podcast--album_artist--duration--bookmark--
     * artist--album--resolution--tags--category--language--mini_thumb_data--
     * name--media_type--old_id--storage_id--is_drm--width--height--is_sound--
     * year_name--genre_name--recently_played--most_played--recently_added_remove_flag--
     * is_favorite--resumePos--isPlayed--face_count--scan_pri--weather_ID--
     * recordingtype--group_id--city_ID--spherical_mosaic--label_id--is_memo--
     * addr--langagecode--is_secretbox--sampling_rate--bit_depth--is_360_video--
     * pic_rating--sef_file_type--reusable--recorded_number--title_bucket--
     * title_label--recording_mode--type3dvideo--video_view_mode--video_codec_info--
     * audio_codec_info--title_search_key--title_pinyin--composer_pinyin--genre_name_pinyin--
     * _display_name_pinyin--bucket_display_name_pinyin--name_pinyin--
     */
    @Override
    protected List<BaseDataBean> doInBackground(Void... params) {
        long time = System.currentTimeMillis();
        List<BaseDataBean> result = new ArrayList<>();
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = MediaStore.Files.getContentUri("external");
        // 获取Download目录index
        long downloadDirId = -1;
        String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        Cursor downloadCursor = null;
        try {
            downloadCursor = resolver.query(
                    uri,
                    null,
                    FileColumns.DATA + "=?",
                    new String[]{downloadPath},
                    null);
            if (downloadCursor != null && downloadCursor.moveToNext()) {
                downloadDirId = downloadCursor.getLong(downloadCursor.getColumnIndex(FileColumns._ID));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (downloadCursor != null) {
                downloadCursor.close();
            }
        }
        // 获取数据
//        String[] projection = null;
//        String selection = null;
//        String[] selectionArgs = null;
//        String sortOrder = null;
        Cursor cursor = null;
        File file;
        try {
//            cursor = resolver.query(uri, projection, selection, selectionArgs, sortOrder);
            cursor = resolver.query(uri, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    if (isCancelled()) return result;
                    String path = cursor.getString(cursor.getColumnIndex(FileColumns.DATA));
                    file = new File(path);
                    if (isNeedHandle(file)) {
                        long id = cursor.getLong(cursor.getColumnIndex(FileColumns._ID));
                        int parentIndex = cursor.getInt(cursor.getColumnIndex(FileColumns.PARENT));
                        BaseDataBean bean = new BaseDataBean(file, id, parentIndex);
                        // 计算flag
                        if (parentIndex == downloadDirId && downloadDirId != -1) {
                            bean.flag = bean.flag | BaseDataBean.FLAG_DOWNLOAD;
                        }
                        if (RecentFileUtil.isRecentFileLite(file)) {
                            bean.flag = bean.flag | BaseDataBean.FLAG_RECENT;
                        }
                        String extension = FileUtils.getFileExtensionLite(file);
                        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                        if (TextUtils.isEmpty(extension) || TextUtils.isEmpty(mimeType)) {
                            bean.flag = bean.flag | BaseDataBean.FLAG_OTHER;
                        } else if (mimeType.matches("image/.+")) {
                            bean.flag = bean.flag | BaseDataBean.FLAG_IMAGE;
                        } else if (mimeType.matches("audio/.+")) {
                            bean.flag = bean.flag | BaseDataBean.FLAG_AUDIO;
                        } else if (mimeType.matches("video/.+")) {
                            bean.flag = bean.flag | BaseDataBean.FLAG_VIDEO;
                        } else if (mimeType.equals("application/vnd.android.package-archive")) {
                            bean.flag = bean.flag | BaseDataBean.FLAG_APK;
                        } else if (mimeType.equals("text/plain")) {
                            bean.flag = bean.flag | BaseDataBean.FLAG_TXT;
                            bean.flag = bean.flag | BaseDataBean.FLAG_DOCUMENT;
                        } else if (mimeType.equals("application/pdf")) {
                            bean.flag = bean.flag | BaseDataBean.FLAG_DOCUMENT;
                            bean.flag = bean.flag | BaseDataBean.FLAG_PDF;
                        } else {
                            // 剩下的mime type 格式不统一，简单使用后缀名判断
                            switch (extension) {
                                case "doc":
                                case "docx":
                                case "ppt":
                                case "pptx":
                                case "xls":
                                case "xlsx":
                                    bean.flag = bean.flag | BaseDataBean.FLAG_DOCUMENT;
                                    bean.flag = bean.flag | BaseDataBean.FLAG_DOC;
                                    break;
                                case "rar":
                                case "zip":
                                case "7z":
                                    bean.flag = bean.flag | BaseDataBean.FLAG_ZIP;
                                    break;
                                default:
                                    bean.flag = bean.flag | BaseDataBean.FLAG_OTHER;
                                    break;
                            }
                        }
                        result.add(bean);
                    }
                }
                long endTime = System.currentTimeMillis();
                Log.e("global", "耗时=" + (endTime - time));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return result;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        if (mReference.get() != null) {
            mReference.get().onProgressUpdate();
        }
    }

    @Override
    protected void onPostExecute(List<BaseDataBean> baseDataBeen) {
        if (mReference.get() != null) {
            mReference.get().onPostExecute(baseDataBeen);
        }
    }

    @Override
    protected void onCancelled() {
        if (mReference.get() != null) {
            mReference.get().onCancelled();
        }
    }

    /**
     * 判断该文件是否需要后续处理, 若不需要直接略过<br>
     * 有扩展名进行处理, 或属于最近文件进行处理<br>
     * 文件夹不处理, 不存在不处理
     *
     * @param file file
     * @return true or false
     */
    private boolean isNeedHandle(File file) {
        if (file == null || !file.exists() || file.isDirectory() || file.isHidden())
            return false;
        String extension = FileUtils.getFileExtensionLite(file);
        return !TextUtils.isEmpty(extension) || RecentFileUtil.isRecentFileLite(file);
    }

    public interface GlobalScanDBListener {
        void onPreExecute();

        void onProgressUpdate();

        void onPostExecute(List<BaseDataBean> data);

        void onCancelled();
    }
}
