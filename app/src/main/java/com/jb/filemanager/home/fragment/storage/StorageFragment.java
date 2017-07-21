package com.jb.filemanager.home.fragment.storage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
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

import com.jb.filemanager.BaseFragment;
import com.jb.filemanager.Const;
import com.jb.filemanager.R;
import com.jb.filemanager.manager.PackageManagerLocker;
import com.jb.filemanager.manager.file.FileManager;
import com.jb.filemanager.ui.dialog.SpaceNotEnoughDialog;
import com.jb.filemanager.ui.widget.BottomOperateBar;
import com.jb.filemanager.ui.widget.HorizontalListView;
import com.jb.filemanager.util.ConvertUtils;
import com.jb.filemanager.util.FileUtil;
import com.jb.filemanager.util.TimeUtil;
import com.jb.filemanager.util.images.ImageFetcher;
import com.jb.filemanager.util.images.ImageUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bill wang on 2017/6/22.
 *
 */

public class StorageFragment extends BaseFragment implements View.OnKeyListener,
        StorageContract.View {

    public static final String PARAM_PATH = "param_path";

    private ImageView mIvStorageDisk;
    private HorizontalListView mHLvDirs;
    private ImageView mIvStyleSwitcher;
    private ListView mLvFiles;
    private GridView mGvFiles;

    private FileListAdapter mListAdapter;
    private FileGridAdapter mGridAdapter;

    private BottomOperateBar mLlBottomOperateFirstContainer;
    private LinearLayout mLlBottomOperateSecondContainer;

    private StorageContract.Presenter mPresenter;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new StoragePresenter(this, new StorageSupport(this));
        mPresenter.onCreate(getArguments());

        mListAdapter = new FileListAdapter(getActivity(), mPresenter);
        mGridAdapter = new FileGridAdapter(getActivity(), mPresenter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mPresenter != null) {
            mPresenter.onActivityCreated();
        }

        mLvFiles.setAdapter(mListAdapter);
        mGvFiles.setAdapter(mGridAdapter);
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
            String currentPath = null;
            if (mPresenter != null) {
                currentPath = mPresenter.getCurrentPath();
            }
            if (!TextUtils.isEmpty(currentPath)) {
                boolean isInternal = FileUtil.isInternalStoragePath(getActivity(), currentPath);
                mIvStorageDisk.setImageResource(isInternal ? R.drawable.img_phone_storage : R.drawable.img_sdcard_storage);
            }
        }

        mHLvDirs = (HorizontalListView) rootView.findViewById(R.id.lv_dirs);
        mHLvDirs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String word = (String) ((HorizontalListView) parent).getAdapter().getItem(position);
                if (mPresenter != null) {
                    mPresenter.onClickPath(word);
                }
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

                if (mPresenter != null) {
                    File file = adapter.getItem(position);
                    mPresenter.onClickItem(file, view.getTag());
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
                    File file = adapter.getItem(position);
                    mPresenter.onClickItem(file, view.getTag());
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
        super.onDestroy();
    }

    // implements OnKeyListener start
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && mPresenter.onClickSystemBack();
    }

    // implements OnKeyListener end




    // implements StorageContract.View start


    @Override
    public void updateListAndGrid() {
        mListAdapter.notifyDataSetChanged();
        mGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateBottomBar() {
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
    public void updateCurrentPath(List<File> data, File currentPath) {
        if (data != null) {
            mListAdapter.setListItems(data);
            mGridAdapter.setListItems(data);

            if (currentPath == null) {
                mHLvDirs.setVisibility(View.INVISIBLE);
                mIvStorageDisk.setVisibility(View.INVISIBLE);
            } else {
                updateCurrentDir(currentPath);
            }
        } else {
            mListAdapter.clear();
            mGridAdapter.clear();
        }
    }

    @Override
    public void updateItemSelectStatus(Object holder) {
        if (holder != null) {
            if (holder instanceof ListItemViewHolder) {
                ListItemViewHolder viewHolder = (ListItemViewHolder) holder;
                boolean isSelected = viewHolder.mIvChecked.isSelected();
                viewHolder.mIvChecked.setSelected(!isSelected);
            } else if (holder instanceof GridItemViewHolder) {
                GridItemViewHolder viewHolder = (GridItemViewHolder) holder;
                boolean isSelected = viewHolder.mIvChecked.isSelected();
                viewHolder.mIvChecked.setSelected(!isSelected);
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

    private void updateCurrentDir(File file) {
        if (mPresenter != null) {
            String currentPath = "";
            ArrayList<File> storageList = mPresenter.getStorageList();
            if (storageList != null && storageList.size() > 0) {
                int count = storageList.size();
                for (int i = 0; i < count; i++) {
                    String filePath = file.getAbsolutePath();
                    String storagePath = storageList.get(i).getAbsolutePath();
                    int index = filePath.indexOf(storagePath);
                    if (index != -1) {
                        boolean isInternal = FileUtil.isInternalStoragePath(
                                getActivity(), storagePath);
                        currentPath = filePath.replace(storagePath, isInternal ? getString(R.string.main_internal_storage)
                                : new File(storageList.get(i).getAbsolutePath()).getName());
                        break;
                    }
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

            if (mIvStorageDisk.getVisibility() == View.INVISIBLE) {
                // 已经是可见的情况下不存在更换 磁盘 图标的可能
                mIvStorageDisk.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(currentPath)) {
                    boolean isInternal = FileUtil.isInternalStoragePath(getActivity(), currentPath);
                    mIvStorageDisk.setImageResource(isInternal ? R.drawable.img_phone_storage : R.drawable.img_sdcard_storage);
                }
            }
        }
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

        FileListAdapter(Context context, StorageContract.Presenter presenter) {
            super(context, presenter == null ? null : presenter.getStorageList());
            mPresenterRef = new WeakReference<>(presenter);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ListItemViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_main_storage_list_style, null);
                holder = new ListItemViewHolder();
                holder.mIvFileThumb = (ImageView) convertView.findViewById(R.id.iv_main_storage_style_list_item_icon);
                holder.mTvFileName = (TextView) convertView.findViewById(R.id.tv_main_storage_style_list_item_name);
                holder.mTvFileDesc = (TextView) convertView.findViewById(R.id.tv_main_storage_style_list_item_desc);
                holder.mIvChecked = (ImageView) convertView.findViewById(R.id.iv_main_storage_style_list_item_selected);
                convertView.setTag(holder);
            } else {
                holder = (ListItemViewHolder) convertView.getTag();
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
    }

    private static class ListItemViewHolder {
        ImageView mIvFileThumb;
        TextView mTvFileName;
        TextView mTvFileDesc;
        ImageView mIvChecked;
    }

    private static class FileGridAdapter extends FileAdapter {

        WeakReference<StorageContract.Presenter> mPresenterRef;

        FileGridAdapter(Context context, StorageContract.Presenter presenter) {
            super(context, presenter == null ? null : presenter.getStorageList());
            mPresenterRef = new WeakReference<>(presenter);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final GridItemViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_main_storage_grid_style, null);
                holder = new GridItemViewHolder();
                holder.mIvFileThumb = (ImageView) convertView.findViewById(R.id.iv_main_storage_style_grid_item_icon);
                holder.mTvFileName = (TextView) convertView.findViewById(R.id.tv_main_storage_style_grid_item_name);
                holder.mIvChecked = (ImageView) convertView.findViewById(R.id.iv_main_storage_style_grid_item_selected);
                convertView.setTag(holder);
            } else {
                holder = (GridItemViewHolder) convertView.getTag();
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
    }

    private static class GridItemViewHolder {
        ImageView mIvFileThumb;
        TextView mTvFileName;
        ImageView mIvChecked;
    }
}