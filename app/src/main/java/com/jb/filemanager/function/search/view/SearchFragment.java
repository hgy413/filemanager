package com.jb.filemanager.function.search.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.R;
import com.jb.filemanager.function.image.app.BaseFragment;
import com.jb.filemanager.function.search.modle.FileInfo;
import com.jb.filemanager.function.search.presenter.SearchContract;
import com.jb.filemanager.function.search.presenter.SearchPresenter;
import com.jb.filemanager.function.search.presenter.SearchSupport;

import java.util.ArrayList;

/**
 * Created by nieyh on 17-7-6.
 * 搜索的进度的页面 用户在此页面进行搜索 如果结果出来了就跳转到结果页
 */

public class SearchFragment extends BaseFragment implements SearchContract.View, View.OnClickListener {
    //搜索输入框
    private EditText mEtSearchInput;
    private ImageView mIvSearchDelete;
    private ImageView mIvSearch;
    private SearchContract.Presenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView ivBack = (ImageView) view.findViewById(R.id.iv_common_action_bar_back);
        if (ivBack != null) {
            ivBack.setOnClickListener(this);
        }

        mEtSearchInput = (EditText) view.findViewById(R.id.et_action_bar_search);
        if (mEtSearchInput != null) {
            mEtSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (mQuickClickGuard.isQuickClick(v.getId())) {
                        return false;
                    }
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        if (mPresenter != null) {
                            mPresenter.search(mEtSearchInput.getText().toString(), getActivity());
                        }
                        return true;
                    }
                    return false;
                }
            });
        }

        mIvSearchDelete = (ImageView) view.findViewById(R.id.iv_action_bar_clear_input);
        if (mIvSearchDelete != null) {
            mIvSearchDelete.setOnClickListener(this);
        }

        mIvSearch = (ImageView) view.findViewById(R.id.iv_action_bar_search);
        if (mIvSearch != null) {
            mIvSearch.setOnClickListener(this);
        }

        mPresenter = new SearchPresenter(this, new SearchSupport());
        mPresenter.onViewCreated(mEtSearchInput);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.release();
        }
    }

    @Override
    public void showLoading() {
        // TODO: 17-7-6 展示正在加载动画
        Toast.makeText(getContext(), "请脑补有个加载动画！！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void dissmissLoading() {
        // TODO: 17-7-6 消失加载动画
        Toast.makeText(getContext(), "动画消失了", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showResult(ArrayList<FileInfo> fileInfoList) {
        SearchResultFragment searchResultFragment = new SearchResultFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(SearchResultFragment.ARG, fileInfoList);
        searchResultFragment.setArguments(bundle);
        replaceFragment(searchResultFragment);
    }

    @Override
    public void tipInputEmpty() {
        Toast.makeText(getContext(), "多输入几个字会死??", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void clearInput() {
        if (mEtSearchInput != null) {
            mEtSearchInput.setText("");
        }
    }

    @Override
    public void finishActivity() {
        getActivity().finish();
    }

    @Override
    public boolean onBackPressed() {
        if (mPresenter != null) {
            mPresenter.onClickBackButton(true);
            return true;
        }
        return super.onBackPressed();
    }

    // implements View.OnClickListener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_common_action_bar_back:
                if (mPresenter != null) {
                    mPresenter.onClickBackButton(false);
                }
                break;
            case R.id.iv_action_bar_search:
                if (mPresenter != null) {
                    mPresenter.search(mEtSearchInput.getText().toString(), getActivity());
                }
                break;
            case R.id.iv_action_bar_clear_input:
                if (mPresenter != null) {
                    mPresenter.onCLickClearInputButton();
                }
                break;
        }
    }
}
