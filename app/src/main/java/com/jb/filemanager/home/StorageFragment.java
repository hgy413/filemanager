package com.jb.filemanager.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jb.filemanager.Const;
import com.jb.filemanager.R;
import com.jb.filemanager.home.event.SelectFileEvent;
import com.jb.filemanager.manager.PackageManagerLocker;
import com.jb.filemanager.manager.file.FileLoader;
import com.jb.filemanager.manager.file.FileManager;
import com.jb.filemanager.util.FileUtil;
import com.jb.filemanager.util.images.ImageCache;
import com.jb.filemanager.util.images.ImageFetcher;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Created by bill wang on 2017/6/22.
 *
 */

public class StorageFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<File>> {

    private ListView mLvFiles;

    private Stack<File> mPathStack = new Stack<>();
    private FileListAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPathStack = new Stack<>();
        mPathStack.push(new File(Const.FILE_MANAGER_DIR));
        String[] paths = FileUtil.getVolumePaths(getActivity());

        mAdapter = new FileListAdapter(getActivity(), paths);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(R.layout.fragment_main_storage, container, false);

        mLvFiles = (ListView) rootView.findViewById(R.id.lv_main_storage_list);
        mLvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileListAdapter adapter = (FileListAdapter) mLvFiles.getAdapter();
                if (adapter.getCount() <= position) {
                    return;
                }

