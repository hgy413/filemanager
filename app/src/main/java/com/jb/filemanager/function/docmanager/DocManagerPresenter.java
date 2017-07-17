package com.jb.filemanager.function.docmanager;

import android.content.Intent;
import android.widget.Toast;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.eventbus.FileOperateEvent;
import com.jb.filemanager.function.apkmanager.AppManagerActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
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
        EventBus globalEventBus = TheApplication.getGlobalEventBus();
        if (!globalEventBus.isRegistered(this)) {
            globalEventBus.register(this);
        }
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        EventBus globalEventBus = TheApplication.getGlobalEventBus();
        if (globalEventBus.isRegistered(this)) {
            globalEventBus.unregister(this);
        }
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
            if (!isSearchBack) {
                mView.finishActivity();
            }
        }
    }

    @Override
    public void onPressHomeKey() {

    }

    @Override
    public void scanStart() {
        Toast.makeText(TheApplication.getAppContext(), "System Scan Start", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void scanFinished() {
        Toast.makeText(TheApplication.getAppContext(), "System scan finish ,refresh the list", Toast.LENGTH_SHORT).show();
        //更新数据
        //refreshData(); 暂时不处理
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

    @Override
    public void handleFileDelete(List<File> docPathList) {
        //处理删除进度的问题
        int size = docPathList.size();
        for (int i = 0; i < size; i++) {
            mSupport.handleFileDelete(docPathList.get(i).getAbsolutePath());
            mView.updateDeleteProgress(i + 1, size + 1);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(FileOperateEvent fileOperateEvent){
        if (FileOperateEvent.OperateType.COPY.equals(fileOperateEvent.mOperateType)){
            handleFileCopy(fileOperateEvent);
        }else if (FileOperateEvent.OperateType.CUT.equals(fileOperateEvent.mOperateType)){
            handleFileCut(fileOperateEvent);
        }else if (FileOperateEvent.OperateType.RENAME.equals(fileOperateEvent.mOperateType)){
            handleFileDelete(fileOperateEvent);
        }else if (FileOperateEvent.OperateType.DELETE.equals(fileOperateEvent.mOperateType)){
            handleFileRename(fileOperateEvent);
        }else {
            handleError();
        }
    }

    private void handleFileCopy(FileOperateEvent fileOperateEvent) {

    }

    private void handleFileCut(FileOperateEvent fileOperateEvent) {

    }

    private void handleFileDelete(FileOperateEvent fileOperateEvent) {
        mSupport.handleFileDelete(fileOperateEvent.mOldFile.getAbsolutePath());
    }

    private void handleFileRename(FileOperateEvent fileOperateEvent) {

    }

    private void handleError() {

    }
}
