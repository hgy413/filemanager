package com.jb.filemanager.function.image;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ExpandableListView;

import com.jb.filemanager.BaseFragment;
import com.jb.filemanager.Const;
import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.eventbus.IOnEventMainThreadSubscriber;
import com.jb.filemanager.function.filebrowser.FileBrowserActivity;
import com.jb.filemanager.function.image.adapter.ImageExpandableAdapter;
import com.jb.filemanager.function.image.modle.ImageGroupModle;
import com.jb.filemanager.function.image.presenter.ImageContract;
import com.jb.filemanager.function.image.presenter.ImagePresenter;
import com.jb.filemanager.function.image.presenter.ImageSupport;
import com.jb.filemanager.manager.file.FileManager;
import com.jb.filemanager.ui.widget.BottomOperateBar;
import com.jb.filemanager.ui.widget.CommonLoadingView;
import com.jb.filemanager.ui.widget.CommonTitleBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.jb.filemanager.function.filebrowser.FileBrowserActivity.REQUEST_CODE_PASTE;

/**
 * Created by nieyh on 17-7-3.
 */

public class ImageManagerFragment extends BaseFragment implements ImageContract.View, CommonTitleBar.OnActionListener, BottomOperateBar.Listener, LoaderManager.LoaderCallbacks<Cursor>{

