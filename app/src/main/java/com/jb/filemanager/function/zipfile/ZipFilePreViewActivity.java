package com.jb.filemanager.function.zipfile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.function.zipfile.bean.ZipPreviewFileBean;
import com.jb.filemanager.function.zipfile.view.BreadcrumbNavigation;
import com.jb.filemanager.util.ConvertUtils;
import com.jb.filemanager.util.TimeUtil;

import java.util.List;
import java.util.Stack;

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

public class ZipFilePreViewActivity extends BaseActivity implements
        BreadcrumbNavigation.OnBreadcrumbClickListener
        , LoadZipInnerFilesListener, AdapterView.OnItemClickListener {

    public static final String EXTRA_FILE_PATH = "extra_file_path";
    public static final String EXTRA_PASSWORD = "extra_password"; // 保证传入的参数非空,或不传入

    private BreadcrumbNavigation mNavigation;
    private String mZipFilePath;
    private String mRootDir = "";
    private String mPassword;
    private ListView mListView;
    private List<ZipPreviewFileBean> mListData;
    private ZipInnerFilesAdapter mAdapter;
    /**
     * 记录路径堆栈，以便后退
     */
    private Stack<String> mPathStack = new Stack<>();
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zip_file_preview);

        mNavigation = (BreadcrumbNavigation) findViewById(R.id.navigation);
        mNavigation.setOnBreadcrumbClickListener(this);
        mNavigation.addRootItem(mRootDir);

        mListView = (ListView) findViewById(R.id.zip_pre_lv);
        mListView.setOnItemClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {
            mZipFilePath = intent.getStringExtra(EXTRA_FILE_PATH);
            mPassword = intent.getStringExtra(EXTRA_PASSWORD);

            listFiles();
        } else {
            finish();
        }
    }

    /**
     * 加载文件内容列表
     */
    private void listFiles() {
        LoadZipInnerFilesTask task = new LoadZipInnerFilesTask();
        task.setListener(this);
        task.execute(mZipFilePath, mRootDir, mPassword);
    }

    /**
     * 点击文件夹进入
     *
     * @param fi info
     */
    private void forward(ZipPreviewFileBean fi) {
        // 将当前路径进栈
        mPathStack.push(mRootDir);
        mRootDir = fi.getFullPath();
        mNavigation.addItem(mRootDir);
        listFiles();
    }

    /**
     * 后退
     */
    private void backward() {
        if (!mPathStack.isEmpty()) {
            // 路径出栈
            mRootDir = mPathStack.pop();
            mNavigation.back();
            listFiles();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 打开压缩包文件错误
     */
    private void onLoadError() {
        Log.e("error", "打开文件失败");
    }

    /**
     * 更新列表数据
     *
     * @param data d
     */
    private void updateListData(List<ZipPreviewFileBean> data) {
        if (mAdapter == null) {
            mListData = data;
            mAdapter = new ZipInnerFilesAdapter(mListData);
            mListView.setAdapter(mAdapter);
        } else {
            mListData.clear();
            mListData.addAll(data);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    // start -- 面包屑导航接口
    @Override
    public void onBreadcrumbClick(BreadcrumbNavigation.BreadcrumbItem item, String path) {
        if (!mRootDir.equals(path)) {
            // 移除当前路径以及后面的路径堆栈
            int index = mPathStack.indexOf(path);
            if (index != -1) {
                int size = mPathStack.size();
                for (int i = size - 1; i >= index; i--) {
                    mPathStack.remove(i);
                }
                mRootDir = path;
                listFiles();
            }
        }
    }
    // end -- 面包屑导航接口

    // start -- item点击接口
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ZipPreviewFileBean item = mAdapter.getItem(position);
        if (item.isDirectory()) {
            forward(item);
        } else {
//            openFile(fi);
            Toast.makeText(mActivity, "click file", Toast.LENGTH_SHORT).show();
        }
    }
    // end -- item点击接口

    // start -- 加载文件内容任务接口
    @Override
    public void onPreLoad() {
        Log.e("task", "开始加载");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showProgressDialog();
            }
        });
    }

    @Override
    public void onLoading(int value) {

    }

    @Override
    public void onPosLoad(List<ZipPreviewFileBean> result) {
        if (result != null) {
            updateListData(result);
            hideProgressDialog();
        } else {
            onLoadError();
        }
        Log.e("task", "加载完成");
    }

    @Override
    public void onCanceled() {
        Log.e("task", "任务取消");
    }
    // end -- 加载文件内容任务接口

    @Override
    public void onBackPressed() {
        backward();
    }
    /**
     * Adapter
     */
    private class ZipInnerFilesAdapter extends BaseAdapter {

        private List<ZipPreviewFileBean> mData;

        ZipInnerFilesAdapter(List<ZipPreviewFileBean> data) {
            mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public ZipPreviewFileBean getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(parent.getContext().getApplicationContext(), R.layout.item_zip_pre, null);
                holder.icon = (ImageView) convertView.findViewById(R.id.item_zip_pre_icon);
                holder.name = (TextView) convertView.findViewById(R.id.item_zip_pre_name);
                holder.size = (TextView) convertView.findViewById(R.id.item_zip_pre_size);
                holder.date = (TextView) convertView.findViewById(R.id.item_zip_pre_date);
                holder.checkbox = (ImageView) convertView.findViewById(R.id.item_zip_pre_checkbox);
                convertView.setTag(R.layout.item_zip_pre, holder);
            } else {
                holder = (ViewHolder) convertView.getTag(R.layout.item_zip_pre);
            }
            ZipPreviewFileBean item = getItem(position);
            holder.icon.setImageResource(item.isDirectory() ? R.drawable.choose_all : R.drawable.choose_none);
            holder.name.setText(item.getFileName());
            holder.size.setText(ConvertUtils.formatFileSize(item.getSize()));
            holder.date.setText(TimeUtil.getTime(item.getLastModifyTime()));
            holder.checkbox.setImageResource(R.drawable.choose_part);
            return convertView;
        }
    }

    private class ViewHolder {
        public ImageView icon;
        public TextView name;
        public TextView size;
        public TextView date;
        public ImageView checkbox;
    }

    /**
     * 浏览压缩包文件
     *
     * @param ctx      c
     * @param path     文件路径
     * @param password 密码
     */
    public static void browserFile(Context ctx, String path, String password) {
        Intent intent = new Intent(ctx, ZipFilePreViewActivity.class);
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
