package com.jb.filemanager.function.feedback;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.statistics.StatisticsTools;
import com.jb.filemanager.util.APIUtil;
import com.jb.filemanager.util.AppUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bill wang on 2017/6/21.
 *
 */

public class FeedbackActivity extends BaseActivity implements FeedbackContract.View {

    private EditText mContainer; //正文
    private ImageView imageView; //下拉框箭头
    private LinearLayout menu_common; //下拉框
    private TextView problem; //下拉框文本
    private TextView forceInstall; //下拉框文本
    private TextView suggestion; //下拉框文本
    private TextView select; //下拉框文本
    private TextView mWarnTipContent;
    private View mWarnTipLayout;
    private TextView mWarnTipReport;
    private TextView mWarnTipNo;
    private TextView mTvCommonActionBarTitle;
    private ImageView mIvCommonActionBarMore;

    private FeedbackContract.Presenter mPresenter;
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        init();
    }

    private void init() {
        mPresenter = new FeedbackPresenter(this, new FeedbackSupport());

        mTvCommonActionBarTitle = (TextView) findViewById(R.id.tv_common_action_bar_title);
        mIvCommonActionBarMore = (ImageView) findViewById(R.id.iv_common_action_bar_more);

        mTvCommonActionBarTitle.setText(R.string.feedback_title);
        mIvCommonActionBarMore.setVisibility(View.VISIBLE);
        mIvCommonActionBarMore.setImageResource(R.drawable.ic_feedback_submit);

        mContainer = (EditText) findViewById(R.id.container_setting_feedback);
        select = (TextView) findViewById(R.id.setting_feedback_menu_select);
        TextView notice = (TextView) findViewById(R.id.notice_setting_feedback);
        imageView = (ImageView) findViewById(R.id.menu_imageview);
        menu_common = (LinearLayout) findViewById(R.id.setting_feedback_menu);

        mWarnTipContent = (TextView) findViewById(R.id.activity_menu_feedback_tip_content);
        mWarnTipLayout = findViewById(R.id.activity_menu_feedback_tip_layout);
        mWarnTipReport = (TextView) findViewById(R.id.activity_menu_feedback_send);
        mWarnTipNo = (TextView) findViewById(R.id.activity_menu_feedback_close);
        mWarnTipContent.setText(getResources().getString(R.string.feedback_tip_content, getResources().getString(R.string.app_name)));
        mWarnTipNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWarnTipLayout.setVisibility(View.GONE);
            }
        });

        mWarnTipReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //上传103统计
                uploadStatistic();
                //处理
                if (mPresenter != null) {
                    mPresenter.sendWarnTip();
                }
                mWarnTipLayout.setVisibility(View.GONE);
            }
        });

        //点击返回
        mTvCommonActionBarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPresenter != null) {
                    mPresenter.release();
                }
                finish();
            }
        });

        //下拉框选择
        menu_common.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select.setText(getResources().getString(R.string.feedback_common));

                imageView.setDrawingCacheEnabled(true);
                Bitmap bMap = Bitmap.createBitmap(imageView.getDrawingCache());
                Matrix matrix = new Matrix();
                matrix.postRotate(180);
                //   matrix.postScale(0.5f, 0.5f);
                int newWidth = bMap.getWidth();
                int newHeight = bMap.getHeight();
                Bitmap bMapRotate = Bitmap.createBitmap(bMap, 0, 0, newWidth, newHeight, matrix, true);

                //put rotated image in ImageView.
                imageView.setImageBitmap(bMapRotate);

                imageView.setDrawingCacheEnabled(false);
                showPopupWindow(v);
            }
        });

        mIvCommonActionBarMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String detailString = mContainer.getText().toString().trim();
                //	String mailString = mEmail.getText().toString().trim();
                String selectItem = select.getText().toString();
                if (detailString.equals("")) {
                    Toast.makeText(FeedbackActivity.this, getString(R.string.feedback_no_contain), Toast.LENGTH_SHORT).show();
                    return;
                }
                sendFeedBack(detailString, selectItem);
                finish();
            }
        });


        mContainer.setHint(R.string.feedback_container_hint);
        /* 自动弹出键盘 */
        mContainer.setFocusable(true);
        mContainer.setFocusableInTouchMode(true);
        mContainer.requestFocus();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) mContainer
                        .getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                inputManager.showSoftInput(mContainer, 0);
            }
        }, 100);
        notice.setText(R.string.feedback_notice);
        mPresenter.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mPresenter != null) {
            mPresenter.release();
        }
    }

    @Override
    protected void onDestroy() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        //InputMethodManager 释放当前Activity
        AppUtils.fixInputMethodManagerLeak(this);
        super.onDestroy();
    }

    /**
     * 获得pop window
     */
    private void showPopupWindow(View view) {

        // 一个自定义的布局，作为显示的内容
        View contentView = getLayoutInflater().inflate(R.layout.pop_feedback, null);
        suggestion = (TextView) contentView.findViewById(R.id.setting_feedback_suggestion);
        problem = (TextView) contentView.findViewById(R.id.setting_feedback_problem);
        forceInstall = (TextView) contentView.findViewById(R.id.setting_feedback_force_install);


        final PopupWindow popupWindow = new PopupWindow(contentView,
                menu_common.getWidth() - 2, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                imageView.setImageDrawable(APIUtil.getDrawable(FeedbackActivity.this, R.drawable.ic_arrow_07));
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(APIUtil.getDrawable(FeedbackActivity.this, R.color.white));

        // 设置好参数之后再show
        popupWindow.showAsDropDown(view, 1, -5);

        suggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select.setText(suggestion.getText().toString());
                imageView.setImageDrawable(APIUtil.getDrawable(FeedbackActivity.this, R.drawable.ic_arrow_07));
                popupWindow.dismiss();
            }
        });
        problem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select.setText(problem.getText().toString());
                imageView.setImageDrawable(APIUtil.getDrawable(FeedbackActivity.this, R.drawable.ic_arrow_07));
                popupWindow.dismiss();
            }
        });
        forceInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select.setText(forceInstall.getText().toString());
                imageView.setImageDrawable(APIUtil.getDrawable(FeedbackActivity.this, R.drawable.ic_arrow_07));
                popupWindow.dismiss();
            }
        });
    }

    private void sendFeedBack(final String detail, final String title) {
        mPresenter.sendFeedBack(detail, title);
    }

    private void showToast(int id) {
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showCheckNetWorkTip() {
        showToast(R.string.feedback_check_network);
    }

    @Override
    public void showNoEmailTip() {
        Toast.makeText(FeedbackActivity.this, getResources().getString(R.string.feedback_no_email), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setContainerNull() {
        mContainer.setText(null);
    }

    @Override
    public void dismissWarnLayout() {
        if (mWarnTipLayout != null) {
            mWarnTipLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 上传统计
     * */
    private void uploadStatistic() {
        StatisticsTools.upload103InfoPrivate();
    }
}
