package com.jb.filemanager.function.filebrowser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jb.filemanager.Const;
import com.jb.filemanager.function.image.app.BaseFragmentActivity;
import com.jb.filemanager.function.search.view.SearchActivity;
import com.jb.filemanager.function.search.view.SearchFragment;
import com.jb.filemanager.home.fragment.storage.StorageFragment;

/**
 * Created by bill wang on 2017/7/19.
 *
 */

public class FileBrowserActivity extends BaseFragmentActivity {

    private static final String PARAM_PATH = "param_path";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        setDefaultFragment(fragment);
    }

    /**
     * 展示搜索
     * */
    public static void startBrowser(Context context, String targetPath) {
        Intent intent = new Intent(context, FileBrowserActivity.class);
        intent.putExtra(PARAM_PATH, targetPath);
        context.startActivity(intent);
    }
}
