package com.jb.filemanager.function.txtpreview;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.zipfile.dialog.ExtractErrorDialog;
import com.jb.filemanager.util.Logger;

import java.util.ArrayList;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/19 19:10
 */

public class TxtPreviewActivity extends BaseActivity {

    public static final String TARGET_DOC_PATH = "target_doc_path";
    private TextView mTvCommonActionBarTitle;
    private ImageView mIvCommonActionBarSearch;
    private RecyclerView mRvTxtPreview;
    private String mDocPath;
    private ExtractErrorDialog mErrorDialog;
    private BookPageFactory mTxtDecodeManager;
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
        mTvCommonActionBarTitle = (TextView) findViewById(R.id.tv_common_action_bar_title);
        mIvCommonActionBarSearch = (ImageView) findViewById(R.id.iv_common_action_bar_search);
        mRvTxtPreview = (RecyclerView) findViewById(R.id.rv_txt_preview);

        mIvCommonActionBarSearch.setVisibility(View.GONE);
        mTxtData = new ArrayList<>();
        mRvTxtPreview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mPreviewAdapter = new TxtPreviewAdapter(mTxtData);
        mRvTxtPreview.setAdapter(mPreviewAdapter);
    }

    public void initData() {
        Intent intent = getIntent();
        mDocPath = intent.getStringExtra(TARGET_DOC_PATH);
        mTxtDecodeManager = new BookPageFactory(13);
        mTxtDecodeManager.setTxtLoadListener(new BookPageFactory.OnTxtLoadListener() {
            @Override
            public void onLoadStart() {
//                AppUtils.showToast("这里有个你看不到的加载动画");
                mRvTxtPreview.setEnabled(false);
            }

            @Override
            public void onLoadComplete() {
//                AppUtils.showToast("Duang Duang Duang 加载完成了");
                TheApplication.postRunOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRvTxtPreview.setEnabled(true);
                    }
                });
            }

            @Override
            public void onLoadError(String msg) {

                mErrorDialog = new ExtractErrorDialog(TxtPreviewActivity.this, "nothing");
                mErrorDialog.setTvDialogTitle(getString(R.string.txt_preview_error));
                mErrorDialog.setTvFileName("");
                mErrorDialog.setTvRetryTxt(getString(R.string.txt_preview_wrong_msg));
                mErrorDialog.setOkBtn(getString(R.string.txt_preview_exit));
                mErrorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
//                        finish();
                    }
                });
                mErrorDialog.show();

            }

            @Override
            public void onLoadPart(final ArrayList<String> part) {
                TheApplication.postRunOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (part.size() == 0) {
                            return;
                        }
                        mTxtData.addAll(part);
                        mPreviewAdapter.notifyDataSetChanged();
                    }
                });

            }
        });
        mTxtDecodeManager.LoadTxtPath(mDocPath);
        mRvTxtPreview.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 当不滚动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的ItemPosition
                    int lastVisibleItem = manager.findLastVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();

                    Logger.d(BookPageFactory.TAG, "判断位置  目前位置: " + lastVisibleItem + " 总数目:" + totalItemCount);
                    // 判断是否滚动到底部，并且是最后滚动
                    if (lastVisibleItem == (totalItemCount - 1)) {
                        Logger.d(BookPageFactory.TAG, "继续加载");
                        //加载更多功能的代码
                        mTxtDecodeManager.LoadTxtPath(mDocPath);
                    }
                }
            }
        });
    }

    private void initClick() {
        mTvCommonActionBarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mErrorDialog != null) {
            mErrorDialog.dismiss();
        }
        mTxtDecodeManager.cancelTask();
        mTxtDecodeManager.isStillRead = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
