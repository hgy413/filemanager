package com.jb.filemanager.function.search.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.BaseFragment;
import com.jb.filemanager.R;
import com.jb.filemanager.function.filebrowser.FileBrowserActivity;
import com.jb.filemanager.function.fileexplorer.NewListItemDialog;
import com.jb.filemanager.function.scanframe.bean.common.FileType;
import com.jb.filemanager.function.search.modle.FileInfo;
import com.jb.filemanager.util.FileTypeUtil;
import com.jb.filemanager.util.FileUtil;
import com.jb.filemanager.util.IntentUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by nieyh on 17-7-5.
 *
 */

public class SearchResultFragment extends BaseFragment implements View.OnClickListener{
    //参数
    public static final String ARG = "arg";

    private SearchResultAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            boolean hasResult = false;
            ArrayList<FileInfo> fileInfoArrayList = bundle.getParcelableArrayList(ARG);
            if (fileInfoArrayList != null && fileInfoArrayList.size() > 0) {
                hasResult = true;
            }

            ImageView ivBack = (ImageView) view.findViewById(R.id.iv_common_action_bar_back);
            if (ivBack != null) {
                ivBack.setOnClickListener(this);
            }

            TextView tvTitle = (TextView) view.findViewById(R.id.tv_search_result_title);
            if (tvTitle != null) {
                tvTitle.getPaint().setAntiAlias(true);
                tvTitle.setText(getString(R.string.search_result_title, hasResult ? fileInfoArrayList.size() : 0));
            }

            RecyclerView resultView = (RecyclerView) view.findViewById(R.id.fragment_search_result_rv);
            if (resultView != null) {
                resultView.setVisibility(hasResult ? View.VISIBLE : View.GONE);
                if (hasResult) {
                    mAdapter = new SearchResultAdapter(fileInfoArrayList);
                    mAdapter.setOnItemClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FileInfo fileInfo = (FileInfo)v.getTag();
                            File clickedFile = new File(fileInfo.mFileAbsolutePath);
                            if (clickedFile.exists()) {
                                if (clickedFile.isDirectory()) {
                                    FileBrowserActivity.startBrowser(getActivity(), fileInfo.mFileAbsolutePath);
                                } else {
                                    FileUtil.openFile(getActivity(), clickedFile);
                                }
                            }
                        }
                    });
                    resultView.setLayoutManager(new LinearLayoutManager(getContext()));
                    resultView.setAdapter(mAdapter);
                }
            }

            TextView emptyTips = (TextView) view.findViewById(R.id.tv_search_result_empty_tips);
            if (emptyTips != null) {
                emptyTips.getPaint().setAntiAlias(true);
                emptyTips.setVisibility(hasResult ? View.GONE : View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        if (mAdapter != null) {
            mAdapter.setOnItemClickListener(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_common_action_bar_back:
                getActivity().finish();
                break;
        }
    }
}
