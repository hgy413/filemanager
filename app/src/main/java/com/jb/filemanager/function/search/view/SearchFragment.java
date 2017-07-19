package com.jb.filemanager.function.search.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jb.filemanager.BaseFragment;
import com.jb.filemanager.Const;
import com.jb.filemanager.R;
import com.jb.filemanager.function.search.modle.FileInfo;
import com.jb.filemanager.function.search.presenter.SearchContract;
import com.jb.filemanager.function.search.presenter.SearchPresenter;
import com.jb.filemanager.function.search.presenter.SearchSupport;
import com.jb.filemanager.util.AppUtils;

import java.util.ArrayList;

/**
 * Created by nieyh on 17-7-6.
 * 搜索的进度的页面 用户在此页面进行搜索 如果结果出来了就跳转到结果页
 */

public class SearchFragment extends BaseFragment implements SearchContract.View, View.OnClickListener {

    private static final int ANIM_SLOW_PLAY_RATE = 1;
    public static final String PARAM_CATEGORY_TYPE = "param_category_type";

    private int mCategoryType;
    //搜索输入框
    private TextView mTvTitle;
    private RelativeLayout mRlSearchContainer;
    private EditText mEtSearchInput;
    private ImageView mIvSearchDelete;
    private ImageView mIvSearch;
    private SearchContract.Presenter mPresenter;

    private LinearLayout mLlAnimContainer;
    private View mViewAnim1;
    private View mViewAnim2;
    private View mViewAnim3;

    private ValueAnimator mAnim;
    private float mLastValue;

