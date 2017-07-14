package com.jb.filemanager.home.fragment.storage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.eventbus.IOnEventMainThreadSubscriber;
import com.jb.filemanager.home.event.SortByChangeEvent;
import com.jb.filemanager.manager.PackageManagerLocker;
import com.jb.filemanager.manager.file.FileLoader;
import com.jb.filemanager.manager.file.FileManager;
import com.jb.filemanager.ui.dialog.SpaceNotEnoughDialog;
import com.jb.filemanager.ui.widget.BottomOperateBar;
import com.jb.filemanager.ui.widget.HorizontalListView;
import com.jb.filemanager.util.ConvertUtils;
import com.jb.filemanager.util.FileUtil;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.TimeUtil;
import com.jb.filemanager.util.images.ImageFetcher;
import com.jb.filemanager.util.images.ImageUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by bill wang on 2017/6/22.
 *
 */

public class StorageFragment extends Fragment implements View.OnKeyListener,
        LoaderManager.LoaderCallbacks<List<File>>,
        StorageContract.View {

    private ImageView mIvStorageDisk;
    private HorizontalListView mHLvDirs;
    private ImageView mIvStyleSwitcher;
    private ListView mLvFiles;
    private GridView mGvFiles;

    private List<File> mStorageList;
    private Stack<File> mPathStack;
    private FileListAdapter mListAdapter;
    private FileGridAdapter mGridAdapter;

    private BottomOperateBar mLlBottomOperateFirstContainer;
    private LinearLayout mLlBottomOperateSecondContainer;

    private StorageContract.Presenter mPresenter;

    private IOnEventMainThreadSubscriber<SortByChangeEvent> mSortChangeEvent = new IOnEventMainThreadSubscriber<SortByChangeEvent>() {

        @Override
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(SortByChangeEvent event) {
            restartLoad();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPathStack = new Stack<>();
        mStorageList = new ArrayList<>();
        initStoragePath();

        mPresenter = new StoragePresenter(this);

        mListAdapter = new FileListAdapter(getActivity(), mStorageList, mPresenter);
        mGridAdapter = new FileGridAdapter(getActivity(), mStorageList, mPresenter);

        EventBus.getDefault().register(mSortChangeEvent);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(FileManager.LOADER_FILES, null, this);

        mLvFiles.setAdapter(mListAdapter);
        mGvFiles.setAdapter(mGridAdapter);

        if (mStorageList != null && mStorageList.size() > 1) {
            mListAdapter.setListItems(mStorageList);
            mGridAdapter.setListItems(mStorageList);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(R.layout.fragment_main_storage, container, false);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(this);

        mIvStyleSwitcher = (ImageView) rootView.findViewById(R.id.iv_main_storage_style_switcher);
        if (mIvStyleSwitcher != null) {
            mIvStyleSwitcher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isGridStyle = mIvStyleSwitcher.isSelected();
                    if (isGridStyle) {
                        mLvFiles.setVisibility(View.VISIBLE);
                        mGvFiles.setVisibility(View.GONE);
                    } else {
                        mGvFiles.setVisibility(View.VISIBLE);
                        mLvFiles.setVisibility(View.GONE);
                    }

                    mIvStyleSwitcher.setSelected(!isGridStyle);
                }
            });
        }

        mIvStorageDisk = (ImageView) rootView.findViewById(R.id.iv_main_storage_disk);
        if (mIvStorageDisk != null) {
            if (mPathStack != null && mPathStack.size() > 0) {
                File file = mPathStack.firstElement();
                boolean isInternal = FileUtil.isInternalStoragePath(
                        getActivity(), file.getAbsolutePath());
                mIvStorageDisk.setImageResource(isInternal ? R.drawable.img_phone_storage : R.drawable.img_sdcard_storage);
            }
        }

        mHLvDirs = (HorizontalListView) rootView.findViewById(R.id.lv_dirs);
        mHLvDirs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String word = (String) ((HorizontalListView) parent).getAdapter().getItem(position);
                backToClickedDirectory(word);
            }
        });

        mLvFiles = (ListView) rootView.findViewById(R.id.lv_main_storage_list);
        mLvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileListAdapter adapter = (FileListAdapter) mLvFiles.getAdapter();
                if (adapter.getCount() <= position) {
                    return;
                }

                File file = adapter.getItem(position);

                if (mPresenter != null) {
                    int status = mPresenter.getStatus();

                    boolean handleClick = false;
                    boolean enterFolder = false;

                    if (status == StoragePresenter.MAIN_STATUS_SELECT) {
                        handleClick = true;
                        enterFolder = false;
                    } else if (status == StoragePresenter.MAIN_STATUS_NORMAL) {
                        handleClick = true;
                        enterFolder = file.isDirectory();
                    } else if (status == StoragePresenter.MAIN_STATUS_PASTE){
                        if (file.isDirectory()) {
                            handleClick = true;
                            enterFolder = true;
                        }
                    }

                    if (handleClick) {
                        if (file.isDirectory() && enterFolder) {
                            mPathStack.push(file);
                            restartLoad();
                        } else {
                            FileListAdapter.ViewHolder holder = (FileListAdapter.ViewHolder) view
                                    .getTag();
                            if (holder != null) {
                                boolean isSelected = holder.mIvChecked.isSelected();
                                holder.mIvChecked.setSelected(!isSelected);

                                mPresenter.addOrRemoveSelected(file);
                            }
                        }
                    }
                }
            }
        });
        mLvFiles.setAdapter(mListAdapter);

        mGvFiles = (GridView) rootView.findViewById(R.id.gv_main_storage_grid);
        mGvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileListAdapter adapter = (FileListAdapter) mLvFiles.getAdapter();
                if (adapter.getCount() <= position) {
                    return;
                }

                if (mPresenter != null) {
                    int status = mPresenter.getStatus();

                    File file = adapter.getItem(position);

                    if (status == StoragePresenter.MAIN_STATUS_SELECT || !file.isDirectory()) {
                        // 选择模式或者是单个文件点击处理
                        FileGridAdapter.ViewHolder holder = (FileGridAdapter.ViewHolder) view
                                .getTag();
                        if (holder != null) {
                            boolean isSelected = holder.mIvChecked.isSelected();
                            holder.mIvChecked.setSelected(!isSelected);

                            mPresenter.addOrRemoveSelected(file);
                        }
                    } else {
                        mPathStack.push(file);
                        restartLoad();
                    }
                }
            }
        });
        mGvFiles.setAdapter(mGridAdapter);

        mLlBottomOperateFirstContainer = (BottomOperateBar) rootView.findViewById(R.id.bottom_operate_bar_container);
        mLlBottomOperateFirstContainer.setListener(new BottomOperateBar.Listener() {
            @Override
            public ArrayList<File> getCurrentSelectedFiles() {
                ArrayList<File> result = null;
                if (mPresenter != null) {
                    result = mPresenter.getSelectedFiles();
                }
                return result;
            }

            @Override
            public Activity getActivity() {
                return StorageFragment.this.getActivity();
            }

            @Override
            public void afterCopy() {
                if (mPresenter != null) {
                    mPresenter.afterCopy();
                }
            }

            @Override
            public void afterCut() {
                if (mPresenter != null) {
                    mPresenter.afterCut();
                }
            }

            @Override
            public void afterRename() {
                if (mPresenter != null) {
                    mPresenter.afterRename();
                }
            }

            @Override
            public void afterDelete() {
                if (mPresenter != null) {
                    mPresenter.afterDelete();
                }
            }
        });


        mLlBottomOperateSecondContainer = (LinearLayout) rootView.findViewById(R.id.ll_main_bottom_operate_second_container);
        if (mLlBottomOperateSecondContainer != null) {
            TextView cancel = (TextView) mLlBottomOperateSecondContainer.findViewById(R.id.tv_main_bottom_operate_second_container_cancel);
            if (cancel != null) {
                cancel.getPaint().setAntiAlias(true);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mPresenter != null) {
                            mPresenter.onClickOperateCancelButton();
                        }
                    }
                });
            }

            TextView ok = (TextView) mLlBottomOperateSecondContainer.findViewById(R.id.tv_main_bottom_operate_second_container_paste);
            if (ok != null) {
                ok.getPaint().setAntiAlias(true);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mPresenter != null) {
                            mPresenter.onClickOperatePasteButton();
                        }
                    }
                });
            }
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mPresenter != null) {
            mPresenter.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPresenter != null) {
            mPresenter.onPause();
        }
    }

    @Override
    public void onDestroy() {
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }

        if (EventBus.getDefault().isRegistered(mSortChangeEvent)) {
            EventBus.getDefault().unregister(mSortChangeEvent);
        }

        getLoaderManager().destroyLoader(FileManager.LOADER_FILES);
        super.onDestroy();
    }

    // implements OnKeyListener start
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_UP) {
            if (mPathStack.size() > 1) {
                mPathStack.pop();
                restartLoad();
                return true;
            } else if (mPathStack.size() == 1) {
                if (mStorageList.size() == 1) {
                    return false;
                } else {
                    mPathStack.pop();
                    mHLvDirs.setVisibility(View.GONE);
                    mListAdapter.setListItems(mStorageList);
                    mGridAdapter.setListItems(mStorageList);
                    return true;
                }
            }
        }
        return false;
    }

    // implements OnKeyListener end


    // implements LoaderManager.LoaderCallbacks<List<File>> start
    @Override
    public Loader<List<File>> onCreateLoader(int id, Bundle args) {
        if (mPathStack != null && !mPathStack.isEmpty()) {
            return new FileLoader(getActivity(), mPathStack.lastElement().getAbsolutePath(), FileManager.getInstance().getFileSort());
        }
        return new FileLoader(getActivity(), null, FileManager.getInstance().getFileSort());
    }

    @Override
    public void onLoadFinished(Loader<List<File>> loader, List<File> data) {
        if (data != null) {
            mListAdapter.setListItems(data);
            mGridAdapter.setListItems(data);

            if (!mPathStack.isEmpty()) {
                updateCurrentDir(mPathStack.lastElement());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<File>> loader) {
        mListAdapter.clear();
        mGridAdapter.clear();
    }

    // implements LoaderManager.LoaderCallbacks<List<File>> end

    // implements StorageContract.View start

    @Override
    public void updateView() {
        if (mPresenter != null) {
            switch (mPresenter.getStatus()) {
                case StoragePresenter.MAIN_STATUS_PASTE:
                    mLlBottomOperateFirstContainer.setVisibility(View.GONE);
                    mLlBottomOperateSecondContainer.setVisibility(View.VISIBLE);
                    break;
                case StoragePresenter.MAIN_STATUS_NORMAL:
                    mLlBottomOperateFirstContainer.setVisibility(View.GONE);
                    mLlBottomOperateSecondContainer.setVisibility(View.GONE);
                    break;
                case StoragePresenter.MAIN_STATUS_SELECT:
                    mLlBottomOperateFirstContainer.setVisibility(View.VISIBLE);
                    mLlBottomOperateSecondContainer.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void showPasteNeedMoreSpaceDialog(long needMoreSpace) {
        SpaceNotEnoughDialog dialog = new SpaceNotEnoughDialog(getActivity(), needMoreSpace, new SpaceNotEnoughDialog.Listener() {
            @Override
            public void onConfirm(SpaceNotEnoughDialog dialog) {
                // TODO @wangzq
                dialog.dismiss();
            }

            @Override
            public void onCancel(SpaceNotEnoughDialog dialog) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    // implements StorageContract.View end


    // private start
    private void initStoragePath() {
        String[] paths = FileUtil.getVolumePaths(getActivity());
        if (paths != null && paths.length > 0) {
            for (String path : paths) {
                File file = new File(path);
                mStorageList.add(file);
            }
            if (paths.length == 1) {
                mPathStack.push(new File(mStorageList.get(0).getAbsolutePath()));
            }
        }
    }

    public void restartLoad() {
//        mProgressBar.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(FileManager.LOADER_FILES, null, this);
    }

    private void updateCurrentDir(File file) {
        if (mPresenter != null) {
            mPresenter.updateCurrentPath(file.getAbsolutePath());
        }

        String currentPath = "";
        int count = mStorageList.size();
        for (int i = 0; i < count; i++) {
            String filePath = file.getAbsolutePath();
            String storagePath = mStorageList.get(i).getAbsolutePath();
            int index = filePath.indexOf(storagePath);
            if (index != -1) {
                boolean isInternal = FileUtil.isInternalStoragePath(
                        getActivity(), storagePath);
                currentPath = filePath.replace(storagePath, isInternal ? getString(R.string.main_internal_storage)
                        : new File(mStorageList.get(i).getAbsolutePath()).getName());
                break;
            }

        }
        final String[] words = currentPath.split(File.separator);
        mHLvDirs.setVisibility(View.VISIBLE);
        mHLvDirs.setAdapter(new ArrayAdapter<>(getActivity(),
                R.layout.item_main_storage_list_dir, R.id.tv_dir, words));
        mHLvDirs.post(new Runnable() {

            @Override
            public void run() {
                mHLvDirs.scrollTo(Integer.MAX_VALUE);
            }
        });

    }

    private void backToClickedDirectory(String word) {
        String clickDirectory = word;
        for (File file : mStorageList) {
            if ((FileUtil.isInternalStoragePath(getActivity(), file.getAbsolutePath()) && word.equals(getString(R.string.main_internal_storage)))
                    || file.getName().equals(word)) {
                clickDirectory = file.getAbsolutePath();
                break;
            }
        }
        if (mPathStack.isEmpty()) {
            return;
        }
        String currentDir = mPathStack.lastElement().getAbsolutePath();
        if (currentDir.endsWith(clickDirectory)) {
            if (isRootDir(currentDir) && mStorageList.size() > 1) {
                mPathStack.clear();
                mListAdapter.setListItems(mStorageList);
                mGridAdapter.setListItems(mStorageList);
                mHLvDirs.setVisibility(View.GONE);
            }
        } else {
            int index = currentDir.indexOf(clickDirectory);
            String dir = currentDir.substring(0,
                    index + clickDirectory.length());
            Stack<File> temp = new Stack<>();
            for (File file : mPathStack) {
                temp.push(file);
                if (file.getAbsolutePath().equals(dir)) {
                    break;
                }
            }
            mPathStack.clear();
            mPathStack.addAll(temp);
            restartLoad();
        }
    }

    private boolean isRootDir(String path) {
        if (mStorageList.size() > 0) {
            for (File file : mStorageList) {
                if (path.equals(file.getAbsolutePath())) {
                    return true;
                }
            }
        }
        return false;
    }

    // private end

    private abstract static class FileAdapter extends BaseAdapter {
        final LayoutInflater mInflater;
        ImageFetcher mImageFetcher;
        int mImageSize;

        List<File> mData = new ArrayList<>();
        List<File> mRootDirs;

        FileAdapter(Context context, List<File> rootDirs) {
            mInflater = LayoutInflater.from(context);
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            mImageSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, dm);
            mImageFetcher = ImageUtils.createImageFetcher((FragmentActivity) context, mImageSize, R.drawable.img_picture);
            mRootDirs = rootDirs;
        }

        void clear() {
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

        void setListItems(List<File> data) {
            mData = data;
            notifyDataSetChanged();
        }

        void loadFileThumb(String path, ImageView ivThumb) {
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

        boolean isRootDir(String path) {
            if (mRootDirs != null && mRootDirs.size() > 0) {
                for (File file : mRootDirs) {
                    if (path.equals(file.getAbsolutePath())) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private static class FileListAdapter extends FileAdapter {

        WeakReference<StorageContract.Presenter> mPresenterRef;

        FileListAdapter(Context context, List<File> rootDirs, StorageContract.Presenter presenter) {
            super(context, rootDirs);
            mPresenterRef = new WeakReference<>(presenter);
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
                String descString = "";
                if (file.isDirectory()) {
                    String[] subFiles = file.list();
                    descString += mInflater.getContext().getString(R.string.main_directory_count, subFiles.length);
                } else {
                    long fileSize = FileUtil.getSize(file);
                    descString += ConvertUtils.getReadableSizeNoSpace(fileSize);
                }
                long lastModify = file.lastModified();
                descString += " " + TimeUtil.getTime(lastModify, TimeUtil.DATE_FORMATTER_FILE_LAST_MODIFY);

                holder.mTvFileDesc.setText(descString);
            }

            // icon
            if (file.isDirectory()) {
                if (isRootDir(file.getAbsolutePath())) {
                    boolean isInternalStorage = FileUtil.isInternalStoragePath(mInflater.getContext(), file.getAbsolutePath());
                    holder.mIvFileThumb.setImageResource(isInternalStorage ? R.drawable.img_phone_storage : R.drawable.img_sdcard_storage);
                } else {
                    holder.mIvFileThumb.setImageResource(R.drawable.img_folder);
                }
            } else {
                loadFileThumb(file.getAbsolutePath(), holder.mIvFileThumb);
            }

            // 选中标识
            if (mPresenterRef != null && mPresenterRef.get() != null) {
                if (mPresenterRef.get().isSelected(file)) {
                    holder.mIvChecked.setSelected(true);
                } else {
                    holder.mIvChecked.setSelected(false);
                }
                holder.mIvChecked.setTag(position);
                holder.mIvChecked.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mPresenterRef != null && mPresenterRef.get() != null) {
                            int status = mPresenterRef.get().getStatus();
                            if (status == StoragePresenter.MAIN_STATUS_SELECT
                                    || status == StoragePresenter.MAIN_STATUS_NORMAL) {
                                boolean isSelected = holder.mIvChecked.isSelected();
                                holder.mIvChecked.setSelected(!isSelected);

                                File file = getItem(position);
                                mPresenterRef.get().addOrRemoveSelected(file);
                            }
                        }
                    }
                });
            }
            return convertView;
        }

        private static class ViewHolder {
            ImageView mIvFileThumb;
            TextView mTvFileName;
            TextView mTvFileDesc;
            ImageView mIvChecked;
        }
    }

    private static class FileGridAdapter extends FileAdapter {

        WeakReference<StorageContract.Presenter> mPresenterRef;

        FileGridAdapter(Context context, List<File> rootDirs, StorageContract.Presenter presenter) {
            super(context, rootDirs);
            mPresenterRef = new WeakReference<>(presenter);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_main_storage_grid_style, null);
                holder = new ViewHolder();
                holder.mIvFileThumb = (ImageView) convertView.findViewById(R.id.iv_main_storage_style_grid_item_icon);
                holder.mTvFileName = (TextView) convertView.findViewById(R.id.tv_main_storage_style_grid_item_name);
                holder.mIvChecked = (ImageView) convertView.findViewById(R.id.iv_main_storage_style_grid_item_selected);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            File file = getItem(position);
            if (isRootDir(file.getAbsolutePath())) {
                holder.mIvChecked.setVisibility(View.INVISIBLE);

                holder.mTvFileName.setText(FileUtil.isInternalStoragePath(mInflater.getContext(),
                        file.getAbsolutePath()) ? mInflater.getContext().getString(
                        R.string.main_internal_storage) : file.getName().toUpperCase());
            } else {
                holder.mIvChecked.setVisibility(View.VISIBLE);
                holder.mTvFileName.setText(file.getName());
            }

            // icon
            if (file.isDirectory()) {
                if (isRootDir(file.getAbsolutePath())) {
                    boolean isInternalStorage = FileUtil.isInternalStoragePath(mInflater.getContext(), file.getAbsolutePath());
                    holder.mIvFileThumb.setImageResource(isInternalStorage ? R.drawable.img_phone_storage : R.drawable.img_sdcard_storage);
                } else {
                    holder.mIvFileThumb.setImageResource(R.drawable.img_folder);
                }
            } else {
                loadFileThumb(file.getAbsolutePath(), holder.mIvFileThumb);
            }

            // 选中标识
            if (mPresenterRef != null && mPresenterRef.get() != null) {
                if (mPresenterRef.get().isSelected(file)) {
                    holder.mIvChecked.setSelected(true);
                } else {
                    holder.mIvChecked.setSelected(false);
                }
                holder.mIvChecked.setTag(position);
                holder.mIvChecked.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mPresenterRef != null && mPresenterRef.get() != null) {
                            int status = mPresenterRef.get().getStatus();
                            if (status == StoragePresenter.MAIN_STATUS_SELECT
                                    || status == StoragePresenter.MAIN_STATUS_NORMAL) {
                                boolean isSelected = holder.mIvChecked.isSelected();
                                holder.mIvChecked.setSelected(!isSelected);

                                File file = getItem(position);
                                mPresenterRef.get().addOrRemoveSelected(file);
                            }
                        }
                    }
                });
            }


            return convertView;
        }

        private static class ViewHolder {
            ImageView mIvFileThumb;
            TextView mTvFileName;
            ImageView mIvChecked;
        }
    }
}