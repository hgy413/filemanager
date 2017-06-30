package com.jb.filemanager.function.musics;

import android.os.Bundle;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;

/**
 * Created by bool on 17-6-30.
 */

public class MusicAcrivity extends BaseActivity implements MusicContract.View{
    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";
    private MusicContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        MusicSupport support = new MusicSupport();
        mPresenter = new MusicPresenter(this, support, new MusicsLoader(this, support),
                getSupportLoaderManager());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //outState.putSerializable(CURRENT_FILTERING_KEY, mPresenter.getFiltering());
        super.onSaveInstanceState(outState);
    }
}
