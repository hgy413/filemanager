package com.jb.filemanager.home;

import android.animation.ValueAnimator;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jb.filemanager.Const;
import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.apkmanager.AppManagerActivity;
import com.jb.filemanager.function.docmanager.DocManagerActivity;
import com.jb.filemanager.function.image.ImageActivity;
import com.jb.filemanager.function.rate.RateManager;
import com.jb.filemanager.function.recent.RecentFileActivity;
import com.jb.filemanager.function.recent.RecentFileManager;
import com.jb.filemanager.function.recent.bean.BlockBean;
import com.jb.filemanager.function.samefile.SameFileActivity;
import com.jb.filemanager.function.scanframe.clean.event.CleanScanDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanScanFileSizeEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanStateEvent;
import com.jb.filemanager.function.trash.CleanTrashActivity;
import com.jb.filemanager.function.zipfile.ZipFileActivity;
import com.jb.filemanager.manager.file.FileManager;
import com.jb.filemanager.ui.view.UsageAnalysis;
import com.jb.filemanager.util.APIUtil;
import com.jb.filemanager.util.ConvertUtils;
import com.jb.filemanager.util.FileUtil;
import com.jb.filemanager.util.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
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

    private static final String[] ZIP_PROJECTION = new String[] {
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

    private LoaderManager.LoaderCallbacks<Cursor> mZipLoaderCallback;
    private long mZipSize;

    private long mDownloadSize;

    private long mRecentSize;

    private long mTotalSize;
    private long mUsedSize;

    private TextView mTvPhotoCount;
    private TextView mTvVideoCount;
    private TextView mTvAppCount;
    private TextView mTvMusicCount;
    private TextView mTvDocCount;
    private TextView mTvZipCount;
    private TextView mTvDownloadCount;
    private TextView mTvRecentCount;
    private TextView mTvAdCount;

    private TextView mTvStorageTitle;
    private TextView mTvStorageUsed;
    private TextView mTvStorageUnused;
    private UsageAnalysis mUaStorage;

    private LinearLayout mLlPhoneAndSdcardSwitcher;
    private TextView mTvSwitchPhone;
    private TextView mTvSwitchSdCard;

    private boolean mIsInternalStorage = true;
    private boolean mHasExternalStorage = false;
    private TextView mTvCleanTrash;
    private boolean mHasShowedNotice = false;
    private ValueAnimator mValueAnimator;

    private Runnable mCheckUsageRunnable = new Runnable() {
        @Override
        public void run() {
            updateUsageAnalysis();
            startUsageCheckTimer();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPhotoLoaderCallback = getImageLoaderCallback();
        mVideoLoaderCallback = getVideoLoaderCallback();
        mAppLoaderCallback = getAppLoaderCallback();
        mAudioLoaderCallback = getAudioLoaderCallback();
        mDocLoaderCallback = getDocLoaderCallback();
        mZipLoaderCallback = getZipLoaderCallback();

        String[] paths = FileUtil.getVolumePaths(getActivity());
        if (paths.length > 1) {
            mHasExternalStorage = true;
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(R.layout.fragment_main_category, container, false);

        FrameLayout flPhoto = (FrameLayout) rootView.findViewById(R.id.fl_main_category_photo);
        if (flPhoto != null) {
            TextView tvPhoto = (TextView) flPhoto.findViewById(R.id.tv_main_category_photo);
            if (tvPhoto != null) {
                tvPhoto.getPaint().setAntiAlias(true);
            }
            flPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add by nieyh 收集评分引导的触发因素信息
                    RateManager.getsInstance().collectTriggeringFactor(RateManager.STORAGE_SUB_PAGE);
                    // 图片管理
                    startActivity(new Intent(getContext(), ImageActivity.class));
                }
            });

            mTvPhotoCount = (TextView) flPhoto.findViewById(R.id.tv_main_category_photo_count);
            if (mTvPhotoCount != null) {
                mTvPhotoCount.getPaint().setAntiAlias(true);
            }
        }

        FrameLayout flVideo = (FrameLayout) rootView.findViewById(R.id.fl_main_category_video);
        if (flVideo != null) {
            TextView tvVideo = (TextView) flVideo.findViewById(R.id.tv_main_category_video);
            if (tvVideo != null) {
                tvVideo.getPaint().setAntiAlias(true);
            }
            flVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add by nieyh 收集评分引导的触发因素信息
                    RateManager.getsInstance().collectTriggeringFactor(RateManager.STORAGE_SUB_PAGE);
                    // 视频管理
                    Intent intent = new Intent(getContext(), SameFileActivity.class);
                    intent.putExtra(SameFileActivity.PARAM_CATEGORY_TYPE, Const.CategoryType.CATEGORY_TYPE_VIDEO);
                    startActivity(intent);
                }
            });

            mTvVideoCount = (TextView) flVideo.findViewById(R.id.tv_main_category_video_count);
            if (mTvVideoCount != null) {
                mTvVideoCount.getPaint().setAntiAlias(true);
            }
        }

        FrameLayout flApp = (FrameLayout) rootView.findViewById(R.id.fl_main_category_app);
        if (flApp != null) {
            TextView tvApp = (TextView) flApp.findViewById(R.id.tv_main_category_app);
            if (tvApp != null) {
                tvApp.getPaint().setAntiAlias(true);
            }
            flApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add by nieyh 收集评分引导的触发因素信息
                    RateManager.getsInstance().collectTriggeringFactor(RateManager.STORAGE_SUB_PAGE);
                    // apk管理
                    startActivity(new Intent(getContext(), AppManagerActivity.class));
                }
            });

            mTvAppCount = (TextView) flApp.findViewById(R.id.tv_main_category_app_count);
            if (mTvAppCount != null) {
                mTvAppCount.getPaint().setAntiAlias(true);
            }
        }

        FrameLayout flMusic = (FrameLayout) rootView.findViewById(R.id.fl_main_category_music);
        if (flMusic != null) {
            TextView tvMusic = (TextView) flMusic.findViewById(R.id.tv_main_category_music);
            if (tvMusic != null) {
                tvMusic.getPaint().setAntiAlias(true);
            }
            flMusic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add by nieyh 收集评分引导的触发因素信息
                    RateManager.getsInstance().collectTriggeringFactor(RateManager.STORAGE_SUB_PAGE);
                    // 音乐管理
                    Intent intent = new Intent(getContext(), SameFileActivity.class);
                    intent.putExtra(SameFileActivity.PARAM_CATEGORY_TYPE, Const.CategoryType.CATEGORY_TYPE_MUSIC);
                    startActivity(intent);
                }
            });

            mTvMusicCount = (TextView) flMusic.findViewById(R.id.tv_main_category_music_count);
            if (mTvMusicCount != null) {
                mTvMusicCount.getPaint().setAntiAlias(true);
            }
        }

        FrameLayout flDoc = (FrameLayout) rootView.findViewById(R.id.fl_main_category_doc);
        if (flDoc != null) {
            TextView tvDoc = (TextView) flDoc.findViewById(R.id.tv_main_category_doc);
            if (tvDoc != null) {
                tvDoc.getPaint().setAntiAlias(true);
            }
            flDoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add by nieyh 收集评分引导的触发因素信息
                    RateManager.getsInstance().collectTriggeringFactor(RateManager.STORAGE_SUB_PAGE);
                    // 文档管理
                    startActivity(new Intent(getContext(), DocManagerActivity.class));
                }
            });

            mTvDocCount = (TextView) flDoc.findViewById(R.id.tv_main_category_doc_count);
            if (mTvDocCount != null) {
                mTvDocCount.getPaint().setAntiAlias(true);
            }
        }

        FrameLayout flZip = (FrameLayout) rootView.findViewById(R.id.fl_main_category_zip);
        if (flZip != null) {
            TextView tvZip = (TextView) flZip.findViewById(R.id.tv_main_category_zip);
            if (tvZip != null) {
                tvZip.getPaint().setAntiAlias(true);
            }
            flZip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add by nieyh 收集评分引导的触发因素信息
                    RateManager.getsInstance().collectTriggeringFactor(RateManager.STORAGE_SUB_PAGE);
                    // zip
                    startActivity(new Intent(getContext(), ZipFileActivity.class));
                }
            });

            mTvZipCount = (TextView) flZip.findViewById(R.id.tv_main_category_zip_count);
            if (mTvZipCount != null) {
                mTvZipCount.getPaint().setAntiAlias(true);
            }
        }

        FrameLayout flDownload = (FrameLayout) rootView.findViewById(R.id.fl_main_category_download);
        if (flDownload != null) {
            TextView tvDownload = (TextView) flDownload.findViewById(R.id.tv_main_category_download);
            if (tvDownload != null) {
                tvDownload.getPaint().setAntiAlias(true);
            }
            flDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add by nieyh 收集评分引导的触发因素信息
                    RateManager.getsInstance().collectTriggeringFactor(RateManager.STORAGE_SUB_PAGE);
                    // 下载管理
                    Intent intent = new Intent(getContext(), SameFileActivity.class);
                    intent.putExtra(SameFileActivity.PARAM_CATEGORY_TYPE, Const.CategoryType.CATEGORY_TYPE_DOWNLOAD);
                    startActivity(intent);
                }
            });

            mTvDownloadCount = (TextView) flDownload.findViewById(R.id.tv_main_category_download_count);
            if (mTvDownloadCount != null) {
                mTvDownloadCount.getPaint().setAntiAlias(true);
            }
        }

        FrameLayout flRecent = (FrameLayout) rootView.findViewById(R.id.fl_main_category_recent);
        if (flRecent != null) {
            TextView tvRecent = (TextView) flRecent.findViewById(R.id.tv_main_category_recent);
            if (tvRecent != null) {
                tvRecent.getPaint().setAntiAlias(true);
            }
            flRecent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add by nieyh 收集评分引导的触发因素信息
                    RateManager.getsInstance().collectTriggeringFactor(RateManager.STORAGE_SUB_PAGE);
                    // 最近文件
                    startActivity(new Intent(getContext(), RecentFileActivity.class));
                }
            });

            mTvRecentCount = (TextView) flRecent.findViewById(R.id.tv_main_category_recent_count);
            if (mTvRecentCount != null) {
                mTvRecentCount.getPaint().setAntiAlias(true);
            }
        }

        FrameLayout flAd = (FrameLayout) rootView.findViewById(R.id.fl_main_category_ad);
        if (flAd != null) {
            TextView tvAd = (TextView) flAd.findViewById(R.id.tv_main_category_ad);
            if (tvAd != null) {
                tvAd.getPaint().setAntiAlias(true);
            }
            flAd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO 点击广告
                }
            });

            mTvAdCount = (TextView) flAd.findViewById(R.id.tv_main_category_ad_count);
            if (mTvAdCount != null) {
                mTvAdCount.getPaint().setAntiAlias(true);
            }
        }

        mTvStorageTitle = (TextView) rootView.findViewById(R.id.tv_main_category_info_storage_title);
        if (mTvStorageTitle != null) {
            mTvStorageTitle.getPaint().setAntiAlias(true);
            if (mIsInternalStorage) {
                mTvStorageTitle.setText(R.string.main_info_phone_storage);
                mTvStorageTitle.setCompoundDrawablesWithIntrinsicBounds(APIUtil.getDrawable(getActivity(), R.drawable.img_phone_storage), null, null, null);
            } else {
                mTvStorageTitle.setText(R.string.main_info_sdcard_storage);
                mTvStorageTitle.setCompoundDrawablesWithIntrinsicBounds(APIUtil.getDrawable(getActivity(), R.drawable.img_sdcard_storage), null, null, null);
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
            mUaStorage.setTotal(mTotalSize);
            mUaStorage.setUsed(APIUtil.getColor(getContext(), R.color.main_category_info_other_color), mUsedSize);
        }

        mLlPhoneAndSdcardSwitcher = (LinearLayout) rootView.findViewById(R.id.ll_main_category_phone_sdcard_switch);
        if (mLlPhoneAndSdcardSwitcher != null) {
            mLlPhoneAndSdcardSwitcher.setVisibility(mHasExternalStorage ? View.VISIBLE : View.GONE);
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
        getLoaderManager().initLoader(FileManager.LOADER_ZIP, null, mZipLoaderCallback);
    }

    @Override
    public void onResume() {
        super.onResume();

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
            String unusedString = getString(R.string.main_info_phone_unused, unusedReadableString);
            SpannableStringBuilder ssb = new SpannableStringBuilder(unusedString);
            ssb.setSpan(new ForegroundColorSpan(APIUtil.getColor(getContext(), R.color.main_category_info_storage_value_color)),
                    unusedString.length() - unusedReadableString.length(),
                    unusedString.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvStorageUnused.setText(ssb);
        }

        updateUsageAnalysis();

        if (getUserVisibleHint()) {
            startUsageCheckTimer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopUsageCheckTimer();
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
        mZipLoaderCallback = null;

        if (mValueAnimator != null) {
            mValueAnimator.cancel();
            mValueAnimator.removeAllUpdateListeners();
            mValueAnimator.removeAllListeners();
            mValueAnimator = null;
        }

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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            startUsageCheckTimer();
        } else {
            stopUsageCheckTimer();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(CleanScanDoneEvent event) {
        boolean allDone = CleanScanDoneEvent.isAllDone();
//        Logger.e("Main", isNeedShowAnim() + "接收到CleanScanDoneEvent事件: " + allDone + event.name());
        long junkFileAllSize = CleanScanFileSizeEvent.getJunkFileAllSize();
        if (allDone && junkFileAllSize > 100 * 1024 * 1024 && !mHasShowedNotice) {
            mHasShowedNotice = true;
//            String data = ConvertUtils.formatFileSize(CleanScanFileSizeEvent.getJunkFileAllSize());
            final String[] data = ConvertUtils.getFormatterStorage(junkFileAllSize);
            float size = Float.parseFloat(data[0]);
            mValueAnimator = ValueAnimator.ofFloat(0, size);
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Float value = (Float) animation.getAnimatedValue();
                    String format = String.format("%.1f", value);
                    mTvCleanTrash.setText(getString(R.string.home_trash_notice, format + data[1]));
                }
            });
            mValueAnimator.setDuration((long) (size * 80));
            mValueAnimator.start();
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
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

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
                        if (mTvPhotoCount != null) {
                            mTvPhotoCount.setText(String.valueOf(cursor.getCount()));
                        }
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
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

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
                        if (mTvVideoCount != null) {
                            mTvVideoCount.setText(String.valueOf(cursor.getCount()));
                        }
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
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

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
                        if (mTvMusicCount != null) {
                            mTvMusicCount.setText(String.valueOf(cursor.getCount()));
                        }
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
                    if (mTvAppCount != null) {
                        mTvAppCount.setText(String.valueOf(data.size()));
                    }
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
                Uri uri = MediaStore.Files.getContentUri("external");


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
                        if (mTvDocCount != null) {
                            mTvDocCount.setText(String.valueOf(cursor.getCount()));
                        }
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

    private LoaderManager.LoaderCallbacks<Cursor> getZipLoaderCallback() {
        return new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Uri uri = MediaStore.Files.getContentUri("external");


                String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                        + "or " + MediaStore.Files.FileColumns.MIME_TYPE + "=?";
                String mimeTypePdf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("zip");
                String mimeTypeTxt = MimeTypeMap.getSingleton().getMimeTypeFromExtension("rar");

                String[] selectionArgs = new String[]{ mimeTypePdf, mimeTypeTxt };

                return new CursorLoader(getActivity(),
                        uri,
                        ZIP_PROJECTION,
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
                        mZipSize = size;
                        if (mTvZipCount != null) {
                            mTvZipCount.setText(String.valueOf(cursor.getCount()));
                        }
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

    private void updateDownloadCount() {
        int count = 0;

        ArrayList<String> downloadPathList = FileUtil.getDownloadPathArray();
        for (String path : downloadPathList) {
            File file = new File(path);
            if (file.exists()) {
                int[] folderAndFile = FileUtil.countFolderAndFile(file);
                if (folderAndFile != null && folderAndFile.length == 2) {
                    count += folderAndFile[0] + folderAndFile[1];
                }
            }
        }

        if (mTvDownloadCount != null) {
            mTvDownloadCount.setText(String.valueOf(count));
        }
    }

    private void updateRecentCount() {
        int count = 0;
        List<BlockBean> blockBeanList = RecentFileManager.getInstance().getRecentFiles();
        for (BlockBean blockBean : blockBeanList) {
            count += blockBean.getChildCount();
        }

        if (mTvRecentCount != null) {
            mTvRecentCount.setText(String.valueOf(count));
        }
    }

    private void handleSwitchPhoneSdCard() {
        if (mTvSwitchPhone != null) {
            mTvSwitchPhone.setSelected(mIsInternalStorage);
        }
        if (mTvSwitchSdCard != null) {
            mTvSwitchSdCard.setSelected(!mIsInternalStorage);
        }

        updateUsageAnalysis();
    }

    private void startUsageCheckTimer() {
        TheApplication.postRunOnUiThread(mCheckUsageRunnable, 30 * 1000);
    }

    private void stopUsageCheckTimer() {
        TheApplication.removeFromUiThread(mCheckUsageRunnable);
    }

    private void updateUsageAnalysis() {
        Logger.e("wangzq", "updateUsageAnalysis");
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        mTotalSize = APIUtil.getTotalBytes(stat);
        mUsedSize = mTotalSize - APIUtil.getAvailableBytes(stat);
        if (mUaStorage != null) {
            mUaStorage.setTotal(mTotalSize);
            mUaStorage.setUsed(APIUtil.getColor(getContext(), R.color.main_category_info_other_color), mUsedSize);
        }

        getLoaderManager().restartLoader(FileManager.LOADER_IMAGE, null, mPhotoLoaderCallback);
        getLoaderManager().restartLoader(FileManager.LOADER_VIDEO, null, mVideoLoaderCallback);
        getLoaderManager().restartLoader(FileManager.LOADER_APP, null, mAppLoaderCallback);
        getLoaderManager().restartLoader(FileManager.LOADER_AUDIO, null, mAudioLoaderCallback);
        getLoaderManager().restartLoader(FileManager.LOADER_DOC, null, mDocLoaderCallback);
        getLoaderManager().restartLoader(FileManager.LOADER_ZIP, null, mZipLoaderCallback);
        updateDownloadCount();
        updateRecentCount();
    }
}
