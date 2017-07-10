package com.jb.filemanager.ui.dialog;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/7 18:01
 */

public class FileRenameWatcher implements TextWatcher {
    enum InputState {
        TEXT_NORMAL, TEXT_ILLEGAL, TEXT_NULL
    }

    private InputState mNewState;
    private OnTextInputStateChangeListener mStateChangeListener;

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String inputContent = editable.toString();
//        String regEx = "^[\u4E00-\u9FA5a-zA-Z()_0-9]([\u4E00-\u9FA5a-zA-Z()_0-9 ]|[_]){0,19}[.][a-zA-Z]{1,10}$";//不能适应多语言
        String regEx = "^[^~!@#$%^&*+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{\\}【】‘；：”“’。，、？]{1,19}[.][a-zA-Z]{1,10}$";
        if (inputContent.length() == 0) {
            if (mNewState != InputState.TEXT_NULL) {
                mNewState = InputState.TEXT_NULL;
                if (mStateChangeListener != null) {
                    mStateChangeListener.onStateChange(mNewState);
                }
            }
        } else if (!inputContent.matches(regEx)) {
            if (mNewState != InputState.TEXT_ILLEGAL) {
                mNewState = InputState.TEXT_ILLEGAL;
                if (mStateChangeListener != null) {
                    mStateChangeListener.onStateChange(mNewState);
                }
            }
        } else {
            if (mNewState != InputState.TEXT_NORMAL) {
                mNewState = InputState.TEXT_NORMAL;
                if (mStateChangeListener != null) {
                    mStateChangeListener.onStateChange(mNewState);
                }
            }
        }

        if (mStateChangeListener != null) {
            mStateChangeListener.onInputChange(inputContent);
        }
    }

    public void setStateChangeListener(OnTextInputStateChangeListener stateChangeListener) {
        this.mStateChangeListener = stateChangeListener;
    }

    public interface OnTextInputStateChangeListener {
        void onStateChange(InputState newState);

        void onInputChange(String input);
    }
}
