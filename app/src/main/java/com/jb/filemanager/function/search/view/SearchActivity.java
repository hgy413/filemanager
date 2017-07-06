package com.jb.filemanager.function.search.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.jb.filemanager.function.image.app.BaseFragmentActivity;

/**
 * Created by nieyh on 17-7-5.
 * 搜索视图 <br/>
 * 如果需要搜索的话 请跳转到这个activity 其他的就不用你管了
 * {@link #showSearchResult(Context)}
 */

public class SearchActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置默认视图
        setDefaultFragment(new SearchFragment());
    }

    /**
     * 展示搜索
     * */
    public static void showSearchResult(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        //去掉动画
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }
}
