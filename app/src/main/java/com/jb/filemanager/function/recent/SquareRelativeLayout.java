package com.jb.filemanager.function.recent;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by xiaoyu on 2017/7/17 20:31.
 */

public class SquareRelativeLayout extends RelativeLayout {
    public SquareRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int withSize = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(withSize, withSize);
    }
}
