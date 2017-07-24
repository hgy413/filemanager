package com.jb.filemanager.function.feedback;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.function.applock.dialog.ListDialog;
import com.jb.filemanager.statistics.StatisticsTools;
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
    private TextView select; //下拉框文本
    private TextView mWarnTipContent;
    private View mWarnTipLayout;
    private TextView mWarnTipReport;
    private TextView mWarnTipNo;
    private TextView mTvCommonActionBarTitle;
    private ImageView mIvCommonActionBarMore;
    private View mVirusInstallLl;
    private View mVirusInstallTv;
    private View mVirusInstallArrow;
    private View mTrickingAdLl;
    private View mTrickingAdTv;
    private View mTrickingAdArrow;

    private FeedbackContract.Presenter mPresenter;
    private Timer mTimer;
    private ListDialog mQuestionListDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        init();
    }


    private void init() {
        mPresenter = new FeedbackPresenter(this, new FeedbackSupport());

        mTvCommonActionBarTitle = (TextView) findViewById(R.id.tv_common_action_bar_title);
        mIvCommonActionBarMore = (ImageView) findViewById(R.id.iv_common_action_bar_send);

        mContainer = (EditText) findViewById(R.id.container_setting_feedback);
        select = (TextView) findViewById(R.id.setting_feedback_menu_select);
        TextView notice = (TextView) findViewById(R.id.notice_setting_feedback);
        imageView = (ImageView) findViewById(R.id.menu_imageview);
        menu_common = (LinearLayout) findViewById(R.id.setting_feedback_menu);

        mWarnTipContent = (TextView) findViewById(R.id.activity_menu_feedback_tip_content);
        mWarnTipLayout = findViewById(R.id.activity_menu_feedback_tip_layout);
        mWarnTipReport = (TextView) findViewById(R.id.activity_menu_feedback_send);
        mVirusInstallLl = findViewById(R.id.activity_menu_feedback_virus_install_ll);
        mTrickingAdLl = findViewById(R.id.activity_menu_feedback_tricking_ad_ll);
        mVirusInstallTv = findViewById(R.id.activity_menu_feedback_virus_install_tip);
        mTrickingAdTv = findViewById(R.id.activity_menu_feedback_tricking_ad_tip);
        mTrickingAdArrow = findViewById(R.id.activity_menu_feedback_tricking_ad_arrow);
        mVirusInstallArrow = findViewById(R.id.activity_menu_feedback_virus_install_arrow);
        mWarnTipNo = (TextView) findViewById(R.id.activity_menu_feedback_close);
        mWarnTipContent.setText(getResources().getString(R.string.feedback_tip_content, getResources().getString(R.string.app_name)));
        mQuestionListDialog = new ListDialog(select, R.array.feedback_common_question_list);
        mQuestionListDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                imageView.setRotation(0);
            }
        });
        mWarnTipNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWarnTipLayout.setVisibility(View.GONE);
                if (mPresenter != null) {
                    mPresenter.clickTipSecondBtu();
                }
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
                showWarnTip2();
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
                imageView.setRotation(180);
                showPopupWindow(v);
            }
        });

        mIvCommonActionBarMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String detailString = mContainer.getText().toString().trim();
                String selectItem = select.getText().toString();
                if (detailString.equals("")) {
                    Toast.makeText(FeedbackActivity.this, getString(R.string.feedback_no_contain), Toast.LENGTH_SHORT).show();
                    return;
                }
                sendFeedBack(detailString, selectItem);
                finish();
            }
        });

        mIvCommonActionBarMore.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int mask = MotionEventCompat.getActionMasked(event);
                switch (mask) {
                    case MotionEvent.ACTION_DOWN:
                        mIvCommonActionBarMore.setAlpha(0.3f);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_OUTSIDE:
                    case MotionEvent.ACTION_UP:
                        mIvCommonActionBarMore.setAlpha(1f);
                        break;
                }
                return false;
            }
        });

        mVirusInstallLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVirusInstallTv.getVisibility() == View.VISIBLE) {
                    mVirusInstallTv.setVisibility(View.GONE);
                    mVirusInstallArrow.setRotation(180);
                } else {
                    mVirusInstallTv.setVisibility(View.VISIBLE);
                    mVirusInstallArrow.setRotation(0);
                }
            }
        });

        mTrickingAdLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTrickingAdTv.getVisibility() == View.VISIBLE) {
                    mTrickingAdTv.setVisibility(View.GONE);
                    mTrickingAdArrow.setRotation(180);
                } else {
                    mTrickingAdTv.setVisibility(View.VISIBLE);
                    mTrickingAdArrow.setRotation(0);
                }
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
        mQuestionListDialog.showUnderView(view);
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
    public void showWarnTip1() {
        mWarnTipContent.setText(getResources().getString(R.string.feedback_tip_content, getResources().getString(R.string.app_name)));
        mWarnTipReport.setVisibility(View.VISIBLE);
        mWarnTipReport.setText(R.string.feedback_tip_send_report);
        mWarnTipNo.setText(R.string.feedback_tip_no);
        String[] array = getResources().getStringArray(R.array.feedback_common_question_list);
        select.setText(array[0]);
    }

    @Override
    public void showWarnTip2() {
        mWarnTipContent.setText(getResources().getString(R.string.feedback_tip_content2));
        mWarnTipReport.setVisibility(View.GONE);
        mWarnTipNo.setText(R.string.common_ok);
        String[] array = getResources().getStringArray(R.array.feedback_common_question_list);
        select.setText(array[3]);
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
        String[] array = getResources().getStringArray(R.array.feedback_common_question_list);
        select.setText(array[3]);
    }

    /**
     * 上传统计
     * */
    private void uploadStatistic() {
        StatisticsTools.upload103InfoPrivate();
    }
}
