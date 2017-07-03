package com.jb.filemanager.function.apkmanager.searchresult;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.util.imageloader.IconLoader;

import java.util.List;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/3 16:47
 */

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ResultHolder> implements View.OnClickListener {

    private List<SearchResultBean> mBeanList;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public SearchResultAdapter(List<SearchResultBean> beanList) {
        mBeanList = beanList;
    }

    @Override
    public ResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_search_result, parent, false);
        return new ResultHolder(v);
    }

    @Override
    public void onBindViewHolder(ResultHolder holder, int position) {
        SearchResultBean baseAppBean = mBeanList.get(position);
        holder.mTvAppName.setText(baseAppBean.mAppName);
        holder.mTvAppPkgName.setText(baseAppBean.mPackageName);
        IconLoader.getInstance().displayImage(baseAppBean.mPackageName, holder.mIvAppIcon);
        holder.itemView.setOnClickListener(this);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mBeanList.size();
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(view,(int)view.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int data);
    }

    static class ResultHolder extends RecyclerView.ViewHolder {
        private ImageView mIvAppIcon;
        private TextView mTvAppName;
        private TextView mTvAppPkgName;

        public ResultHolder(View itemView) {
            super(itemView);
            mIvAppIcon = (ImageView) itemView.findViewById(R.id.iv_app_icon);
            mTvAppName = (TextView) itemView.findViewById(R.id.tv_app_name);
            mTvAppPkgName = (TextView) itemView.findViewById(R.id.tv_app_pkg_name);
        }
    }


}
