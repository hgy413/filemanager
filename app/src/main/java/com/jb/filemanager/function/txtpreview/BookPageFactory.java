package com.jb.filemanager.function.txtpreview;

import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;

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

    public static final String TAG = "BookPageFactory  ";
    private int mWidth;
    private int mHeight;
    private int mMarginWidth = 12; // 左右与边缘的距离
    private int mMarginHeight = 0; // 上下与边缘的距离
    private float mVisibleHeight; // 绘制内容的宽
    private float mVisibleWidth; // 绘制内容的宽

    private Paint mPaint;
    private int mFontSize = 16; //dp
    private float mFontSizePx;
    private int mTextColor = Color.LTGRAY;

    private int mLineCount = 0; // 每页可以显示的行数
    private int mLineWordCount = 0; // 每行可以显示的字数

    public volatile boolean isStillRead = true;
    private TxtLoadTask mTxtLoadTask;
    private OnTxtLoadListener mTxtLoadListener;


    private int mLoadedLineCount;//已经加载的行数
    private volatile boolean mIsLoadDone = true;//上次加载是否完成
    private boolean mIsAllLoad;//是否全部加载完成

    public BookPageFactory(int lineHeight) {

        mWidth = ScreenUtils.getScreenWidth();
        mHeight = ScreenUtils.getScreenHeight();

        mVisibleWidth = mWidth - DrawUtils.dip2px(mMarginWidth) * 2;
        mVisibleHeight = mHeight - DrawUtils.dip2px(mMarginHeight) * 2;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setTextSize(mFontSize);
        mPaint.setColor(mTextColor);

        mFontSizePx = DrawUtils.dip2px(mFontSize);
        mLineWordCount = (int) (mVisibleWidth / mFontSizePx);
        mLineCount = (int) (mVisibleHeight / lineHeight); // 可显示的行数

    }

    public void LoadTxtPath(String path) {
        if (!mIsLoadDone || mIsAllLoad) {
            return;
        }
        Logger.d(TAG, "start load !");
        mTxtLoadTask = new TxtLoadTask();
        mTxtLoadTask.executeOnExecutor(ZAsyncTask.THREAD_POOL_EXECUTOR, path);
    }

    public void setTxtLoadListener(OnTxtLoadListener txtLoadListener) {
        mTxtLoadListener = txtLoadListener;
    }

    public interface OnTxtLoadListener {
        void onLoadStart();

        void onLoadComplete();

        void onLoadError(String msg);

        void onLoadPart(ArrayList<String> part);
    }

    private class TxtLoadTask extends ZAsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String param = params[0];
            readTxt(param);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mIsLoadDone = false;
            if (mTxtLoadListener != null) {
                mTxtLoadListener.onLoadStart();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mTxtLoadListener != null) {
                mTxtLoadListener.onLoadComplete();
            }
            mIsLoadDone = true;
        }
    }

    public void cancelTask() {
        if (!mTxtLoadTask.isCancelled()) {
            mTxtLoadTask.cancel(true);
        }
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
        Logger.d(TAG, "readTxt");
        try {
            in = new FileInputStream(txtFile);
            in1 = new InputStreamReader(in, "UTF-8");
            bufferedReader = new BufferedReader(in1);
            int count = 0;
            Logger.d(TAG, "count :" + count + "count1:" + mLoadedLineCount);
            while ((line = bufferedReader.readLine()) != null && isStillRead) {
                count++;
                if (count < mLoadedLineCount) {
                    continue;
                }
                temp.append(line).append("\n");
                if (temp.length() > 3000) {
                    ArrayList<String> gbk = split(temp.toString(), mLineWordCount * 2, "GBK");
                    if (mTxtLoadListener != null) {
                        Logger.d(TAG, "load part " + mLoadedLineCount + "    " + count);
                        mLoadedLineCount += count;
                        mTxtLoadListener.onLoadPart(gbk);
                        Logger.d(TAG, "load part");
                        break;
                    }
                }
            }

            if (temp.length() < 3000) {
                //说明已经加载全部了
                mIsAllLoad = true;
                ArrayList<String> gbk = split(temp.toString(), mLineWordCount * 2, "GBK");
                if (mTxtLoadListener != null) {
                    Logger.d(TAG, "load part 2   " + mLoadedLineCount + "    " + count);
                    mLoadedLineCount += count;
                    mTxtLoadListener.onLoadPart(gbk);
                    Logger.d(TAG, "load part2  ");
                }
            }
        } catch (IOException e) {
            if (mTxtLoadListener != null) {
                mTxtLoadListener.onLoadError(e.toString());
            }
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

    /**
     * 分页处理
     *
     * @param text 需要分页的字符串
     * @param length 长度
     * @param encoding 编码
     * @return 分行的结果
     * @throws UnsupportedEncodingException 解码错误
     */
    public ArrayList<String> split(String text, int length, String encoding) throws UnsupportedEncodingException {
        ArrayList<String> texts = new ArrayList<>();
        if (TextUtils.isEmpty(text)) {
            return texts;
        }
        String temp = "    ";
        String c;
        int lines = 0;
        int pos = 2;
        int startInd = 0;
        for (int i = 0; i < text.length(); ) {
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
