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
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.function.image.adapter.ImageAdapter;
import com.jb.filemanager.function.image.modle.ImageGroupModle;
import com.jb.filemanager.function.image.app.BaseFragment;
import com.jb.filemanager.function.image.presenter.ImageContract;
import com.jb.filemanager.function.image.presenter.ImagePresenter;
import com.jb.filemanager.function.image.presenter.ImageSupport;
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
    private RecyclerView mImageManagerFragmentListView;
    private GridLayoutManager mGridLayoutManager;
    private ImageAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handleExtras();
        TextView back = (TextView) view.findViewById(R.id.tv_common_action_bar_with_search_title);
        if (back != null) {
            back.getPaint().setAntiAlias(true);
            back.setText(R.string.image_title);
            back.setOnClickListener(this);
        }

        mImageManagerFragmentListView = (RecyclerView) view.findViewById(R.id.fragment_image_rv);
        mPresenter = new ImagePresenter(this, new ImageSupport());
        loadData();
    }

    /**
     * 设置额外数据
     * */
    public void setExtras(boolean isInternalStorage) {
        Bundle extras = new Bundle();
        extras.putBoolean(ARG_IS_INTERNAL_STORAGE, isInternalStorage);
        setArguments(extras);
    }

    /**
     * 处理数据
     * */
    private void handleExtras() {
        Bundle extras = getArguments();
        if (extras != null) {
            isInternalStorage = extras.getBoolean(ARG_IS_INTERNAL_STORAGE, false);
        }
    }

    @Override
    public void bindData(List<ImageGroupModle> imageGroupModleList) {
        if (mAdapter == null) {
            mAdapter = new ImageAdapter(imageGroupModleList, this);
            mGridLayoutManager = new GridLayoutManager(getContext(), 4);
            mImageManagerFragmentListView.setAdapter(mAdapter);
            mImageManagerFragmentListView.setLayoutManager(mGridLayoutManager);
        }
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                //如果是标题 则返回4
                //反之1
                if (mAdapter.getItemViewType(position) == 0) {
                    return 4;
                }
                return 1;
            }
        });
        //绑定数据
        mAdapter.notifyDataSetChanged();
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
            case R.id.tv_common_action_bar_with_search_title:
                if (mPresenter != null) {
                    mPresenter.handleBackClick();
                }
                break;
            case R.id.iv_common_action_bar_with_search_search:
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
                        new String[] { "image/jpeg", "image/png" },
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
