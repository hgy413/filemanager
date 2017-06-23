package com.jb.filemanager.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jb.filemanager.R;

import java.util.List;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/6/7 20:14
 */

class MainDrawerAdapter extends RecyclerView.Adapter<MainDrawerAdapter.MainDrawerHolder> {

    private static final int TYPE_FEEDBACK = 1;
    private static final int TYPE_NORMAL = 0;
    private List<DrawerItemBean> mItemBeanList;
    private Context mContext;

    MainDrawerAdapter(List<DrawerItemBean> itemBeanList, Context context) {
        mItemBeanList = itemBeanList;
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        return MainDrawer.FEED_BACK.equals(mItemBeanList.get(position).mTag) ? TYPE_FEEDBACK : TYPE_NORMAL;
    }

    @Override
    public MainDrawerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate;
        switch (viewType) {
            case TYPE_FEEDBACK:
                inflate = LayoutInflater.from(mContext).inflate(R.layout.item_drawer_with_margin, parent, false);
                break;
            case TYPE_NORMAL:
                inflate = LayoutInflater.from(mContext).inflate(R.layout.item_drawer, parent, false);
                break;
            default:
                inflate = LayoutInflater.from(mContext).inflate(R.layout.item_drawer, parent, false);
                break;
        }
        return new MainDrawerHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final MainDrawerHolder holder, int position) {
        final DrawerItemBean drawerItemBean = mItemBeanList.get(position);
        holder.mTvDrawerName.setText(drawerItemBean.mItemName);
        holder.mIvDrawerIcon.setImageResource(drawerItemBean.mItemIconResId);
        holder.mLlDrawerItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    int adapterPosition = holder.getAdapterPosition();
                    mOnItemClickListener.onItemClick(mItemBeanList.get(adapterPosition).mTag, adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemBeanList.size();
    }


    static class MainDrawerHolder extends RecyclerView.ViewHolder {
        LinearLayout mLlDrawerItem;
        ImageView mIvDrawerIcon;
        TextView mTvDrawerName;

        MainDrawerHolder(View itemView) {
            super(itemView);
            mLlDrawerItem = (LinearLayout) itemView.findViewById(R.id.ll_drawer_item);
            mIvDrawerIcon = (ImageView) itemView.findViewById(R.id.iv_drawer_icon);
            mTvDrawerName = (TextView) itemView.findViewById(R.id.tv_drawer_name);
        }
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    interface OnRecyclerViewItemClickListener {
        void onItemClick(String tag, int position);
    }

    void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}