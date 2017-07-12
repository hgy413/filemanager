package com.jb.filemanager.function.trash.adapter.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * add for .IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionViewHolder
 * 这是RecyclerView自己的一个bug  目前能做的只能是catch ╮(╯▽╰)╭
 * http://stackoverflow.com/questions/31759171/recyclerview-and-java-lang-indexoutofboundsexception-inconsistency-detected-in
 * http://blog.csdn.net/lovexieyuan520/article/details/50537846
 */
public class WrapContentLinearLayoutManager extends LinearLayoutManager {
    private boolean mCanScrollVertically = true;

    public WrapContentLinearLayoutManager(Context context) {
        super(context);
    }

    public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public WrapContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean canScrollVertically() {
        return mCanScrollVertically && super.canScrollVertically();
    }

    public void setCanScrollVertically(boolean canScrollVertically) {
        this.mCanScrollVertically = canScrollVertically;
    }
}