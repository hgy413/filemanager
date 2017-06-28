package com.jb.filemanager.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 支持3种状态的多框
 *
 * @author chenbenbin
 */
public class GroupSelectBox extends ImageView {
    private SelectState mState = SelectState.NONE_SELECTED;

    public GroupSelectBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 勾选状态
     *
     * @author chenbenbin
     */
    public enum SelectState {
        /**
         * 未选
         */
        NONE_SELECTED,
        /**
         * 多选
         */
        MULT_SELECTED,
        /**
         * 全选
         */
        ALL_SELECTED;

        private int mRes;

        void setRes(int res) {
            mRes = res;
        }

        int getRes() {
            return mRes;
        }
    }

    public void setImageSource(int unSelectedRes, int multSelectedRes,
                               int allSelectedRes) {
        SelectState.NONE_SELECTED.setRes(unSelectedRes);
        SelectState.MULT_SELECTED.setRes(multSelectedRes);
        SelectState.ALL_SELECTED.setRes(allSelectedRes);
    }

    /**
     * 设置状态
     */
    public void setState(SelectState state) {
        mState = state;
        setImageResource(mState.getRes());
    }

    /**
     * 获取状态
     */
    public SelectState getState() {
        return mState;
    }
}