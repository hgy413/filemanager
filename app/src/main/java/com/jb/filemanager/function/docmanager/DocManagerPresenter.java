package com.jb.filemanager.function.docmanager;

import android.content.Intent;

import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.function.apkmanager.AppManagerActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/4 11:07
 */

public class DocManagerPresenter implements DocManagerContract.Presenter{
    private DocManagerContract.View mView;
    private DocManagerContract.Support mSupport;

    public DocManagerPresenter(DocManagerContract.View view, DocManagerContract.Support support) {
        mView = view;
        mSupport = support;
    }

    @Override
    public void onCreate(Intent intent) {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        mView = null;
        mSupport = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppManagerActivity.UNINSTALL_APP_REQUEST_CODE) {
            //卸载完应用  所有数据重新获取
            refreshData();
        } else if (requestCode == AppManagerActivity.SEARCH_RESULT_REQUEST_CODE) {
            refreshData();//在结果页可能操作了APP  所以刷新数据
            mView.hideProgress();
        }
    }

    @Override
    public void refreshData() {
        onCreate(null);
        mView.refreshList();
    }

    @Override
    public void onClickBackButton(boolean isSearchBack) {
        if (mView != null) {
            if (isSearchBack) {
                mView.refreshTitle();
            } else {
                mView.finishActivity();
            }
        }
    }

    @Override
    public void onPressHomeKey() {

    }

    @Override
    public List<DocGroupBean> getDocInfo() {
        List<DocChildBean> textFileInfo = mSupport.getTextFileInfo();
        List<DocChildBean> docFileInfo = mSupport.getDocFileInfo();
        List<DocChildBean> pdfFileInfo = mSupport.getPdfFileInfo();

        DocGroupBean txtGroupBean = new DocGroupBean(textFileInfo,"TXT", GroupSelectBox.SelectState.NONE_SELECTED,true);
        DocGroupBean docGroupBean = new DocGroupBean(docFileInfo,"DOC", GroupSelectBox.SelectState.NONE_SELECTED,false);
        DocGroupBean pdfGroupBean = new DocGroupBean(pdfFileInfo,"PDF", GroupSelectBox.SelectState.NONE_SELECTED,false);

        ArrayList<DocGroupBean> groups = new ArrayList<>();
        groups.add(txtGroupBean);
        groups.add(docGroupBean);
        groups.add(pdfGroupBean);
        return groups;
    }
}
