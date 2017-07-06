package com.jb.filemanager.function.search.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jb.filemanager.R;
import com.jb.filemanager.function.image.app.BaseFragment;
import com.jb.filemanager.function.search.modle.FileInfo;

import java.util.ArrayList;

/**
 * Created by nieyh on 17-7-5.
 */

public class SearchResultFragment extends BaseFragment {
    //参数
    public static final String ARG = "arg";
    //结果视图
    private RecyclerView mResultView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mResultView = (RecyclerView) view.findViewById(R.id.fragment_search_result_rv);
        Bundle bundle = getArguments();
        if (bundle != null) {
            ArrayList<FileInfo> fileInfoArrayList = bundle.getParcelableArrayList(ARG);
            mResultView.setLayoutManager(new LinearLayoutManager(getContext()));
            mResultView.setAdapter(new SearchResultAdapter(fileInfoArrayList));
        }
    }
}
