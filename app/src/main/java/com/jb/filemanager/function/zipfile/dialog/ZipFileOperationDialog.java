package com.jb.filemanager.function.zipfile.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.zipfile.ZipFilePreviewActivity;
import com.jb.filemanager.function.zipfile.bean.ZipFileItemBean;
import com.jb.filemanager.function.zipfile.util.ZipUtils;
import com.jb.filemanager.ui.dialog.BaseDialog;

import net.lingala.zip4j.exception.ZipException;

import java.io.IOException;

import de.innosystec.unrar.exception.RarException;

/**
 * Created by xiaoyu on 2017/6/30 13:50.
 * <p>
 * 压缩文件操作项弹窗。
 * </p>
 */

public class ZipFileOperationDialog extends BaseDialog implements View.OnClickListener {

    // 错误码
    public static final int ERROR_CODE_IOEXCEPTION = 0x00;
    public static final int ERROR_CODE_RAREXCEPTION = 0x01;
    public static final int ERROR_CODE_ZIPEXCEPTION = 0x02;
    public static final int ERROR_CODE_ENCRYPTED_RAR = 0x03;

    private Context mContext;
    private ZipFileItemBean mFile;
    private ProgressDialog mProgressDialog;

    public ZipFileOperationDialog(Activity act, ZipFileItemBean fileItem) {
        super(act, true);
        mContext = getContext().getApplicationContext();
        mFile = fileItem;
        initializeView();
    }

    private void initializeView() {
        View rootView = View.inflate(mContext, R.layout.dialog_zip_file_detail, null);
        setContentView(rootView);

        TextView fileName = (TextView) rootView.findViewById(R.id.file_name);
        fileName.setText(mFile.getFileName());
        TextView btnView = (TextView) rootView.findViewById(R.id.btn_view);
        btnView.setOnClickListener(this);
        TextView btnExtract = (TextView) rootView.findViewById(R.id.btn_extract);
        btnExtract.setOnClickListener(this);
        TextView btnCancel = (TextView) rootView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_view:
                onPreviewBtnClick();
                break;
            case R.id.btn_extract:
                Toast.makeText(mContext, "extrace", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }

    /**
     * 点击预览按钮
     */
    private void onPreviewBtnClick() {
        this.dismiss();
        showProgressDialog();
        new Thread() {
            @Override
            public void run() {
                try {
                    boolean e = ZipUtils.isPackFileEncrypted(mFile.getFile());
                    if (!e && mProgressDialog.isShowing() && isAlive()) {
                        // 不关注格式, 未加密的文件直接跳转
                        enterDirectly();
                    } else if (e && mProgressDialog.isShowing() && isAlive() && ZipUtils.isZipFormatFile(mFile.getFile())) {
                        // zip格式的加密文件, 获取密码
                        enterWithPassword();
                    } else if (e && mProgressDialog.isShowing() && isAlive() && ZipUtils.isRarFormatFile(mFile.getFile())) {
                        showToast(ERROR_CODE_ENCRYPTED_RAR);
                    } else {
                        // 十有八九用户取消操作了, 不采取任何行动;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    showToast(ERROR_CODE_IOEXCEPTION);
                } catch (RarException e) {
                    e.printStackTrace();
                    showToast(ERROR_CODE_RAREXCEPTION);
                } catch (ZipException e) {
                    e.printStackTrace();
                    showToast(ERROR_CODE_ZIPEXCEPTION);
                }
            }
        }.start();
    }

    /**
     * 显示提示
     *
     * @param errorCode 错误码
     */
    private void showToast(final int errorCode) {
        TheApplication.postRunOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    dismissProgressDialog();
                    Toast.makeText(mContext, "文件错误 error_code=" + errorCode, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 直接进入预览页面
     */
    private void enterDirectly() {
        TheApplication.postRunOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissProgressDialog();
                ZipFilePreviewActivity.browserFile(mContext, mFile.getFile().getPath(), null);
            }
        });
    }

    /**
     * 获取密码后再进入
     */
    private void enterWithPassword() {
        TheApplication.postRunOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissProgressDialog();
                PasswordInputDialog passwordInputDialog = new PasswordInputDialog(mActivity);
                passwordInputDialog.setListener(new PasswordInputDialog.PasswordInputCallback() {
                    @Override
                    public void onInputFinish(String password) {
                        ZipFilePreviewActivity.browserFile(mContext, mFile.getFile().getPath(), password);
                    }
                });
                passwordInputDialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setTitle("获取文件加密状态");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
