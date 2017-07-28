package com.jb.filemanager.function.filebrowser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.home.fragment.storage.StorageFragment;
import com.jb.filemanager.manager.file.FileManager;

/**
 * Created by bill wang on 2017/7/19.
 *
 */

public class FileBrowserActivity extends BaseActivity {

    private static final String PARAM_PATH = "param_path";
    private static final String PARAM_REQUEST = "param_request";

    public static final int REQUEST_CODE_BROWSER = 100;
    public static final int REQUEST_CODE_PASTE = 101;

    public static final String RETURN_PARAM_IS_PASTE = "is_paste";

    private int mRequest;
    private boolean mIsPaste = false;
    private boolean mHandleRequest = false;

    /**
     * 展示浏览器--浏览
     * */
    public static void startBrowserForView(Context context, String targetPath) {
        Intent intent = new Intent(context, FileBrowserActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(PARAM_PATH, targetPath);
        intent.putExtra(PARAM_REQUEST, REQUEST_CODE_BROWSER);
        context.startActivity(intent);
    }

    /**
     * 展示浏览器--粘贴
     * */
    public static void startBrowserForPaste(Activity activity, String targetPath) {
        Intent intent = new Intent(activity, FileBrowserActivity.class);
        intent.putExtra(PARAM_PATH, targetPath);
        intent.putExtra(PARAM_REQUEST, REQUEST_CODE_PASTE);
        activity.startActivityForResult(intent, REQUEST_CODE_PASTE);
    }

    /**
     * 展示浏览器--粘贴
     * */
    public static Intent getBrowserForPasteIntent(Activity activity, String targetPath) {
        Intent intent = new Intent(activity, FileBrowserActivity.class);
        intent.putExtra(PARAM_PATH, targetPath);
        intent.putExtra(PARAM_REQUEST, REQUEST_CODE_PASTE);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_file_browser);

        TextView tvTitle = (TextView) findViewById(R.id.tv_common_action_bar_title);
        if (tvTitle != null) {
            tvTitle.getPaint().setAntiAlias(true);
            tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            tvTitle.setText(R.string.file_browser_title);
        }

        String path = "";
        Intent intent = getIntent();
        if (intent != null) {
            path = intent.getStringExtra(PARAM_PATH);
            mRequest = intent.getIntExtra(PARAM_REQUEST, REQUEST_CODE_BROWSER);
        }

        Bundle fragmentParam = new Bundle();
        fragmentParam.putString(StorageFragment.PARAM_PATH, path);

        //设置默认视图
        StorageFragment fragment = new StorageFragment();
        fragment.setArguments(fragmentParam);
        fragment.setListener(new StorageFragment.Listener() {
            @Override
            public void onClickPaste() {
                if (!mHandleRequest) {
                    mIsPaste = true;
                    mHandleRequest = true;
                }
            }

            @Override
            public void onClickCancel() {
                if (!mHandleRequest) {
                    mIsPaste = false;
                    mHandleRequest = true;
                }
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_file_browser_content, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        FileManager.getInstance().clearCutFiles();
        FileManager.getInstance().clearCopyFiles();

        if (mRequest == REQUEST_CODE_PASTE) {
            Intent returnData = new Intent();
            returnData.putExtra(RETURN_PARAM_IS_PASTE, mIsPaste);
            setResult(RESULT_OK, returnData);
        }

        finish();
    }
}
