package com.jb.filemanager.function.image;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.function.image.adapter.ImageExpandableAdapter;
import com.jb.filemanager.function.image.modle.ImageGroupModle;
import com.jb.filemanager.function.image.app.BaseFragment;
import com.jb.filemanager.function.image.presenter.ImageContract;
import com.jb.filemanager.function.image.presenter.ImagePresenter;
import com.jb.filemanager.function.image.presenter.ImageSupport;
import com.jb.filemanager.function.search.view.SearchActivity;
import com.jb.filemanager.manager.file.FileManager;

import java.util.List;

/**
 * Created by nieyh on 17-7-3.
 */

public class ImageManagerFragment extends BaseFragment implements View.OnClickListener, ImageContract.View {

    private ImageContract.Presenter mPresenter;
    //是否是内部存储
    private static final String ARG_IS_INTERNAL_STORAGE = "arg_is_internal_storage";
    //是否内部存储
    private boolean isInternalStorage = false;
    //图片列表
    private ExpandableListView mExpandableListView;
    private ImageExpandableAdapter mAdapter;
    private ImageView mSearch;
    private GroupSelectBox mGroupSelectBox;
    private TextView mTitle;
    private ImageView mBack;
    private ImageView mCancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handleExtras();
        mTitle = (TextView) view.findViewById(R.id.fragment_image_title);
        mBack = (ImageView) view.findViewById(R.id.fragment_image_back);
        mCancel = (ImageView) view.findViewById(R.id.fragment_image_cancel);
        if (mTitle != null) {
            mTitle.getPaint().setAntiAlias(true);
            mTitle.setText(R.string.image_title);
        }
        mSearch = (ImageView) view.findViewById(R.id.fragment_image_search);
        mGroupSelectBox = (GroupSelectBox) view.findViewById(R.id.fragment_image_check_group);
        mGroupSelectBox.setImageSource(R.drawable.choose_none, R.drawable.choose_all, R.drawable.choose_all);
        mSearch.setOnClickListener(this);
        mExpandableListView = (ExpandableListView) view.findViewById(R.id.fragment_image_el);
        mCancel.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mGroupSelectBox.setOnClickListener(this);
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
    public void showSelected(int size) {
        if (size > 0) {
            //修改标题
            mTitle.setText(size + ">> 被选");
            mGroupSelectBox.setVisibility(View.VISIBLE);
            mGroupSelectBox.setState(GroupSelectBox.SelectState.NONE_SELECTED);
            mCancel.setVisibility(View.VISIBLE);
            mSearch.setVisibility(View.GONE);
            mBack.setVisibility(View.GONE);
        }
    }

    @Override
    public void showNoSelected() {
        //修改标题
        mTitle.setText(R.string.image_title);
        mGroupSelectBox.setVisibility(View.GONE);
        mCancel.setVisibility(View.GONE);
        mSearch.setVisibility(View.VISIBLE);
        mBack.setVisibility(View.VISIBLE);
    }

    @Override
    public void showAllSelected() {
        mGroupSelectBox.setState(GroupSelectBox.SelectState.ALL_SELECTED);
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
    public void onPressedHomeKey() {
        if (mPresenter != null) {
            mPresenter.handlePressHomeKey();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (mPresenter != null) {
            mPresenter.handleBackPressed();
        }
        return super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_image_back:
                if (mPresenter != null) {
                    mPresenter.handleBackClick();
                }
                break;
            case R.id.fragment_image_search:
                SearchActivity.showSearchResult(getActivity());
                break;
            case R.id.fragment_image_cancel:
                //cancel
                if (mPresenter != null) {
                    mPresenter.handleCancel();
                }
                break;
            case R.id.fragment_image_check_group:
                //check
                if (mPresenter != null) {
                    mPresenter.handleCheck(mGroupSelectBox.getState() == GroupSelectBox.SelectState.ALL_SELECTED);
                }
                break;
            default:
                break;
        }
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
}
