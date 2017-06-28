package com.jb.filemanager.home;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.R;
import com.jb.filemanager.function.image.ImageActivity;
import com.jb.filemanager.function.music.MusicActivity;
import com.jb.filemanager.home.bean.CategoryBean;
import com.jb.filemanager.manager.file.FileManager;
import com.jb.filemanager.util.Logger;

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

    private GridView mCategoryView;

    private boolean mIsInternalStorage;

    private LoaderManager.LoaderCallbacks<Cursor> mPhotoLoaderCallback;
    private long mPhotoSize;

    private LoaderManager.LoaderCallbacks<Cursor> mVideoLoaderCallback;
    private long mAudioSize;

    LoaderManager.LoaderCallbacks<Cursor> mAudioLoaderCallback;
    private long mVideoSize;

    LoaderManager.LoaderCallbacks<List<Long>> mAppLoaderCallback;
    private long mAppSize;

    LoaderManager.LoaderCallbacks<Cursor> mDocLoaderCallback;
    private long mDocSize;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPhotoLoaderCallback = getImageLoaderCallback();
        mVideoLoaderCallback = getVideoLoaderCallback();
        mAppLoaderCallback = getAppLoaderCallback();
        mAudioLoaderCallback = getAudioLoaderCallback();
        mDocLoaderCallback = getDocLoaderCallback();

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(R.layout.fragment_main_category, container, false);

        // TODO test data
        CategoryBean bean1 = new CategoryBean(R.drawable.ic_main_category_image, "image", 199);
        CategoryBean bean2 = new CategoryBean(R.drawable.ic_main_category_image, "music", 99);
        CategoryBean bean3 = new CategoryBean(R.drawable.ic_main_category_image, "image", 199);
        CategoryBean bean4 = new CategoryBean(R.drawable.ic_main_category_image, "music", 99);
        CategoryBean bean5 = new CategoryBean(R.drawable.ic_main_category_image, "image", 199);
        CategoryBean bean6 = new CategoryBean(R.drawable.ic_main_category_image, "music", 99);
        CategoryBean bean7 = new CategoryBean(R.drawable.ic_main_category_image, "image", 199);
        CategoryBean bean8 = new CategoryBean(R.drawable.ic_main_category_image, "music", 99);
        CategoryBean bean9 = new CategoryBean(R.drawable.ic_main_category_image, "image", 199);
        CategoryBean bean10 = new CategoryBean(R.drawable.ic_main_category_image, "music", 99);
        CategoryBean bean11= new CategoryBean(R.drawable.ic_main_category_image, "image", 199);
        CategoryBean bean12 = new CategoryBean(R.drawable.ic_main_category_image, "music", 99);
        CategoryBean bean13 = new CategoryBean(R.drawable.ic_main_category_image, "image", 199);
        CategoryBean bean14 = new CategoryBean(R.drawable.ic_main_category_image, "music", 99);
        CategoryBean bean15 = new CategoryBean(R.drawable.ic_main_category_image, "image", 199);
        CategoryBean bean16 = new CategoryBean(R.drawable.ic_main_category_image, "music", 99);
        CategoryBean bean17 = new CategoryBean(R.drawable.ic_main_category_image, "image", 199);
        CategoryBean bean18 = new CategoryBean(R.drawable.ic_main_category_image, "music", 99);
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
        arrayList.add(bean10);
        arrayList.add(bean11);
        arrayList.add(bean12);
        arrayList.add(bean13);
        arrayList.add(bean14);
        arrayList.add(bean15);
        arrayList.add(bean16);
        arrayList.add(bean17);
        arrayList.add(bean18);


        CategoryAdapter adapter = new CategoryAdapter();
        adapter.setData(arrayList);

        mCategoryView = (GridView) rootView.findViewById(R.id.gv_main_category);
        mCategoryView.setAdapter(adapter);

        mCategoryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                switch (position) {
                    case 0:
                        // 图片管理
                        startActivity(new Intent(getContext(), ImageActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(getContext(), MusicActivity.class));
                    default:
                        break;
                }
            }
        });

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
                    mAppSize = size;
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
                        mAudioSize = size;
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

                holder.mTvNumber = (TextView) convertView.findViewById(R.id.tv_main_category_number);
                if (holder.mTvNumber != null) {
                    holder.mTvNumber.getPaint().setAntiAlias(true);
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

                if (holder.mTvNumber != null) {
                    String number = context.getString(R.string.main_category_item_number, bean.getCategoryNumber());
                    holder.mTvNumber.setText(number);
                }
            }
            return convertView;
        }

        private static class ViewHolder {
            ImageView mIvIcon;
            TextView mTvName;
            TextView mTvNumber;
        }
    }
}
