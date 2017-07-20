package com.jb.filemanager.function.txtpreview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jb.filemanager.R;

import java.util.ArrayList;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/20 17:35
 */

public class TxtPreviewAdapter extends RecyclerView.Adapter<TxtPreviewAdapter.TxtHolder> {

    private ArrayList<String> mTxtData;

    public TxtPreviewAdapter(ArrayList<String> txtData) {
        this.mTxtData = txtData;
    }

    @Override
    public TxtHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_txt_preview, parent, false);
        return new TxtHolder(v);
    }

    @Override
    public void onBindViewHolder(TxtHolder holder, int position) {
        String data = mTxtData.get(position);
        holder.mTvTxtPreview.setText(data);
    }

    @Override
    public int getItemCount() {
        return mTxtData.size();
    }

    static class TxtHolder extends RecyclerView.ViewHolder {
        private TextView mTvTxtPreview;

        public TxtHolder(View itemView) {
            super(itemView);
            mTvTxtPreview = (TextView) itemView.findViewById(R.id.tv_txt_preview);
        }
    }
}
