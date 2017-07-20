package com.jb.filemanager.function.txtpreview;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.function.zipfile.dialog.ExtractErrorDialog;
import com.jb.filemanager.util.AppUtils;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/19 19:10
 */

public class TxtPreviewActivity extends BaseActivity {

    public static final String TARGET_DOC_PATH = "target_doc_path";
    private RelativeLayout mRlTitle;
    private TextView mTvCommonActionBarTitle;
    private ImageView mIvCommonActionBarMore;
    private View mVTitleShadow;
    private TextView mTvTxtContent;
    private String mDocPath;
    private ExtractErrorDialog mErrorDialog;
    private TxtDecodeManager mTxtDecodeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_txt_preview);
        initView();
        initData();
        initClick();
    }

    private void initView() {
        mRlTitle = (RelativeLayout) findViewById(R.id.rl_title);
        mTvCommonActionBarTitle = (TextView) findViewById(R.id.tv_common_action_bar_title);
        mIvCommonActionBarMore = (ImageView) findViewById(R.id.iv_common_action_bar_more);
        mVTitleShadow = (View) findViewById(R.id.v_title_shadow);
        mTvTxtContent = (TextView) findViewById(R.id.tv_txt_content);
    }

    public void initData() {
        Intent intent = getIntent();
        mDocPath = intent.getStringExtra(TARGET_DOC_PATH);
        mTxtDecodeManager = new TxtDecodeManager();
        mTxtDecodeManager.setTxtLoadListener(new TxtDecodeManager.OnTxtLoadListener() {
            @Override
            public void onLoadStart() {

                AppUtils.showToast("这里有个你看不到的加载动画");

            }

            @Override
            public void onLoadComplete(final String result) {
                AppUtils.showToast("Duang Duang Duang 加载完成");
                mTvTxtContent.setText(result);
            }

            @Override
            public void onLoadError(String msg) {

                mErrorDialog = new ExtractErrorDialog(TxtPreviewActivity.this, "nothing");
                mErrorDialog.setTvDialogTitle("error");
                mErrorDialog.setTvFileName("");
                mErrorDialog.setTvRetryTxt("something wrong ,plz exit");
                mErrorDialog.setOkBtn("exit");
                mErrorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
//                        finish();
                    }
                });
                mErrorDialog.show();

            }
        });
        mTxtDecodeManager.LoadTxtPath(mDocPath);
    }

    private void initClick() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mErrorDialog != null) {
            mErrorDialog.dismiss();
        }
        mTxtDecodeManager.cancelTask();
    }
}
