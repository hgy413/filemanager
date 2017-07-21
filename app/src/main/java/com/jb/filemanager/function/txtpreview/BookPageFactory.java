package com.jb.filemanager.function.txtpreview;

import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.os.ZAsyncTask;
import com.jb.filemanager.util.DrawUtils;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.ScreenUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * @author yuyh.
 * @date 16/8/7.
 */
public class BookPageFactory {

    private static final String TAG = "BookPageFactory  ";
    private int mWidth;
    private int mHeight;
    private int mMarginWidth = 12; // 左右与边缘的距离
    private int mMarginHeight = 20; // 上下与边缘的距离
    private float mVisibleHeight; // 绘制内容的宽
    private float mVisibleWidth; // 绘制内容的宽

    private Paint mPaint;
    private int mFontSize = 16; //dp
    private float mFontSizePx;
    private int mTextColor = Color.LTGRAY;

    private int mLineCount = 0; // 每页可以显示的行数
    private int mLineWordCount = 0; // 每行可以显示的字数

    public volatile boolean isStillRead = true;
    private final TxtLoadTask mTxtLoadTask;
    private OnTxtLoadListener mTxtLoadListener;


    private int mLineCount1;

    public BookPageFactory(int lineHeight) {
        mTxtLoadTask = new TxtLoadTask();
        mWidth = ScreenUtils.getScreenWidth();
        mHeight = ScreenUtils.getScreenHeight();

        mVisibleWidth = mWidth - DrawUtils.dip2px(mMarginWidth) * 2;
        mVisibleHeight = mHeight - DrawUtils.dip2px(mMarginHeight) * 2 - ScreenUtils.getStatusBarHeight(TheApplication.getAppContext());

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setTextSize(mFontSize);
        mPaint.setColor(mTextColor);

        mFontSizePx = DrawUtils.dip2px(mFontSize);
        mLineWordCount = (int) (mVisibleWidth / mFontSizePx);
        mLineCount = (int) (mVisibleHeight / lineHeight); // 可显示的行数

    }

    public void LoadTxtPath(String path) {
        mTxtLoadTask.executeOnExecutor(ZAsyncTask.THREAD_POOL_EXECUTOR, path);
    }

    public void setTxtLoadListener(OnTxtLoadListener txtLoadListener) {
        mTxtLoadListener = txtLoadListener;
    }

    public interface OnTxtLoadListener {
        void onLoadStart();

        void onLoadComplete(ArrayList<String> result);

        void onLoadError(String msg);

        void onLoadPart(ArrayList<String> part);
    }

    private class TxtLoadTask extends ZAsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String param = params[0];
            readPage(param);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TheApplication.postRunOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mTxtLoadListener != null) {
                        mTxtLoadListener.onLoadStart();
                    }
                }
            });
        }
    }

    public void cancelTask() {
        if (!mTxtLoadTask.isCancelled()) {
            mTxtLoadTask.cancel(true);
        }
    }

    /**
     * 读取文章并进行分页处理。增加线程锁，避免同时对一篇文章进行分页
     *
     * @param path
     * @return
     */
    public void readPage(String path) {
        readTxt(path);
    }

    /**
     * 读取文章的段落集合
     */
    public void readTxt(String basePath) {
        StringBuilder temp = new StringBuilder();

        BufferedReader bufferedReader = null;
        FileInputStream in = null;
        InputStreamReader in1 = null;
        File txtFile = new File(basePath);
        String line;
        try {
            in = new FileInputStream(txtFile);
            in1 = new InputStreamReader(in, "UTF-8");
            bufferedReader = new BufferedReader(in1);
            int count = 0;
            while ((line = bufferedReader.readLine()) != null && isStillRead) {
                if (count < mLineCount1) {
                    continue;
                }
                temp.append(line).append("\n");
                if (temp.length() > 1000) {
                    ArrayList<String> gbk = split(temp.toString(), mLineWordCount * 2, "GBK");
                    if (mTxtLoadListener != null) {
                        Logger.d(TAG, "load part " + mLineCount1 + "    " + count);
                        mLineCount1 += count;
                        mTxtLoadListener.onLoadPart(gbk);
                        Logger.d(TAG, "load part");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Logger.d(TAG, "finally run");
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                    Logger.d(TAG, "br close");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Logger.d(TAG, "br exception");
            }
            try {
                if (in != null) {
                    in.close();
                    Logger.d(TAG, "in close");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Logger.d(TAG, "in exception");
            }

            try {
                if (in1 != null) {
                    in1.close();
                    Logger.d(TAG, "in1 close");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Logger.d(TAG, "in1 exception");
            }
        }
    }

    public void releaseLocker() {
    }

    /**
     * 分页处理
     *
     * @param text
     * @param length
     * @param encoding
     * @return
     * @throws UnsupportedEncodingException
     */
    public ArrayList<String> split(String text, int length, String encoding) throws UnsupportedEncodingException {
        ArrayList<String> texts = new ArrayList();
        String temp = "    ";
        String c;
        int lines = 0;
        int pos = 2;
        int startInd = 0;
        for (int i = 0; text != null && i < text.length(); ) {
            byte[] b = String.valueOf(text.charAt(i)).getBytes(encoding);
            pos += b.length;
            if (pos >= length) {
                int endInd;
                if (pos == length) {
                    endInd = ++i;
                } else {
                    endInd = i;
                }
                temp += text.substring(startInd, endInd); // 加入一行
                lines++;
                if (lines >= mLineCount) { // 超出一页
                    texts.add(temp); // 加入
                    temp = "";
                    lines = 0;
                }
                pos = 0;
                startInd = i;
            } else {
                c = new String(b, encoding);
                if (c.equals("\n")) {
                    temp += text.substring(startInd, i + 1);
                    lines++;
                    if (lines >= mLineCount) {
                        texts.add(temp);
                        temp = "";
                        lines = 0;
                    }
                    temp += "    ";
                    pos = 2;
                    startInd = i + 1;
                }
                i++;
            }
        }
        if (startInd < text.length()) {
            temp += text.substring(startInd);
            lines++;
        }
        if (!TextUtils.isEmpty(temp))
            texts.add(temp);
        return texts;
    }
}
