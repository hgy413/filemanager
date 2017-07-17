package com.jb.filemanager.function.image;

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
import android.widget.ExpandableListView;

import com.jb.filemanager.R;
import com.jb.filemanager.function.image.adapter.ImageExpandableAdapter;
import com.jb.filemanager.function.image.app.BaseFragment;
import com.jb.filemanager.function.image.modle.ImageGroupModle;
import com.jb.filemanager.function.image.presenter.ImageContract;
import com.jb.filemanager.function.image.presenter.ImagePresenter;
import com.jb.filemanager.function.image.presenter.ImageSupport;
import com.jb.filemanager.manager.file.FileManager;
import com.jb.filemanager.ui.widget.CommonTitleBar;

import java.util.List;

/**
 * Created by nieyh on 17-7-3.
 */

public class ImageManagerFragment extends BaseFragment implements ImageContract.View, CommonTitleBar.OnActionListener {

    private ImageContract.Presenter mPresenter;
    //是否是内部存储
    private static final String ARG_IS_INTERNAL_STORAGE = "arg_is_internal_storage";
    //是否内部存储
    private boolean isInternalStorage = false;
    //图片列表
    private ExpandableListView mExpandableListView;
    private ImageExpandableAdapter mAdapter;
    private CommonTitleBar mCommonTitleBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handleExtras();
        mCommonTitleBar = (CommonTitleBar) view.findViewById(R.id.fragment_image_title_bar);
        mCommonTitleBar.setBarDefaultTitle(R.string.image_title);
        mCommonTitleBar.setOnActionListener(this);
        mExpandableListView = (ExpandableListView) view.findViewById(R.id.fragment_image_el);
        mPresenter = new ImagePresenter(this, new ImageSupport());
        loadData();
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
        if (mAdapter == null) {
            mAdapter = new ImageExpandableAdapter(imageGroupModleList, this, mPresenter);
            mExpandableListView.setAdapter(mAdapter);
            //默认全部展开
            for (int i = 0; i < imageGroupModleList.size(); i++) {
                mExpandableListView.expandGroup(i);
            }
        }
        //绑定数据
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showSelected(int selectSize, int allSize) {
        if (mCommonTitleBar != null) {
            mCommonTitleBar.notifyChoose(selectSize, allSize);
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
    public boolean onBackPressed() {
        if (mCommonTitleBar != null) {
            return !mCommonTitleBar.onBackPressed();
        }
        return super.onBackPressed();
    }

    private void loadData() {
        getLoaderManager().initLoader(FileManager.LOADER_IMAGE, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Uri uri = isInternalStorage ? MediaStore.Images.Media.INTERNAL_CONTENT_URI : MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                return new CursorLoader(getActivity(),
                        uri,
                        null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_ADDED);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                if (mPresenter != null) {
                    mPresenter.handleDataFinish(cursor);
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        });
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
}
