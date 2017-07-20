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

/**
 * Created by bill wang on 2017/7/19.
 *
 */

public class FileBrowserActivity extends BaseActivity {

    private static final String PARAM_PATH = "param_path";

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
                    finish();
                }
            });
            tvTitle.setText(R.string.file_browser_title);
        }

        String path = "";
        Intent intent = getIntent();
        if (intent != null) {
            path = intent.getStringExtra(PARAM_PATH);
        }

        Bundle fragmentParam = new Bundle();
        fragmentParam.putString(StorageFragment.PARAM_PATH, path);

        //设置默认视图
        StorageFragment fragment = new StorageFragment();
        fragment.setArguments(fragmentParam);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_file_browser_content, fragment);
        transaction.commit();
    }

    /**
     * 展示搜索
     * */
    public static void startBrowser(Context context, String targetPath) {
        Intent intent = new Intent(context, FileBrowserActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(PARAM_PATH, targetPath);
        context.startActivity(intent);
    }
}
