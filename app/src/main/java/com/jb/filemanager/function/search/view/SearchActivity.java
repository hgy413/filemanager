package com.jb.filemanager.function.search.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jb.filemanager.Const;
import com.jb.filemanager.function.image.app.BaseFragmentActivity;

/**
 * Created by nieyh on 17-7-5.
 * 搜索视图 <br/>
 * 如果需要搜索的话 请跳转到这个activity 其他的就不用你管了
 * {@link #showSearchResult(Context, int)}
 */

public class SearchActivity extends BaseFragmentActivity {

    private static final String PARAM_CATEGORY_TYPE = "param_category_type";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int categoryType = Const.CategoryType.CATEGORY_TYPE_ALL;
        Intent intent = getIntent();
        if (intent != null) {
            categoryType = intent.getIntExtra(PARAM_CATEGORY_TYPE, Const.CategoryType.CATEGORY_TYPE_ALL);
        }

        Bundle fragmentParam = new Bundle();
        fragmentParam.putInt(SearchFragment.PARAM_CATEGORY_TYPE, categoryType);

        //设置默认视图
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(fragmentParam);
        setDefaultFragment(fragment);
    }

    /**
     * 展示搜索
     * */
    public static void showSearchResult(Context context, int categoryType) {
        Intent intent = new Intent(context, SearchActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        //去掉动画
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        intent.putExtra(PARAM_CATEGORY_TYPE, categoryType);
        context.startActivity(intent);
    }
}
