package com.jb.filemanager.function.zipfile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.function.zipfile.adapter.ZipInnerFilesAdapter;
import com.jb.filemanager.function.zipfile.bean.ZipPreviewFileBean;
import com.jb.filemanager.function.zipfile.dialog.ExtractFileDialog;
import com.jb.filemanager.function.zipfile.presenter.ZipFilePreviewContract;
import com.jb.filemanager.function.zipfile.presenter.ZipFilePreviewPresenter;
import com.jb.filemanager.function.zipfile.view.BreadcrumbNavigation;

import java.util.List;

/**
 * Created by xiaoyu on 2017/6/30 14:34.
 * <p>
 * 能进入到该预览界面的压缩文件
 * <ol>
 * <li>zip</li>
 * <li>加密zip</li>
 * <li>rar</li>
 * </ol>
 * </p>
 */

public class ZipFilePreviewActivity extends BaseActivity implements
        BreadcrumbNavigation.OnBreadcrumbClickListener,
        AdapterView.OnItemClickListener,
        ZipFilePreviewContract.View, View.OnClickListener {

    public static final String EXTRA_FILE_PATH = "extra_file_path";
    public static final String EXTRA_PASSWORD = "extra_password"; // 保证传入的参数非空,或不传入

    private BreadcrumbNavigation mNavigation;
    private ListView mListView;
    private ZipInnerFilesAdapter mAdapter;
    private ProgressDialog mProgressDialog;

    private ZipFilePreviewPresenter mPresenter = new ZipFilePreviewPresenter(this);
    private TextView mBtnExtract;
    private ExtractFileDialog mExtractFileDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zip_file_preview);

        mBtnExtract = (TextView) findViewById(R.id.zip_pre_btn_extract);
        mBtnExtract.setOnClickListener(this);

        mNavigation = (BreadcrumbNavigation) findViewById(R.id.navigation);
        mNavigation.setOnBreadcrumbClickListener(this);
        mNavigation.addRootItem("");

        mListView = (ListView) findViewById(R.id.zip_pre_lv);
        mListView.setOnItemClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {
            mPresenter.onCreate(intent.getStringExtra(EXTRA_FILE_PATH),
                    intent.getStringExtra(EXTRA_PASSWORD));
        } else {
            finish();
        }
    }

    @Override
    public void updateListData(List<ZipPreviewFileBean> data) {
        if (mAdapter == null) {
            mAdapter = new ZipInnerFilesAdapter(data);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mPresenter.onProgressDialogCancel();
                }
            });
        }
        mProgressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void navigationBackward(boolean isEmpty) {
        if (!isEmpty) {
            mNavigation.back();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void navigationForward(String path) {
        mNavigation.addItem(path);
    }

    @Override
    public void showToast(String toast) {
        if (!TextUtils.isEmpty(toast)) {
            Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateExtractDialog(String currentPath) {
        if (mExtractFileDialog == null) {
            mExtractFileDialog = new ExtractFileDialog(this);
            mExtractFileDialog.setCanceledOnTouchOutside(false);
            mExtractFileDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mPresenter.onExtractDialogCancel();
                }
            });
        }
        mExtractFileDialog.updatePath(currentPath);
        if (!mExtractFileDialog.isShowing()) {
            mExtractFileDialog.show();
        }
    }

    @Override
    public void onExtractFilesAccomplish() {
        if (mExtractFileDialog != null) {
            mExtractFileDialog.dismiss();
        }
    }

    @Override
    public void onBreadcrumbClick(BreadcrumbNavigation.BreadcrumbItem item, String path) {
        mPresenter.onBreadcrumbClick(path);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPresenter.onListItemClick(mAdapter.getItem(position));
    }

    @Override
    public void onBackPressed() {
        mPresenter.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.zip_pre_btn_extract:
                mPresenter.onExtractFiles();
                break;
        }
    }

    /**
     * 浏览压缩包文件
     *
     * @param ctx      c
     * @param path     文件路径
     * @param password 密码
     */
    public static void browserFile(Context ctx, String path, String password) {
        Intent intent = new Intent(ctx, ZipFilePreviewActivity.class);
        if (!(ctx instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(EXTRA_FILE_PATH, path);
        intent.putExtra(EXTRA_PASSWORD, password);
        try {
            ctx.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
