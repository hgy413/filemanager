package com.jb.filemanager.function.video;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.function.musics.GroupList;
import com.jb.filemanager.function.musics.MusicInfo;
import com.jb.filemanager.ui.widget.BottomOperateBar;

public class VideoActivity extends AppCompatActivity implements VideoContract.View,
        View.OnClickListener{
    private VideoContract.Presenter mPresenter;
    private BottomOperateBar mBottomOperateBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        initView();
    }

    protected void initView() {
        TextView back = (TextView) findViewById(R.id.tv_common_action_bar_with_search_title);
        if (back != null) {
            back.getPaint().setAntiAlias(true);
            back.setText(R.string.video_title);
            back.setOnClickListener(this);
        }
        mBottomOperateBar = (BottomOperateBar) findViewById(R.id.bottom_operate_bar_container);
        mBottomOperateBar.setClickListener(this);
    }

    @Override
    public void showVideoList(GroupList<String, MusicInfo> mMusicMaps) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_common_action_bar_with_search_title:
                onBackPressed();
                break;
            case R.id.iv_common_action_bar_with_search_search:
                // TODO
                break;
            case R.id.tv_common_operate_bar_cut:
                if (mPresenter != null) {
                    mPresenter.onClickOperateCutButton();
                }
                break;
            case R.id.tv_common_operate_bar_copy:
                if (mPresenter != null) {
                    mPresenter.onClickOperateCopyButton();
                }
                break;
            case R.id.tv_common_operate_bar_delete:
                if (mPresenter != null) {
                    mPresenter.onClickOperateDeleteButton();
                }
                break;
            case R.id.tv_common_operate_bar_more:
                if (mPresenter != null) {
                    mPresenter.onClickOperateMoreButton();
                }
                break;
            default:
                break;
        }
    }
}
