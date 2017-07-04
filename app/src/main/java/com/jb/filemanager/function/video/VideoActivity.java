package com.jb.filemanager.function.video;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.function.musics.GroupList;
import com.jb.filemanager.function.musics.MusicInfo;

public class VideoActivity extends AppCompatActivity implements VideoContract.View,
        View.OnClickListener{
    private VideoContract.Presenter mPresenter;
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
    }

    @Override
    public void showVideoList(GroupList<String, MusicInfo> mMusicMaps) {

    }

    @Override
    public void onClick(View v) {

    }
}