    private ArrayList<FileInfo> mSearchResult;
    private boolean mSearchFinished;
    private boolean mAnimPlayOnce;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCategoryType = bundle.getInt(PARAM_CATEGORY_TYPE, Const.CategoryType.CATEGORY_TYPE_ALL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View mask = view.findViewById(R.id.view_fragment_search_mask);
        if (mask != null) {
            mask.setOnClickListener(this);
        }

        ImageView ivBack = (ImageView) view.findViewById(R.id.iv_common_action_bar_back);
        if (ivBack != null) {
            ivBack.setOnClickListener(this);
        }

        mTvTitle = (TextView) view.findViewById(R.id.tv_fragment_search_title);
        if (mTvTitle != null) {
            mTvTitle.getPaint().setAntiAlias(true);
        }

        mRlSearchContainer = (RelativeLayout) view.findViewById(R.id.rl_main_action_bar_search_container);

        mEtSearchInput = (EditText) view.findViewById(R.id.et_action_bar_search);
        if (mEtSearchInput != null) {
            mEtSearchInput.getPaint().setAntiAlias(true);
            mEtSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (mQuickClickGuard.isQuickClick(v.getId())) {
                        return false;
                    }
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        if (mPresenter != null) {
                            mPresenter.search(mEtSearchInput.getText().toString(), mEtSearchInput);
                        }
                        return true;
                    }
                    return false;
                }
            });
            mEtSearchInput.requestFocus();
        }

        mIvSearchDelete = (ImageView) view.findViewById(R.id.iv_action_bar_clear_input);
        if (mIvSearchDelete != null) {
            mIvSearchDelete.setOnClickListener(this);
        }

        mIvSearch = (ImageView) view.findViewById(R.id.iv_action_bar_search);
        if (mIvSearch != null) {
            mIvSearch.setOnClickListener(this);
        }

        mLlAnimContainer = (LinearLayout) view.findViewById(R.id.ll_fragment_search_anim_container);

        mViewAnim1 = view.findViewById(R.id.view_fragment_search_anim_1);
        mViewAnim2 = view.findViewById(R.id.view_fragment_search_anim_2);
        mViewAnim3 = view.findViewById(R.id.view_fragment_search_anim_3);

        mPresenter = new SearchPresenter(this, new SearchSupport());
        mPresenter.onViewCreated();
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
        if (mAnim == null) {
            final float duration1 = 0.135f;
            final float duration2 = 0.1f;
            mAnim = ValueAnimator.ofFloat(0.0f, 0.505f);
            mAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    if (value - mLastValue > 0.001f) {
                        if (value >= 0.0f && value <= 0.235f) {
                            if (value >= 0.0f && value < 0.135f) {
                                float progress = (value / duration1) * (2.4f - 1) + 1;
                                mViewAnim1.setScaleX(progress);
                                mViewAnim1.setScaleY(progress);
                            } else {
                                float progress = 2.4f - ((value - duration1) / duration2) * (2.4f - 1);
                                mViewAnim1.setScaleX(progress);
                                mViewAnim1.setScaleY(progress);
                            }
                        }

                        if (value >= 0.135f && value <= 0.370f) {
                            float value2 = value - 0.135f;
                            if (value2 >= 0.0f && value2 < 0.135f) {
                                float progress = (value2 / duration1) * (2.4f - 1) + 1;
                                mViewAnim2.setScaleX(progress);
                                mViewAnim2.setScaleY(progress);
                            } else {
                                float progress = 2.4f - ((value2 - duration1) / duration2) * (2.4f - 1);
                                mViewAnim2.setScaleX(progress);
                                mViewAnim2.setScaleY(progress);
                            }
                        }

                        if (value >= 0.270f && value <= 0.505f) {
                            float value3 = value - 0.270f;
                            if (value3 >= 0.0f && value3 < 0.135f) {
                                float progress = (value3 / duration1) * (2.4f - 1) + 1;
                                mViewAnim3.setScaleX(progress);
                                mViewAnim3.setScaleY(progress);
                            } else {
                                float progress = 2.4f - ((value3 - duration1) / duration2) * (2.4f - 1);
                                mViewAnim3.setScaleX(progress);
                                mViewAnim3.setScaleY(progress);
                            }
                        }

                        mLastValue = value;
                    }
                }
            });

            mAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (mTvTitle != null) {
                        mTvTitle.setText(mEtSearchInput.getText().toString());
                        mTvTitle.setVisibility(View.VISIBLE);
                    }

                    if (mRlSearchContainer != null) {
                        mRlSearchContainer.setVisibility(View.GONE);
                    }

                    if (mIvSearch != null) {
                        mIvSearch.setVisibility(View.GONE);
                    }

                    mLlAnimContainer.setVisibility(View.VISIBLE);
                    mViewAnim1.setScaleX(1.0f);
                    mViewAnim1.setScaleY(1.0f);
                    mViewAnim2.setScaleX(1.0f);
                    mViewAnim2.setScaleY(1.0f);
                    mViewAnim3.setScaleX(1.0f);
                    mViewAnim3.setScaleY(1.0f);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    super.onAnimationRepeat(animation);
                    mLastValue = 0.0f;

                    mViewAnim1.setScaleX(1.0f);
                    mViewAnim1.setScaleY(1.0f);
                    mViewAnim2.setScaleX(1.0f);
                    mViewAnim2.setScaleY(1.0f);
                    mViewAnim3.setScaleX(1.0f);
                    mViewAnim3.setScaleY(1.0f);

                    mAnimPlayOnce = true;
                    if (mSearchFinished) {
                        doShowResult(mSearchResult);
                    }
                }
            });
            mAnim.setInterpolator(new LinearInterpolator());
            mAnim.setDuration(505 * ANIM_SLOW_PLAY_RATE);
            mAnim.setRepeatMode(ValueAnimator.RESTART);
            mAnim.setRepeatCount(ValueAnimator.INFINITE);
        }
        mAnim.start();
    }

    @Override
    public void showResult(ArrayList<FileInfo> fileInfoList) {
        mSearchResult = fileInfoList;
        mSearchFinished = true;
        if (mAnimPlayOnce) {
            doShowResult(fileInfoList);
        }
    }

    @Override
    public void tipInputEmpty() {
        AppUtils.showToast(getContext(), R.string.toast_search_input_empty);
    }

    @Override
    public void clearInput() {
        if (mEtSearchInput != null) {
            mEtSearchInput.setText("");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopLoadingAnim();
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
            case R.id.view_fragment_search_mask:
            case R.id.iv_common_action_bar_back:
                if (mPresenter != null) {
                    mPresenter.onClickBackButton(false);
                }
                break;
            case R.id.iv_action_bar_search:
                if (mPresenter != null) {
                    mPresenter.search(mEtSearchInput.getText().toString(), mEtSearchInput);
                }
                break;
            case R.id.iv_action_bar_clear_input:
                if (mPresenter != null) {
                    mPresenter.onCLickClearInputButton();
                }
                break;
        }
    }

    private void stopLoadingAnim() {
        if (mAnim != null) {
            mAnim.cancel();
            mAnim.removeAllUpdateListeners();
            mAnim.removeAllListeners();
            mAnim = null;
            mLastValue = 0.0f;
        }
    }

    private void doShowResult(ArrayList<FileInfo> fileInfoList) {
        SearchResultFragment searchResultFragment = new SearchResultFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(SearchResultFragment.ARG, fileInfoList);
        searchResultFragment.setArguments(bundle);
        replaceFragment(searchResultFragment);
    }
}
