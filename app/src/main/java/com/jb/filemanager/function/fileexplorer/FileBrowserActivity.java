package com.jb.filemanager.function.fileexplorer;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.function.scanframe.bean.common.FileType;
import com.jb.filemanager.function.scanframe.clean.FileInfo;
import com.jb.filemanager.util.IntentUtil;
import com.jb.filemanager.util.file.FileSizeFormatter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Stack;

/**
 * <li>ListActivity的默认布局由一个位于屏幕中心的全屏列表构成 <li>
 * 布局中必须包含一个id为"@id/android:list"的ListView <li>
 * 若指定了一个id为"@id/android:empty"的view，
 * 当ListView中没有数据要显示时，这个view就会被显示，同时ListView会被隐藏
 *
 * @author lishen
 */
public class FileBrowserActivity extends ListActivity implements
        OnItemClickListener, BreadcrumbNavigation.OnBreadcrumbClickListener, CommonTitle.OnBackListener {

    /**
     * 附带的title参数 <li>类型：String
     */
    public static final String EXTRA_TITLE = "title";
    /**
     * 附带的文件夹地址参数 <li>类型：StringArray
     */
    public static final String EXTRA_DIRS = "extra_dirs";
    /**
     * 附带的高亮文件路径参数 <li>类型：String
     */
    public static final String EXTRA_FOCUS_FILE = "extra_focus_file";

    /**
     * 当传入多个路径时，默认的顶层目录
     */
    private static final String DEFAULT_ROOT_DIR = ".";

    /**
     * 文件浏览器模式-多文件夹浏览
     */
    private static final byte MODE_BROWSER = 0x01;
    /**
     * 文件浏览器模式-单个文件浏览
     */
    private static final byte MODE_FOCUS = 0x02;
    /**
     * 当前文件浏览器的模式
     */
    private int mMode = MODE_BROWSER;

    /**
     * 初始传入的地址列表
     */
    private String[] mBaseDirs;
    /**
     * 当前浏览地址
     */
    private String mRootDir;
    /**
     * 高亮的文件名
     */
    private String mFocusFile;
    /**
     * 高亮的文件位置
     */
    private int mFocusFileIdx;

    private ArrayList<FileInfo> mFileInfos = new ArrayList<FileInfo>();
    private FilesAdapter mFilesAdapter;

    private BreadcrumbNavigation mNavigation;

    private NewListItemDialog mDialog;

    /**
     * 记录路径堆栈，以便后退
     */
    private Stack<String> mPathStack = new Stack<String>();
    /**
     * 主题相关
     */
    private TextView mTvCommonActionBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ColorStatusBarUtil.transparentStatusBar(this);

        setContentView(R.layout.activity_filebrowser);
        mTvCommonActionBarTitle = (TextView) findViewById(R.id.tv_common_action_bar_title);
        mTvCommonActionBarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        CommonEmptyView empty = (CommonEmptyView) findViewById(android.R.id.empty);
        empty.setTips(R.string.clean_file_browser_no_files);
        mNavigation = (BreadcrumbNavigation) findViewById(R.id.navigation);
        mNavigation.setOnBreadcrumbClickListener(this);

        // 获取路径参数
        String filePath = null;
        Intent intent = getIntent();
        if (intent != null) {
//            mTitle.setTitleName(intent.getStringExtra(EXTRA_TITLE));
            mBaseDirs = intent.getStringArrayExtra(EXTRA_DIRS);
            if (mBaseDirs == null) {
                filePath = intent.getStringExtra(EXTRA_FOCUS_FILE);
            }
        }

        if (mBaseDirs != null || filePath != null) {
            init();
            if (mBaseDirs != null) {
                mMode = MODE_BROWSER;
                if (mBaseDirs.length > 1) {
                    mRootDir = DEFAULT_ROOT_DIR;
                } else {
                    mRootDir = mBaseDirs[0];
                }
            } else {
                mMode = MODE_FOCUS;
                final int index;
                if (filePath != null && !TextUtils.isEmpty(filePath)) {
                    try {
                        index = filePath.lastIndexOf("/");
                        mRootDir = filePath.substring(0, index);
                        mFocusFile = filePath.substring(index + 1);
                    } catch (Exception e) {
                        finish();
                    }
                }
            }
            mNavigation.addRootItem(mRootDir);
            listFiles();
        } else {
            finish();
        }
    }

    private void init() {
        mFilesAdapter = new FilesAdapter();
        setListAdapter(mFilesAdapter);
        final ListView listview = getListView();
        listview.setOnItemClickListener(this);
    }

    /**
     * 文件比较器
     */
    private Comparator<FileInfo> mCP = new Comparator<FileInfo>() {

        @Override
        public int compare(FileInfo l, FileInfo r) {
            boolean isLFile = l.isFile();
            boolean isRFile = r.isFile();
            if (isLFile != isRFile) {
                return isLFile ? 1 : -1;
            } else {
                String fileName_l = getString(l.mFileName, "").toLowerCase(
                        Locale.US);
                String fileName_r = getString(r.mFileName, "").toLowerCase(
                        Locale.US);
                return fileName_l.compareTo(fileName_r);
            }
        }

    };

    /**
     * 返回字符串，如果字符串为空则返回默认字符串
     *
     * @param src
     * @param fallback
     * @return
     */
    private String getString(String src, String fallback) {
        if (!TextUtils.isEmpty(src)) {
            return src;
        } else {
            return fallback;
        }
    }

    private void listFiles() {
        if (!DEFAULT_ROOT_DIR.equals(mRootDir)) {
            mFileInfos = FileEngine.listFiles(mRootDir);
        } else {
            mFileInfos.clear();
            for (String dir : mBaseDirs) {
                mFileInfos.add(FileEngine.buildFileInfo(new File(dir)));
            }
        }
        Collections.sort(mFileInfos, mCP);
        findFocusFile();
        mFilesAdapter.notifyDataSetChanged();
        if (mFocusFileIdx != -1) {
            getListView()
                    .setSelectionFromTop(Math.max(mFocusFileIdx - 1, 0), 0);
        }
    }

    /**
     * 定位高亮文件的位置
     */
    private void findFocusFile() {
        mFocusFileIdx = -1;
        if (mMode == MODE_FOCUS && mFocusFile != null) {
            for (int i = 0; i < mFileInfos.size(); i++) {
                FileInfo fi = mFileInfos.get(i);
                if (mFocusFile.equals(fi.mFileName)) {
                    mFocusFileIdx = i;
                    break;
                }
            }
        }
    }

    /**
     * adapter
     *
     * @author lishen
     */
    class FilesAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mFileInfos.size();
        }

        @Override
        public FileInfo getItem(int position) {
            return mFileInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FileItem item = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(
                        R.layout.filebrowser_file_item, parent, false);
                item = new FileItem(convertView);
                convertView.setTag(item);
            } else {
                item = (FileItem) convertView.getTag();
            }
            FileInfo fileInfo = getItem(position);
            item.mFileName.setText(fileInfo.mFileName);
            if (fileInfo.isFile()) {
                FileSizeFormatter.FileSize fz = FileSizeFormatter.formatFileSize(fileInfo.mSize);
                item.mFileTime.setText(String.format("%s %s", fz.toFullString(), fileInfo.mTime));
                item.mIcon.setImageResource(R.drawable.img_file);
            } else {
                item.mFileTime.setText(fileInfo.mTime);
                item.mIcon.setImageResource(R.drawable.img_folder);
            }
            convertView
                    .setBackgroundResource(R.drawable.common_list_item_white_selector);
            updateTextColorInFocusMode(item, position);
            return convertView;
        }

        /**
         * 高亮指定文件
         *
         * @param item
         * @param position
         */
        private void updateTextColorInFocusMode(FileItem item, int position) {
            if (mMode == MODE_FOCUS) {
                final Resources r = getResources();
                if (position == mFocusFileIdx) {
                    item.mFileName.setTextColor(Color.parseColor("#85c443"));
                    item.mFileTime.setTextColor(Color.parseColor("#85c443"));
                } else {
                    item.mFileName.setTextColor(Color.parseColor("#787878"));
                    item.mFileTime.setTextColor(Color.parseColor("#bebebe"));
                }
            }
        }


    }

    /**
     * viewholder
     */
    class FileItem {

        public ImageView mIcon;
        public TextView mFileName;
        public TextView mFileTime;

        public View mMask;

        FileItem(View item) {
            mIcon = (ImageView) item.findViewById(R.id.icon);
            mFileName = (TextView) item.findViewById(R.id.title);
            mFileTime = (TextView) item.findViewById(R.id.time);
            mMask = item.findViewById(R.id.mask);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        FileInfo fi = mFilesAdapter.getItem(position);
        if (fi.isFile()) {
            openFile(fi);
        } else {
            forward(fi);
        }
    }

    /**
     * 点击文件夹进入
     *
     * @param fi
     */
    private void forward(FileInfo fi) {
        // 将当前路径进栈
        mPathStack.push(mRootDir);
        mRootDir = fi.mPath;
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
     * 打开文件
     *
     * @param fi
     */
    private void openFile(FileInfo fi) {
        if (!IntentUtil.openFileWithIntent(this, fi)) {
            // ========= 统计：展示Open as对话框 ==============
//			StatisticsTools.uploadClickData(DET_OA_CLI);
            showOpenWithDialog(fi);
        }
    }

    /**
     * 显示打开方式对话框
     */
    private void showOpenWithDialog(final FileInfo fi) {
        if (mDialog == null) {
            mDialog = new NewListItemDialog(this, true);
        }
        if (!isFinishing()) {
            mDialog.setFileTypeClickListener(new NewListItemDialog.OnFileTypeClickListener() {
                @Override
                public void onTypeTextClick() {
                    IntentUtil.openFileWithIntent(FileBrowserActivity.this,
                            FileType.DOCUMENT, fi.mPath);
                }

                @Override
                public void onTypeAudioClick() {
                    IntentUtil.openFileWithIntent(FileBrowserActivity.this,
                            FileType.MUSIC, fi.mPath);
                }

                @Override
                public void onTypeVideoClick() {
                    IntentUtil.openFileWithIntent(FileBrowserActivity.this,
                            FileType.VIDEO, fi.mPath);
                }

                @Override
                public void onTypeImageClick() {
                    IntentUtil.openFileWithIntent(FileBrowserActivity.this,
                            FileType.IMAGE, fi.mPath);
                }
            });
            mDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        backward();
    }

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

    @Override
    public void onBackClick() {
        finish();
    }

    /**
     * 浏览文件夹
     *
     * @param ctx
     * @param title 标题
     * @param dirs  文件夹
     */
    public static void browserDirs(Context ctx, String title, String... dirs) {
        Intent intent = new Intent(ctx, FileBrowserActivity.class);
        if (!(ctx instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_DIRS, dirs);
        try {
            ctx.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 浏览文件
     *
     * @param ctx
     * @param title 标题
     * @param path  文件路径
     */
    public static void browserFile(Context ctx, String title, String path) {
        Intent intent = new Intent(ctx, FileBrowserActivity.class);
        if (!(ctx instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_FOCUS_FILE, path);
        try {
            ctx.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}