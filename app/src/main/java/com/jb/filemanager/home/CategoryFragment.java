package com.jb.filemanager.home;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.Const;
import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.apkmanager.AppManagerActivity;
import com.jb.filemanager.function.docmanager.DocManagerActivity;
import com.jb.filemanager.function.image.ImageActivity;
import com.jb.filemanager.function.recent.RecentFileActivity;
import com.jb.filemanager.function.samefile.SameFileActivity;
import com.jb.filemanager.function.scanframe.clean.event.CleanScanDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanScanFileSizeEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanStateEvent;
import com.jb.filemanager.function.trash.CleanTrashActivity;
import com.jb.filemanager.function.zipfile.ZipFileActivity;
import com.jb.filemanager.home.bean.CategoryBean;
import com.jb.filemanager.manager.file.FileManager;
import com.jb.filemanager.ui.view.UsageAnalysis;
import com.jb.filemanager.util.APIUtil;
import com.jb.filemanager.util.ConvertUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bill wang on 2017/6/22.
 *
 */

public class CategoryFragment extends Fragment {

    private static final String[] PHOTO_PROJECTION = new String[] {
            MediaStore.Images.Media.SIZE };

    private static final String[] VIDEO_PROJECTION = new String[] {
            MediaStore.Video.Media.SIZE };

    private static final String[] AUDIO_PROJECTION = new String[] {
            MediaStore.Audio.Media.SIZE};

    private static final String[] DOC_PROJECTION = new String[] {
            MediaStore.Files.FileColumns.SIZE};

    private LoaderManager.LoaderCallbacks<Cursor> mPhotoLoaderCallback;
    private long mPhotoSize;

    private LoaderManager.LoaderCallbacks<Cursor> mVideoLoaderCallback;
    private long mVideoSize;

    private LoaderManager.LoaderCallbacks<Cursor> mAudioLoaderCallback;
    private long mAudioSize;

    private LoaderManager.LoaderCallbacks<List<Long>> mAppLoaderCallback;
    private long mAppsSize;

    private LoaderManager.LoaderCallbacks<Cursor> mDocLoaderCallback;
    private long mDocsSize;

    private long mTotalSize;
    private long mUsedSize;

    private GridView mCategoryView;
    private TextView mTvStorageTitle;
    private TextView mTvStorageUsed;
    private TextView mTvStorageUnused;
    private UsageAnalysis mUaStorage;

    private TextView mTvSwitchPhone;
    private TextView mTvSwitchSdCard;

    private boolean mIsInternalStorage = true;
    private boolean mHasExternalStorage = false;
    private TextView mTvCleanTrash;
    private boolean mHasShowedNotice = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPhotoLoaderCallback = getImageLoaderCallback();
        mVideoLoaderCallback = getVideoLoaderCallback();
        mAppLoaderCallback = getAppLoaderCallback();
        mAudioLoaderCallback = getAudioLoaderCallback();
        mDocLoaderCallback = getDocLoaderCallback();

        mHasExternalStorage = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(R.layout.fragment_main_category, container, false);

        CategoryBean bean1 = new CategoryBean(R.drawable.ic_main_category_photo, getString(R.string.main_category_item_photo));
        CategoryBean bean2 = new CategoryBean(R.drawable.ic_main_category_video, getString(R.string.main_category_item_video));//temp for video
        CategoryBean bean3 = new CategoryBean(R.drawable.ic_main_category_app, getString(R.string.main_category_item_apps));
        CategoryBean bean4 = new CategoryBean(R.drawable.ic_main_category_music, getString(R.string.main_category_item_music));// temp for music
        CategoryBean bean5 = new CategoryBean(R.drawable.ic_main_category_doc, getString(R.string.main_category_item_doc));//temp for doc
        CategoryBean bean6 = new CategoryBean(R.drawable.ic_main_category_zip, getString(R.string.main_category_item_zip));//temp for apk
        CategoryBean bean7 = new CategoryBean(R.drawable.ic_main_category_download, getString(R.string.main_category_item_download));// temp for download
        CategoryBean bean8 = new CategoryBean(R.drawable.ic_main_category_recent, getString(R.string.main_category_item_recent));
        CategoryBean bean9 = new CategoryBean(R.drawable.ic_main_category_ad, getString(R.string.main_category_item_ad));

