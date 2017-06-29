package com.jb.filemanager.function.trashignore.activity;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.function.trashignore.presenter.TrashIgnorePresenter;
import com.jb.filemanager.function.trashignore.view.TrashIgnoreViewLayer;
import com.jb.filemanager.util.imageloader.IconLoader;


/**
 * Created by xiaoyu on 2017/2/28 11:30.
 */

public class TrashIgnoreActivity extends BaseActivity {

    private TrashIgnorePresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash_ignore);
        IconLoader.ensureInitSingleton(this);
        IconLoader.getInstance().bindServicer(this);
        FrameLayout flRootLayer = (FrameLayout) findViewById(R.id.trash_ignore_root_layer);
        TrashIgnoreViewLayer viewLayer = new TrashIgnoreViewLayer(this);
        flRootLayer.addView(viewLayer.getView());
        mPresenter = new TrashIgnorePresenter(viewLayer, getApplicationContext());
        mPresenter.onEnterActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IconLoader.getInstance().unbindServicer(this);
        mPresenter.onExitActivity();
    }

    @Override
    public void finish() {
        super.finish();
    }
}
