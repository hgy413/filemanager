package com.jb.filemanager.function.txtpreview;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.function.zipfile.dialog.ExtractErrorDialog;
import com.jb.filemanager.util.AppUtils;

import java.util.ArrayList;

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
    private RecyclerView mRvTxtPreview;
    private String mDocPath;
    private ExtractErrorDialog mErrorDialog;
    private TxtDecodeManager mTxtDecodeManager;
    private ArrayList<String> mTxtData;
    private TxtPreviewAdapter mPreviewAdapter;

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
        mRvTxtPreview = (RecyclerView) findViewById(R.id.rv_txt_preview);

        mTxtData = new ArrayList<>();
        mRvTxtPreview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

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
            public void onLoadComplete(final ArrayList<String> result) {
                AppUtils.showToast("Duang Duang Duang 加载完成了");
                /*StringBuilder stringBuilder = new StringBuilder();
                if (result != null) {
                    for (String txt : result) {
                        stringBuilder.append(txt);
                    }
                    mTvTxtContent.setText(stringBuilder.toString());
                }*/

                mTxtData.clear();
                mTxtData.addAll(result);
                mPreviewAdapter = new TxtPreviewAdapter(mTxtData);
                mRvTxtPreview.setAdapter(mPreviewAdapter);
//                mPreviewAdapter.notify();
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
    }
}
