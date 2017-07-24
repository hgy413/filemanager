package com.jb.filemanager.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.eventbus.FileOperateEvent;
import com.jb.filemanager.manager.file.FileManager;
import com.jb.filemanager.ui.dialog.DeleteFileDialog;
import com.jb.filemanager.ui.dialog.DocRenameDialog;
import com.jb.filemanager.ui.dialog.MultiFileDetailDialog;
import com.jb.filemanager.ui.dialog.SingleFileDetailDialog;
import com.jb.filemanager.util.APIUtil;
import com.jb.filemanager.util.FileUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bool on 17-7-5.
 * 底部功能栏
 */

public class BottomOperateBar extends LinearLayout implements View.OnClickListener{

    private Context mContext;
    private LinearLayout mLlCut, mLlCopy, mLlDelete, mLlMore; // 四个按钮
    public BottomOperateBar(Context context) {
        this(context, null, 0);
    }

    private Listener mListener;

    public BottomOperateBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomOperateBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.common_operate_bar, this, true);
        setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        //此处相当于布局文件中的Android:layout_gravity属性
        layoutParams.gravity = Gravity.BOTTOM;
        setLayoutParams(layoutParams);
        setBackgroundColor(getResources().getColor(R.color.bottom_bar_item_pressed_color));
        setVisibility(View.GONE);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLlCut = (LinearLayout) findViewById(R.id.ll_common_operate_bar_cut);
        mLlCopy = (LinearLayout) findViewById(R.id.ll_common_operate_bar_copy);
        mLlDelete = (LinearLayout) findViewById(R.id.ll_common_operate_bar_delete);
        mLlMore = (LinearLayout) findViewById(R.id.ll_common_operate_bar_more);
        mLlCut.setOnClickListener(this);
        mLlCopy.setOnClickListener(this);
        mLlDelete.setOnClickListener(this);
        mLlMore.setOnClickListener(this);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mListener == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.ll_common_operate_bar_cut: {
                ArrayList<File> selectedFiles = mListener.getCurrentSelectedFiles();
                FileManager.getInstance().setCutFiles(selectedFiles);
                mListener.afterCut();
            }
                break;
            case R.id.ll_common_operate_bar_copy: {
                ArrayList<File> selectedFiles = mListener.getCurrentSelectedFiles();
                FileManager.getInstance().setCopyFiles(selectedFiles);
                mListener.afterCopy();
            }
                break;
            case R.id.ll_common_operate_bar_delete: {
                DeleteFileDialog dialog = new DeleteFileDialog(mListener.getActivity(), new DeleteFileDialog.Listener() {
                    @Override
                    public void onConfirm(DeleteFileDialog dialog) {
                        dialog.dismiss();
                        if (mListener != null) {
                            ArrayList<File> selectedFiles = mListener.getCurrentSelectedFiles();
                            FileUtil.deleteSelectedFiles(selectedFiles);
                            mListener.afterDelete();
                        }
                    }

                    @Override
                    public void onCancel(DeleteFileDialog dialog) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
                break;
            case R.id.ll_common_operate_bar_more:
                showPopupMore();
                break;
        }
    }

    private void showPopupMore() {
        if (mListener != null && mListener.getActivity() != null) {
            ArrayList<File> selectedFiles = mListener.getCurrentSelectedFiles();
            if (selectedFiles != null && selectedFiles.size() > 0) {
                int resId;
                if (selectedFiles.size() > 1) {
                    resId = R.layout.pop_mutli_file_operate_more;
                } else {
                    resId = R.layout.pop_single_file_operate_more;
                }

                // 一个自定义的布局，作为显示的内容
                View contentView = mListener.getActivity().getLayoutInflater().inflate(resId, null);
                TextView details = (TextView) contentView.findViewById(R.id.tv_main_operate_more_detail);
                TextView rename = (TextView) contentView.findViewById(R.id.tv_main_operate_more_rename);

                int width = (int)getResources().getDimension(R.dimen.popup_window_width);
                final PopupWindow popupWindow = new PopupWindow(contentView,
                        width, ViewGroup.LayoutParams.WRAP_CONTENT, true);

                popupWindow.setTouchable(true);

                popupWindow.setTouchInterceptor(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                        // 这里如果返回true的话，touch事件将被拦截
                        // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                    }
                });

                // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
                // 我觉得这里是API的一个bug
                popupWindow.setBackgroundDrawable(APIUtil.getDrawable(mListener.getActivity(), R.color.white));

                int marginRight = (int)getResources().getDimension(R.dimen.popup_window_margin_right);
                int marginTarget = (int)getResources().getDimension(R.dimen.popup_window_margin_target);
                // 设置好参数之后再show
                popupWindow.showAsDropDown(this,
                        this.getWidth() - width - marginRight,
                        marginTarget);

                if (details != null) {
                    details.getPaint().setAntiAlias(true);
                    details.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDetailDialog();
                            popupWindow.dismiss();
                        }
                    });
                }

                if (rename != null) {
                    rename.getPaint().setAntiAlias(true);
                    rename.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showRenameDialog();
                            popupWindow.dismiss();
                        }
                    });
                }
            }
        }
    }

    private void showDetailDialog() {
        if (mListener != null) {
            ArrayList<File> selectedFiles = mListener.getCurrentSelectedFiles();
            if (selectedFiles != null && mListener.getActivity() != null) {
                if (selectedFiles.size() == 1) {
                    SingleFileDetailDialog singleFileDetailDialog = new SingleFileDetailDialog(mListener.getActivity(), selectedFiles.get(0), new SingleFileDetailDialog.Listener() {
                        @Override
                        public void onConfirm(SingleFileDetailDialog dialog) {
                            dialog.dismiss();
                        }
                    });
                    singleFileDetailDialog.show();
                } else {
                    MultiFileDetailDialog multiFileDetailDialog = new MultiFileDetailDialog(mListener.getActivity(), selectedFiles, new MultiFileDetailDialog.Listener() {
                        @Override
                        public void onConfirm(MultiFileDetailDialog dialog) {
                            dialog.dismiss();
                        }
                    });
                    multiFileDetailDialog.show();
                }
            }
        }
    }

    private void showRenameDialog() {
        if (mListener != null && mListener.getActivity() != null) {
            ArrayList<File> selectedFiles = mListener.getCurrentSelectedFiles();
            if (selectedFiles != null && selectedFiles.size() == 1 && selectedFiles.get(0).exists()) {
                final File file = selectedFiles.get(0);
                DocRenameDialog docRenameDialog = new DocRenameDialog(mListener.getActivity(), file.isDirectory(), new DocRenameDialog.Listener() {
                    @SuppressWarnings("ResultOfMethodCallIgnored")
                    @Override
                    public void onConfirm(DocRenameDialog dialog, String newName) {
                        if (file.exists()) {
                            File targetFile = new File(file.getParentFile().getAbsolutePath() + File.separator + newName);
                            file.renameTo(targetFile);
                            MediaScannerConnection.scanFile(mContext, new String[]{targetFile.getParentFile().getAbsolutePath()}, null,
                                    new MediaScannerConnection.OnScanCompletedListener() {
                                        @Override
                                        public void onScanCompleted(String path, Uri uri) {
                                            mListener.afterRename();
                                        }
                                    }); // 修改后的文件添加到系统数据库
                            TheApplication.postEvent(new FileOperateEvent(file, targetFile, FileOperateEvent.OperateType.RENAME));
                        }

                        if (mListener != null) {
                            mListener.afterRename();
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancel(DocRenameDialog dialog) {
                        dialog.dismiss();
                    }
                });
                docRenameDialog.show();
            }
        }

    }

    public interface Listener {

        // 获取选中的文件，用于逻辑操作
        ArrayList<File> getCurrentSelectedFiles();

        // 获取一个activity 用户dialog弹窗
        Activity getActivity();

        // 点击复制后调用，逻辑处理已做，页面更新。
        void afterCopy();

        // 点击剪切后调用，逻辑处理已做，页面更新。
        void afterCut();

        // 重命名后调用，逻辑处理已做，页面更新。
        void afterRename();

        // 点击确认删除选中文件后调用，逻辑处理已做，页面更新
        void afterDelete();
    }

}
