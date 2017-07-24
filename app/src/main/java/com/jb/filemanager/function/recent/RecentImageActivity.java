package com.jb.filemanager.function.recent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.function.image.ImageDetailFragment;
import com.jb.filemanager.function.image.modle.ImageModle;

import java.util.ArrayList;

/**
 * Created by xiaoyu on 2017/7/20 14:51.
 */

public class RecentImageActivity extends BaseActivity {

    public static final String EXTRA_DATA_LIST = "extra_data_list";
    public static final String EXTRA_CURR_POSITION = "extra_curr_position";
    public static final String EXTRA_BUNDLE_DATA = "extra_bundle_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_image_view);

        Bundle bundle = getIntent().getBundleExtra(EXTRA_BUNDLE_DATA);
        ArrayList<ImageModle> imageModleList = bundle.getParcelableArrayList(EXTRA_DATA_LIST);
        int currentPos = bundle.getInt(EXTRA_CURR_POSITION);
        // R.id.recent_image : Fragment容器
        ImageDetailFragment imageDetailFragment = new ImageDetailFragment();
        imageDetailFragment.setExtras(imageModleList, currentPos);
        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
        tr.add(R.id.recent_image, imageDetailFragment, ImageDetailFragment.class.getSimpleName());
        tr.commitAllowingStateLoss();
    }

    public static void startView(Context context, ArrayList<ImageModle> imageModleList, int pos) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(EXTRA_DATA_LIST, imageModleList);
        bundle.putInt(EXTRA_CURR_POSITION, pos);
        Intent intent = new Intent(context, RecentImageActivity.class);
        intent.putExtra(EXTRA_BUNDLE_DATA, bundle);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }
}