        ArrayList<CategoryBean> arrayList = new ArrayList<>();
        arrayList.add(bean1);
        arrayList.add(bean2);
        arrayList.add(bean3);
        arrayList.add(bean4);
        arrayList.add(bean5);
        arrayList.add(bean6);
        arrayList.add(bean7);
        arrayList.add(bean8);
        arrayList.add(bean9);

        CategoryAdapter adapter = new CategoryAdapter();
        adapter.setData(arrayList);

        mCategoryView = (GridView) rootView.findViewById(R.id.gv_main_category);
        if (mCategoryView != null) {
            mCategoryView.setAdapter(adapter);
            mCategoryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    switch (position) {
                        case 0:
                            // 图片管理
                            startActivity(new Intent(getContext(), ImageActivity.class));
                            break;
                        case 1:
                            // 视频管理
                            Intent intent = new Intent(getContext(), SameFileActivity.class);
                            intent.putExtra(Const.CLASSIFY_TYPE, Const.FILE_TYPE_VIDEO);
                            startActivity(intent);
                            break;
                        case 2:
                            // apk管理
                            startActivity(new Intent(getContext(), AppManagerActivity.class));
                            break;
                        case 4:
                            // 文档管理
                            startActivity(new Intent(getContext(), DocManagerActivity.class));
                            break;
                        case 3:
                            // 音乐管理
                            intent = new Intent(getContext(), SameFileActivity.class);
                            intent.putExtra(Const.CLASSIFY_TYPE, Const.FILE_TYPE_MUSIC);
                            startActivity(intent);
                            break;
                        case 5:
                            // zip
                            startActivity(new Intent(getContext(), ZipFileActivity.class));
                            break;
                        case 6:
                            // 下载管理
                            intent = new Intent(getContext(), SameFileActivity.class);
                            intent.putExtra(Const.CLASSIFY_TYPE, Const.FILE_TYPE_DOWNLOAD);
                            startActivity(intent);
                            break;
                        case 7:
                            // 最近文件
                            startActivity(new Intent(getContext(), RecentFileActivity.class));
                            break;
                        default:
                            break;
                    }
                }
            });
        }

        mTvStorageTitle = (TextView) rootView.findViewById(R.id.tv_main_category_info_storage_title);
        if (mTvStorageTitle != null) {
            mTvStorageTitle.getPaint().setAntiAlias(true);
            if (mIsInternalStorage) {
                mTvStorageTitle.setText(R.string.main_info_phone_storage);
            } else {
                mTvStorageTitle.setText(R.string.main_info_sdcard_storage);
            }
        }
        

        mTvStorageUsed = (TextView) rootView.findViewById(R.id.tv_main_category_info_storage_used);
        if (mTvStorageUsed != null) {
            mTvStorageUsed.getPaint().setAntiAlias(true);
        }

        mTvStorageUnused = (TextView) rootView.findViewById(R.id.tv_main_category_info_storage_unused);
        if (mTvStorageUnused != null) {
            mTvStorageUnused.getPaint().setAntiAlias(true);
        }

        mUaStorage = (UsageAnalysis) rootView.findViewById(R.id.ua_main_category_info_usage_analysis);
        if (mUaStorage != null) {
            mUaStorage.reload();
            mUaStorage.setTotal(mTotalSize);
            mUaStorage.setUsed(APIUtil.getColor(getContext(), R.color.main_category_info_other_color), mUsedSize);
        }

        mTvSwitchPhone = (TextView) rootView.findViewById(R.id.tv_main_category_info_switch_phone);
        if (mTvSwitchPhone != null) {
            mTvSwitchPhone.getPaint().setAntiAlias(true);
            mTvSwitchPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIsInternalStorage = !mIsInternalStorage;
                    handleSwitchPhoneSdCard();
                }
            });

            mTvSwitchPhone.setSelected(mIsInternalStorage);
        }

        mTvSwitchSdCard = (TextView) rootView.findViewById(R.id.tv_main_category_info_switch_sdcard);
        if (mTvSwitchSdCard != null) {
            mTvSwitchSdCard.getPaint().setAntiAlias(true);
            mTvSwitchSdCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIsInternalStorage = !mIsInternalStorage;
                    handleSwitchPhoneSdCard();
                }
            });

            mTvSwitchSdCard.setSelected(!mIsInternalStorage);
        }

        mTvCleanTrash = (TextView) rootView.findViewById(R.id.tv_main_category_clear_trash);
        mTvCleanTrash.getPaint().setAntiAlias(false);
        mTvCleanTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHasShowedNotice = true;
                startActivity(new Intent(getContext(), CleanTrashActivity.class));
            }
        });


        try {
            TheApplication.getGlobalEventBus().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(FileManager.LOADER_IMAGE, null, mPhotoLoaderCallback);
        getLoaderManager().initLoader(FileManager.LOADER_VIDEO, null, mVideoLoaderCallback);
        getLoaderManager().initLoader(FileManager.LOADER_APP, null, mAppLoaderCallback);
        getLoaderManager().initLoader(FileManager.LOADER_AUDIO, null, mAudioLoaderCallback);
        getLoaderManager().initLoader(FileManager.LOADER_DOC, null, mDocLoaderCallback);
    }

    @Override
    public void onResume() {
        super.onResume();

        StatFs stat = new StatFs(mIsInternalStorage ? Environment.getDataDirectory().getPath() : Environment.getExternalStorageDirectory().getPath());
        mTotalSize = APIUtil.getTotalBytes(stat);
        mUsedSize = mTotalSize - APIUtil.getAvailableBytes(stat);
        if (mUaStorage != null) {
            mUaStorage.reload();
            mUaStorage.setTotal(mTotalSize);
            mUaStorage.setUsed(APIUtil.getColor(getContext(), R.color.main_category_info_other_color), mUsedSize);
        }
        if (mTvStorageUsed != null) {
            String usedReadableString = ConvertUtils.getReadableSize(mUsedSize);
            String usedString = getString(R.string.main_info_phone_used, usedReadableString);
            SpannableStringBuilder ssb = new SpannableStringBuilder(usedString);
            ssb.setSpan(new ForegroundColorSpan(APIUtil.getColor(getContext(), R.color.main_category_info_storage_value_color)),
                    usedString.length() - usedReadableString.length(),
                    usedString.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvStorageUsed.setText(ssb);
        }

        if (mTvStorageUnused != null) {
            String unusedReadableString = ConvertUtils.getReadableSize(mTotalSize - mUsedSize);
            String unusedString = getString(R.string.main_info_phone_used, unusedReadableString);
            SpannableStringBuilder ssb = new SpannableStringBuilder(unusedString);
            ssb.setSpan(new ForegroundColorSpan(APIUtil.getColor(getContext(), R.color.main_category_info_storage_value_color)),
                    unusedString.length() - unusedReadableString.length(),
                    unusedString.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvStorageUnused.setText(ssb);
        }

        getLoaderManager().restartLoader(FileManager.LOADER_IMAGE, null, mPhotoLoaderCallback);
        getLoaderManager().restartLoader(FileManager.LOADER_VIDEO, null, mVideoLoaderCallback);
        getLoaderManager().restartLoader(FileManager.LOADER_APP, null, mAppLoaderCallback);
        getLoaderManager().restartLoader(FileManager.LOADER_AUDIO, null, mAudioLoaderCallback);
        getLoaderManager().restartLoader(FileManager.LOADER_DOC, null, mDocLoaderCallback);
    }

    @Override
    public void onDestroy() {
        getLoaderManager().destroyLoader(FileManager.LOADER_IMAGE);
        getLoaderManager().destroyLoader(FileManager.LOADER_VIDEO);
        getLoaderManager().destroyLoader(FileManager.LOADER_APP);
        getLoaderManager().destroyLoader(FileManager.LOADER_AUDIO);
        getLoaderManager().destroyLoader(FileManager.LOADER_DOC);

        mAppLoaderCallback = null;
        mAudioLoaderCallback = null;
        mDocLoaderCallback = null;
        mPhotoLoaderCallback = null;
        mVideoLoaderCallback = null;

        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            TheApplication.getGlobalEventBus().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 扫描到的文件的大小
     *
     * @param event e
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(CleanScanFileSizeEvent event) {
    }

    @Subscribe
    public void onEventMainThread(CleanScanDoneEvent event) {
        boolean allDone = CleanScanDoneEvent.isAllDone();
//        Logger.e("Main", isNeedShowAnim() + "接收到CleanScanDoneEvent事件: " + allDone + event.name());
        if (allDone && CleanScanFileSizeEvent.getJunkFileAllSize() > 100 * 1024 * 1024 && !mHasShowedNotice) {
            mHasShowedNotice = true;
            String data = ConvertUtils.formatFileSize(CleanScanFileSizeEvent.getJunkFileAllSize());
            if (mTvCleanTrash != null) {
                mTvCleanTrash.setText(getString(R.string.home_trash_notice, data));
            }
        }
    }

    @Subscribe
    public void onEventMainThread(CleanStateEvent event) {
        if (CleanStateEvent.DELETE_ING.equals(event) && mTvCleanTrash != null) {
            //垃圾清理开始  改变文字显示
            mTvCleanTrash.setText(R.string.home_button_clean);
        }
    }

    private LoaderManager.LoaderCallbacks<Cursor> getImageLoaderCallback() {
        return new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Uri uri = mIsInternalStorage ? MediaStore.Images.Media.INTERNAL_CONTENT_URI : MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                return new CursorLoader(getActivity(),
                        uri,
                        PHOTO_PROJECTION,
                        MediaStore.Images.Media.SIZE + "!= 0 AND " + MediaStore.Images.Media.DATA + " IS NOT NULL",
                        null,
                        MediaStore.Images.Media.DEFAULT_SORT_ORDER);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                if (cursor != null) {
                    try {
                        long size = 0L;
                        while (cursor.moveToNext()) {
                            size += cursor.getLong(0);
                        }
                        mPhotoSize = size;
                        mUaStorage.addItem(APIUtil.getColor(getContext(), R.color.main_category_info_photo_color), size);
                    } finally {
                        cursor.close();
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        };
    }

    private LoaderManager.LoaderCallbacks<Cursor> getVideoLoaderCallback() {
        return new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Uri uri = mIsInternalStorage ? MediaStore.Video.Media.INTERNAL_CONTENT_URI : MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

                return new CursorLoader(getActivity(),
                        uri,
                        VIDEO_PROJECTION,
                        MediaStore.Video.Media.SIZE + "!= 0 AND " + MediaStore.Video.Media.DATA + " IS NOT NULL",
                        null,
                        MediaStore.Video.Media.DEFAULT_SORT_ORDER);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                if (cursor != null) {
                    try {
                        long size = 0L;
                        while (cursor.moveToNext()) {
                            size += cursor.getLong(0);
                        }
                        mVideoSize = size;
                        mUaStorage.addItem(APIUtil.getColor(getContext(), R.color.main_category_info_video_color), size);
                    } finally {
                        cursor.close();
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        };
    }

    private LoaderManager.LoaderCallbacks<Cursor> getAudioLoaderCallback() {
        return new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Uri uri = mIsInternalStorage ? MediaStore.Audio.Media.INTERNAL_CONTENT_URI : MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

                return new CursorLoader(getActivity(),
                        uri,
                        AUDIO_PROJECTION,
                        MediaStore.Audio.Media.SIZE + "!= 0 AND " + MediaStore.Audio.Media.DATA + " IS NOT NULL",
                        null,
                        MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                if (cursor != null) {
                    try {
                        long size = 0L;
                        while (cursor.moveToNext()) {
                            size += cursor.getLong(0);
                        }
                        mAudioSize = size;
                        mUaStorage.addItem(APIUtil.getColor(getContext(), R.color.main_category_info_music_color), size);
                    } finally {
                        cursor.close();
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        };
    }

    private LoaderManager.LoaderCallbacks<List<Long>> getAppLoaderCallback() {
        return new LoaderManager.LoaderCallbacks<List<Long>>() {
            @Override
            public Loader<List<Long>> onCreateLoader(int id, Bundle args) {
                return new AppSizeLoader(getContext());
            }

            @Override
            public void onLoadFinished(Loader<List<Long>> loader, List<Long> data) {
                if (data != null && data.size() > 0) {
                    long size = 0L;
                    for (Long appSize : data) {
                        size += appSize;
                    }
                    mAppsSize = size;
                    mUaStorage.addItem(APIUtil.getColor(getContext(), R.color.main_category_info_apps_color), size);
                }
            }

            @Override
            public void onLoaderReset(Loader<List<Long>> loader) {

            }
        };
    }

    private LoaderManager.LoaderCallbacks<Cursor> getDocLoaderCallback() {
        return new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Uri uri = mIsInternalStorage ? MediaStore.Files.getContentUri("internal") : MediaStore.Files.getContentUri("external");


                String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                        + "or " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                        + "or " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                        + "or " + MediaStore.Files.FileColumns.MIME_TYPE + "=?";
                String mimeTypePdf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
                String mimeTypeTxt = MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt");
                String mimeTypeDoc = MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc");
                String mimeTypeDocx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("docx");

                String[] selectionArgs = new String[]{ mimeTypePdf, mimeTypeTxt, mimeTypeDoc, mimeTypeDocx };

                return new CursorLoader(getActivity(),
                        uri,
                        DOC_PROJECTION,
                        selectionMimeType,
                        selectionArgs,
                        null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                if (cursor != null) {
                    try {
                        long size = 0L;
                        while (cursor.moveToNext()) {
                            size += cursor.getLong(0);
                        }
                        mDocsSize = size;
                        mUaStorage.addItem(APIUtil.getColor(getContext(), R.color.main_category_info_docs_color), size);
                    } finally {
                        cursor.close();
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        };
    }

    private void handleSwitchPhoneSdCard() {
        if (mTvSwitchPhone != null) {
            mTvSwitchPhone.setSelected(mIsInternalStorage);
        }
        if (mTvSwitchSdCard != null) {
            mTvSwitchSdCard.setSelected(!mIsInternalStorage);
        }

        StatFs stat = new StatFs(mIsInternalStorage ? Environment.getDataDirectory().getPath() : Environment.getExternalStorageDirectory().getPath());
        mTotalSize = APIUtil.getTotalBytes(stat);
        mUsedSize = mTotalSize - APIUtil.getAvailableBytes(stat);
        if (mUaStorage != null) {
            mUaStorage.reload();
            mUaStorage.setTotal(mTotalSize);
            mUaStorage.setUsed(APIUtil.getColor(getContext(), R.color.main_category_info_other_color), mUsedSize);
        }

        getLoaderManager().restartLoader(FileManager.LOADER_IMAGE, null, mPhotoLoaderCallback);
        getLoaderManager().restartLoader(FileManager.LOADER_VIDEO, null, mVideoLoaderCallback);
        getLoaderManager().restartLoader(FileManager.LOADER_APP, null, mAppLoaderCallback);
        getLoaderManager().restartLoader(FileManager.LOADER_AUDIO, null, mAudioLoaderCallback);
        getLoaderManager().restartLoader(FileManager.LOADER_DOC, null, mDocLoaderCallback);
    }

    private static class CategoryAdapter extends BaseAdapter {

        private ArrayList<CategoryBean> mData;

        public void setData(ArrayList<CategoryBean> data) {
            mData = data;
        }

        @Override
        public int getCount() {
            int result = 0;
            if (mData != null && mData.size() > 0) {
                result = mData.size();
            }
            return result;
        }

        @Override
        public Object getItem(int position) {
            Object result = null;
            if (mData != null && mData.size() > position) {
                result = mData.get(position);
            }
            return result;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            Context context = parent.getContext();
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_main_category,
                        parent, false);
                holder = new ViewHolder();
                holder.mIvIcon = (ImageView) convertView.findViewById(R.id.iv_main_category_icon);

                holder.mTvName = (TextView) convertView.findViewById(R.id.tv_main_category_name);
                if (holder.mTvName != null) {
                    holder.mTvName.getPaint().setAntiAlias(true);
                }

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (mData != null && mData.size() > position) {
                CategoryBean bean = mData.get(position);

                if (holder.mIvIcon != null) {
                    holder.mIvIcon.setImageResource(bean.getCategoryIconResId());
                }

                if (holder.mTvName != null) {
                    holder.mTvName.setText(bean.getCategoryName());
                }
            }
            return convertView;
        }

        private static class ViewHolder {
            ImageView mIvIcon;
            TextView mTvName;
        }
    }
}