    private ImageContract.Presenter mPresenter;
    //是否是内部存储
    public static final String ARG_IS_INTERNAL_STORAGE = "arg_is_internal_storage";
    //是否内部存储
    private boolean isInternalStorage = false;
    //图片列表
    private ExpandableListView mExpandableListView;
    private ImageExpandableAdapter mAdapter;
    private CommonTitleBar mCommonTitleBar;
    private BottomOperateBar mBobBottomOperator;
    private ViewStub mEmptyViewStub;
    private ViewStub mExpandableListStub;
    private CommonLoadingView mCommonLoadingView;
    private boolean isLoaded = false;
    //图片删除事件监听器
    private IOnEventMainThreadSubscriber<ImageFileDeleteEvent> mImageFileDeleteEventSubscriber = new IOnEventMainThreadSubscriber<ImageFileDeleteEvent>() {
        @Override
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(ImageFileDeleteEvent event) {
            if (mPresenter != null) {
                mPresenter.handleDeletedBg(event.mImageModle);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!TheApplication.getGlobalEventBus().isRegistered(mImageFileDeleteEventSubscriber)) {
            TheApplication.getGlobalEventBus().register(mImageFileDeleteEventSubscriber);
        }
        handleExtras();
        mCommonTitleBar = (CommonTitleBar) view.findViewById(R.id.fragment_image_title_bar);
        mCommonTitleBar.setBarDefaultTitle(R.string.image_title);
        mCommonTitleBar.setOnActionListener(this);
        mExpandableListStub = (ViewStub) view.findViewById(R.id.fragment_image_el);
        mCommonLoadingView = (CommonLoadingView) view.findViewById(R.id.fragment_image_loading);
        mEmptyViewStub = (ViewStub) view.findViewById(R.id.fragment_image_empty_vs);
        mBobBottomOperator = (BottomOperateBar) view.findViewById(R.id.fragment_image_bob);
        mBobBottomOperator.setListener(this);
        mPresenter = new ImagePresenter(this, new ImageSupport(isInternalStorage));
    }

    /**
     * 设置额外数据
     */
    public void setExtras(boolean isInternalStorage) {
        Bundle extras = new Bundle();
        extras.putBoolean(ARG_IS_INTERNAL_STORAGE, isInternalStorage);
        setArguments(extras);
    }

    /**
     * 处理数据
     */
    private void handleExtras() {
        Bundle extras = getArguments();
        if (extras != null) {
            isInternalStorage = extras.getBoolean(ARG_IS_INTERNAL_STORAGE, false);
        }
    }

    @Override
    public void bindData(List<ImageGroupModle> imageGroupModleList) {
        if (imageGroupModleList != null && imageGroupModleList.size() > 0) {
            if (mAdapter == null) {
                mExpandableListView = (ExpandableListView) mExpandableListStub.inflate();
                mAdapter = new ImageExpandableAdapter(imageGroupModleList, this, mPresenter);
                mExpandableListView.setAdapter(mAdapter);
                mExpandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                    @Override
                    public void onGroupCollapse(int groupPosition) {
                        ImageStatistics.upload(ImageStatistics.IMG_GROUP_FLOD);
                    }
                });
            }
            //默认全部展开
            for (int i = 0; i < imageGroupModleList.size(); i++) {
                mExpandableListView.expandGroup(i);
            }
            //绑定数据
            mAdapter.notifyDataSetChanged();
        } else {
            mEmptyViewStub.inflate();
        }
    }

    @Override
    public void showSelected(int selectSize, int allSize) {
        if (mCommonTitleBar != null) {
            mCommonTitleBar.notifyChoose(selectSize, allSize);
        }
        if (selectSize > 0) {
            showBobar();
        } else {
            dismissBobar();
        }
    }

    @Override
    public void notifyViewChg() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void finish() {
        onToBack();
    }

    @Override
    public void gotoStoragePage() {
        startActivityForResult(FileBrowserActivity.getBrowserForPasteIntent(getActivity(), null), REQUEST_CODE_PASTE);
    }

    @Override
    public boolean onBackPressed() {
        dismissBobar();
        if (mCommonTitleBar != null) {
            boolean b = mCommonTitleBar.onBackPressed();
            if (!b) {
                ImageStatistics.upload(ImageStatistics.IMG_EXIT, 1);
            }
            return !b;
        }
        return super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PASTE && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                boolean isPaste = bundle.getBoolean(FileBrowserActivity.RETURN_PARAM_IS_PASTE);
                //还有问题 当粘贴失败时 无法准确判断
//                if (isPaste) {
//                    clearSelected();
//                }
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mAdapter != null) {
            mAdapter.release();
        }
        if (mPresenter != null) {
            mPresenter.release();
        }
        mPresenter = null;
        if (TheApplication.getGlobalEventBus().isRegistered(mImageFileDeleteEventSubscriber)) {
            TheApplication.getGlobalEventBus().unregister(mImageFileDeleteEventSubscriber);
        }
        super.onDestroy();
    }

    @Override
    public void dismissBobar() {
        if (mBobBottomOperator != null) {
            mBobBottomOperator.setVisibility(View.GONE);
        }
    }

    @Override
    public void showBobar() {
        if (mBobBottomOperator != null) {
            mBobBottomOperator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void clearSelected() {
        mCommonTitleBar.onBackPressed();
        dismissBobar();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = isInternalStorage ? MediaStore.Images.Media.INTERNAL_CONTENT_URI : MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        mCommonLoadingView.startLoading();
        return new CursorLoader(getActivity(),
                uri,
                null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_ADDED + " desc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (mPresenter != null) {
            mPresenter.handleDataFinish(cursor);
        }
        mCommonLoadingView.stopLoading();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isLoaded) {
            loadData();
            isLoaded = true;
        } else {
            updateLoadData();
        }
    }

    private void loadData() {
        getLoaderManager().initLoader(FileManager.LOADER_IMAGE, null,this);
    }

    /**
     * 重置数据
     * */
    private void updateLoadData() {
        getLoaderManager().restartLoader(FileManager.LOADER_IMAGE, null, this);
    }

    @Override
    public void onCheckAction(boolean isCheck) {
        //check
        if (mPresenter != null) {
            mPresenter.handleCheck(isCheck);
        }
    }

    @Override
    public void onBackAction() {
        //back
        if (mPresenter != null) {
            mPresenter.handleBackClick();
        }
    }

    @Override
    public void onCancelAction() {
        //cancel
        if (mPresenter != null) {
            mPresenter.handleCancel();
        }
    }

    @Override
    public int getCategoryType() {
        return Const.CategoryType.CATEGORY_TYPE_PHOTO;
    }

    @Override
    public void onSearchAction() {
        ImageStatistics.upload(ImageStatistics.IMG_SEARCH_CLI);
    }

    /**
     * 底部栏的功能接口
     */

    @Override
    public ArrayList<File> getCurrentSelectedFiles() {
        if (mPresenter != null) {
            return mPresenter.getCurrentSelectedFiles();
        }
        return new ArrayList<>();
    }

    @Override
    public void afterCopy() {
        gotoStoragePage();
        if (mPresenter != null) {
            mPresenter.handleCopy();
        }
    }

    @Override
    public void afterCut() {
        gotoStoragePage();
        if (mPresenter != null) {
            mPresenter.handleCut();
        }
    }

    @Override
    public void afterRename() {
        if (mPresenter != null) {
            mPresenter.handleRename();
        }
        clearSelected();
    }

    @Override
    public void afterDelete() {
        if (mPresenter != null) {
            mPresenter.handleDeleted();
        }
        clearSelected();
    }

    @Override
    public void statisticsClickCopy() {
        ImageStatistics.upload(ImageStatistics.IMG_COPY_CLI);
    }

    @Override
    public void statisticsClickCut() {
        ImageStatistics.upload(ImageStatistics.IMG_CUT_CLI);
    }

    @Override
    public void statisticsClickDelete() {
        ImageStatistics.upload(ImageStatistics.IMG_DELETE_CLI);
    }

    @Override
    public void statisticsClickMore() {
        ImageStatistics.upload(ImageStatistics.IMG_MORE_CLI);
    }

    @Override
    public void statisticsClickRename() {
        ImageStatistics.upload(ImageStatistics.IMG_RENAME_CLI);
    }

    @Override
    public void statisticsClickDetail() {
        ImageStatistics.upload(ImageStatistics.IMG_DETAILS_CLI);
    }
}