                File file = adapter.getItem(position);
                if (file.isDirectory() && !FileManager.getInstance().isSelected(file)) {
//                    mPathStack.push(file);
                    // TODO
//                    restartLoad();
                } else {
                    FileListAdapter.ViewHolder holder = (FileListAdapter.ViewHolder) view
                            .getTag();
                    if (holder != null) {
                        boolean isSelected = holder.mIvChecked.isSelected();
                        holder.mIvChecked.setSelected(!isSelected);
                        if (!isSelected) {
                            FileManager.getInstance().add(file);
                        } else {
                            FileManager.getInstance().remove(file);
                        }
                    }
                }
            }
        });
        mLvFiles.setAdapter(mAdapter);

        getLoaderManager().initLoader(FileManager.LOADER_FILES, null, this);

        return rootView;
    }

    // implements LoaderManager.LoaderCallbacks<List<File>>
    @Override
    public Loader<List<File>> onCreateLoader(int id, Bundle args) {
        if (mPathStack != null && !mPathStack.isEmpty()) {
            return new FileLoader(getActivity(), mPathStack.lastElement().getAbsolutePath());
        }
        return new FileLoader(getActivity(), null);
    }

    @Override
    public void onLoadFinished(Loader<List<File>> loader, List<File> data) {
        mAdapter.setListItems(data);
    }

    @Override
    public void onLoaderReset(Loader<List<File>> loader) {
        mAdapter.clear();
    }

    // private start

    public void restartLoad() {
//        mProgressBar.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(FileManager.LOADER_FILES, null, this);
    }



    // private end

    private static class FileListAdapter extends BaseAdapter {

        private final LayoutInflater mInflater;
        private ImageFetcher mImageFetcher;
        private int mImageSize;

        private List<File> mData = new ArrayList<>();
        private String[] mRootDirs;

        public FileListAdapter(Context context, String[] rootDirs) {
            mInflater = LayoutInflater.from(context);
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            mImageSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, dm);
            mImageFetcher = createImageFetcher((FragmentActivity) context, mImageSize, R.drawable.img_picture);
            mRootDirs = rootDirs;
        }

        public void add(File file) {
            mData.add(file);
            notifyDataSetChanged();
        }

        public void clear() {
            mData.clear();
            notifyDataSetChanged();
        }

        @Override
        public File getItem(int position) {
            File result = null;
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
        public int getCount() {
            int result = 0;
            if (mData != null) {
                result = mData.size();
            }
            return result;
        }

        public List<File> getListItems() {
            return mData;
        }

        public void setListItems(List<File> data) {
            mData = data;
            notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_main_storage_list_style, null);
                holder = new ViewHolder();
                holder.mIvFileThumb = (ImageView) convertView.findViewById(R.id.iv_main_storage_style_list_item_icon);
                holder.mTvFileName = (TextView) convertView.findViewById(R.id.tv_main_storage_style_list_item_name);
                holder.mTvFileDesc = (TextView) convertView.findViewById(R.id.tv_main_storage_style_list_item_desc);
                holder.mIvChecked = (ImageView) convertView.findViewById(R.id.iv_main_storage_style_list_item_selected);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            File file = getItem(position);
            if (isRootDir(file.getAbsolutePath())) {
                holder.mTvFileDesc.setVisibility(View.INVISIBLE);
                holder.mIvChecked.setVisibility(View.INVISIBLE);

                holder.mTvFileName.setText(FileUtil.isInternalStoragePath(mInflater.getContext(),
                        file.getAbsolutePath()) ? mInflater.getContext().getString(
                        R.string.main_internal_storage) : file.getName().toUpperCase());
            } else {
                holder.mIvChecked.setVisibility(View.VISIBLE);
                holder.mTvFileDesc.setVisibility(View.VISIBLE);
                holder.mTvFileName.setText(file.getName());
            }

            // 描述
            if (holder.mTvFileDesc != null) {
                if (file.isDirectory()) {
                    holder.mTvFileDesc.setText("我是文件夹");
                } else {
                    holder.mTvFileDesc.setText("我是文件");
                }
            }

            // icon
            if (file.isDirectory()) {
                if (isRootDir(file.getAbsolutePath())) {
                    boolean isInternalStorage = FileUtil.isInternalStoragePath(mInflater.getContext(), file.getAbsolutePath());
                    holder.mIvFileThumb.setImageResource(isInternalStorage ? R.drawable.img_phone : R.drawable.img_sdcard);
                } else {
                    holder.mIvFileThumb.setImageResource(R.drawable.img_folder);
                }
            } else {
                loadFileThumb(file.getAbsolutePath(), holder.mIvFileThumb);
            }

            // 选中标识
            if (FileManager.getInstance().isSelected(file)) {
                holder.mIvChecked.setSelected(true);
            } else {
                holder.mIvChecked.setSelected(false);
            }
            holder.mIvChecked.setTag(position);
            holder.mIvChecked.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    boolean isSelected = holder.mIvChecked.isSelected();
                    File file = getItem(position);
                    if (!isSelected) {
                        FileManager.getInstance().add(file);
                    } else {
                        FileManager.getInstance().remove(file);
                    }
                    holder.mIvChecked.setSelected(!isSelected);
                }
            });

            return convertView;
        }

        private void loadFileThumb(String path, ImageView ivThumb) {
            int type = FileUtil.getFileType(path);
            switch (type) {
                case FileManager.PICTURE:
                case FileManager.VIDEO:
                    if (mImageFetcher != null) {
                        mImageFetcher.loadImage(path, ivThumb);
                    }
                    break;

                case FileManager.MUSIC:
                    ivThumb.setImageResource(R.drawable.img_music);
                    break;

                case FileManager.OTHERS:
                    ivThumb.setImageResource(R.drawable.img_file);
                    break;

                case FileManager.APP:
                    ivThumb.setImageDrawable(PackageManagerLocker.getInstance().getApplicationIconByPath(path, 120, 120));
                    break;

                default:
                    break;
            }
        }

        private static class ViewHolder {
            ImageView mIvFileThumb;
            TextView mTvFileName;
            TextView mTvFileDesc;
            ImageView mIvChecked;
        }

        private boolean isRootDir(String path) {
            if (mRootDirs != null && mRootDirs.length > 0) {
                for (String filePath : mRootDirs) {
                    if (path.equals(filePath)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private ImageFetcher createImageFetcher(FragmentActivity activity, int imageSize, int defaultImageId) {
            ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(activity,
                    Const.IMAGE_CACHE_DIR);

            cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of
            // app memory

            // The ImageFetcher takes care of loading images into our ImageView
            // children asynchronously
            ImageFetcher imageFetcher = new ImageFetcher(activity, imageSize);
            imageFetcher.setLoadingImage(defaultImageId);
            imageFetcher.addImageCache(activity.getSupportFragmentManager(), cacheParams);
            return imageFetcher;
        }
    }
}