package com.jb.filemanager.function.search.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.rate.RateManager;
import com.jb.filemanager.function.search.modle.FileInfo;
import com.jb.filemanager.util.AppUtils;

import java.util.ArrayList;

/**
 * Created by nieyh on 17-7-5.
 * 搜索视图 <br/>
 * 如果需要搜索的话 请跳转到这个activity 其他的就不用你管了
 * {@link #showSearchResult(Context, int)}
 */

public class SearchActivity extends BaseActivity implements SearchContract.View, View.OnClickListener {

    private static final int ANIM_SLOW_PLAY_RATE = 1;

    static final String PARAM_CATEGORY_TYPE = "param_category_type";

    // title
    private TextView mTvTitle;
    private RelativeLayout mRlSearchContainer;
    private EditText mEtSearchInput;
    private ImageView mIvSearchDelete;
    private ImageView mIvSearch;

    // mask
    private View mViewSearchMask;

    // anim
    private LinearLayout mLlAnimContainer;
    private View mViewAnim1;
    private View mViewAnim2;
    private View mViewAnim3;

    // result
    private View mViewSearchResultBg;
    private TextView mTvResultEmptyTips;
    private RecyclerView mRvResultList;
    private SearchResultAdapter mAdapter;

    private ValueAnimator mAnim;
    private float mLastValue;


    private SearchContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();

        mPresenter = new SearchPresenter(this, new SearchSupport());
        mPresenter.onCreate(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (mPresenter != null) {
            mPresenter.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        super.onDestroy();
    }

    /**
     * 展示搜索
     * */
    public static void showSearchResult(Context context, int categoryType) {
        Intent intent = new Intent(context, SearchActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        //去掉动画
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        intent.putExtra(PARAM_CATEGORY_TYPE, categoryType);
        context.startActivity(intent);
    }

    // implement SearchContract.View
    @Override
    public void showInputEmptyTips() {
        AppUtils.showToast(this, R.string.toast_search_input_empty);
    }

    @Override
    public void showSearchAnim() {
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

                    if (mViewSearchMask != null) {
                        mViewSearchMask.setVisibility(View.GONE);
                    }

                    if (mLlAnimContainer != null) {
                        mLlAnimContainer.setVisibility(View.VISIBLE);
                    }

                    if (mViewSearchResultBg != null) {
                        mViewSearchResultBg.setVisibility(View.GONE);
                    }

                    if (mRvResultList != null) {
                        mRvResultList.setVisibility(View.GONE);
                    }

                    if (mTvResultEmptyTips != null) {
                        mTvResultEmptyTips.setVisibility(View.GONE);
                    }

                    mViewAnim1.setScaleX(1.0f);
                    mViewAnim1.setScaleY(1.0f);
                    mViewAnim2.setScaleX(1.0f);
                    mViewAnim2.setScaleY(1.0f);
                    mViewAnim3.setScaleX(1.0f);
                    mViewAnim3.setScaleY(1.0f);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    mLastValue = 0.0f;

                    mViewAnim1.setScaleX(1.0f);
                    mViewAnim1.setScaleY(1.0f);
                    mViewAnim2.setScaleX(1.0f);
                    mViewAnim2.setScaleY(1.0f);
                    mViewAnim3.setScaleX(1.0f);
                    mViewAnim3.setScaleY(1.0f);

                    if (mPresenter != null) {
                        mPresenter.onAnimRepeat();
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
    public void stopSearchAnim() {
        if (mAnim != null) {
            mAnim.cancel();
            mAnim.removeAllUpdateListeners();
            mAnim.removeAllListeners();
            mAnim = null;
            mLastValue = 0.0f;
        }
    }

    @Override
    public void showSearchResult(ArrayList<FileInfo> fileInfoList) {
        boolean hasResult = fileInfoList != null && fileInfoList.size() > 0;

        if (mRlSearchContainer != null) {
            mRlSearchContainer.setVisibility(View.GONE);
        }

        if (mIvSearch != null) {
            mIvSearch.setVisibility(View.GONE);
        }

        if (mTvTitle != null) {
            mTvTitle.setVisibility(View.VISIBLE);
            mTvTitle.setText(getString(R.string.search_result_title, hasResult ? fileInfoList.size() : 0));
        }

        if (mLlAnimContainer != null) {
            mLlAnimContainer.setVisibility(View.GONE);
        }

        if (mViewSearchResultBg != null) {
            mViewSearchResultBg.setVisibility(View.VISIBLE);
        }

        if (mRvResultList != null) {
            mRvResultList.setVisibility(hasResult ? View.VISIBLE : View.GONE);
        }

        if (mTvResultEmptyTips != null) {
            mTvResultEmptyTips.setVisibility(hasResult ? View.GONE : View.VISIBLE);
        }

        if (mAdapter != null) {
            mAdapter.setData(fileInfoList);
        }
    }

    @Override
    public void showKeyboard() {
        // 显示键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void hideKeyboard() {
        // 隐藏键盘
        if (mEtSearchInput != null && mEtSearchInput.getWindowToken() != null) {
            InputMethodManager imm = (InputMethodManager) TheApplication.getInstance().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEtSearchInput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void clearInput() {
        if (mEtSearchInput != null) {
            mEtSearchInput.setText("");
        }
    }

    @Override
    public void finishActivity() {
        finish();
        // 小米自己走动画，必须重载空动画
        overridePendingTransition(R.anim.nothing, R.anim.nothing);
    }

    @Override
    public void onBackPressed() {
        mPresenter.onClickBackButton(true);
    }

    @Override
    public void goToActivity(Intent intent) {
        if (intent != null) {
            startActivity(intent);
        }
    }

    // implement View.OnClickListener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_fragment_search_mask:
                if (mPresenter != null) {
                    mPresenter.onClickMask();
                }
                break;
            case R.id.iv_common_action_bar_back:
                if (mPresenter != null) {
                    mPresenter.onClickBackButton(false);
                }
                break;
            case R.id.iv_action_bar_search:
                if (mPresenter != null && mEtSearchInput != null) {
                    mPresenter.onClickSearch(mEtSearchInput.getText().toString());
                }
                break;
            case R.id.iv_action_bar_clear_input:
                if (mPresenter != null) {
                    mPresenter.onClickClearInputButton();
                }
                break;
        }
    }

    // private
    private void initView() {
        ImageView ivBack = (ImageView) findViewById(R.id.iv_common_action_bar_back);
        if (ivBack != null) {
            ivBack.setOnClickListener(this);
        }

        mTvTitle = (TextView) findViewById(R.id.tv_fragment_search_title);
        if (mTvTitle != null) {
            mTvTitle.getPaint().setAntiAlias(true);
        }

        mRlSearchContainer = (RelativeLayout) findViewById(R.id.rl_main_action_bar_search_container);

        mEtSearchInput = (EditText) findViewById(R.id.et_action_bar_search);
        if (mEtSearchInput != null) {
            mEtSearchInput.getPaint().setAntiAlias(true);
            mEtSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (mQuickClickGuard.isQuickClick(v.getId())) {
                        return false;
                    }
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        if (mPresenter != null && mEtSearchInput != null) {
                            mPresenter.onClickSearchOnKeyboard(mEtSearchInput.getText().toString());
                        }
                        return true;
                    }
                    return false;
                }
            });
            mEtSearchInput.requestFocus();
        }

        mIvSearchDelete = (ImageView) findViewById(R.id.iv_action_bar_clear_input);
        if (mIvSearchDelete != null) {
            mIvSearchDelete.setOnClickListener(this);
        }

        mIvSearch = (ImageView) findViewById(R.id.iv_action_bar_search);
        if (mIvSearch != null) {
            mIvSearch.setOnClickListener(this);
        }

        mViewSearchMask = findViewById(R.id.view_fragment_search_mask);
        if (mViewSearchMask != null) {
            mViewSearchMask.setOnClickListener(this);
        }

        mLlAnimContainer = (LinearLayout) findViewById(R.id.ll_fragment_search_anim_container);
        mViewAnim1 = findViewById(R.id.view_fragment_search_anim_1);
        mViewAnim2 = findViewById(R.id.view_fragment_search_anim_2);
        mViewAnim3 = findViewById(R.id.view_fragment_search_anim_3);

        mViewSearchResultBg = findViewById(R.id.view_search_result_bg);

        mRvResultList = (RecyclerView) findViewById(R.id.fragment_search_result_rv);
        if (mRvResultList != null) {
            mAdapter = new SearchResultAdapter(null);
            mAdapter.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add by nieyh 评分引导逻辑 收集评分因素
                    RateManager.getsInstance().collectTriggeringFactor(RateManager.SEARCH_RESULT_CLICK);
                    FileInfo fileInfo = (FileInfo)v.getTag();

                    if (mPresenter != null) {
                        mPresenter.onClickSearchResult(SearchActivity.this, fileInfo.mFileAbsolutePath);
                    }
                }
            });
            mRvResultList.setLayoutManager(new LinearLayoutManager(this));
            mRvResultList.setAdapter(mAdapter);
        }

        mTvResultEmptyTips = (TextView) findViewById(R.id.tv_search_result_empty_tips);
        if (mTvResultEmptyTips != null) {
            mTvResultEmptyTips.getPaint().setAntiAlias(true);
        }
    }

}
