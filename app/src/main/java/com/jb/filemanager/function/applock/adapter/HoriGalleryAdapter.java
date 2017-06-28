package com.jb.filemanager.function.applock.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.jb.filemanager.R;
import com.jb.filemanager.function.applock.model.bean.IntruderDisplaySubBean;
import com.jb.filemanager.util.imageloader.ImageLoader;

import java.util.List;

/**
 * Created by nieyh on 2017/1/6.
 * 水平滑动的画廊适配器
 */

public class HoriGalleryAdapter extends BaseAdapter {

    private List<IntruderDisplaySubBean> mIntruderDisplaySubBeens;

    public HoriGalleryAdapter(List<IntruderDisplaySubBean> intruderDisplaySubBeens) {
        this.mIntruderDisplaySubBeens = intruderDisplaySubBeens;
    }

    public void bindData(List<IntruderDisplaySubBean> intruderDisplaySubBeens) {
        this.mIntruderDisplaySubBeens = intruderDisplaySubBeens;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mIntruderDisplaySubBeens.size();
    }

    @Override
    public Object getItem(int position) {
        return mIntruderDisplaySubBeens.size();
    }

    @Override
    public long getItemId(int position) {
        return mIntruderDisplaySubBeens.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HoriGalleryViewHolder horiGalleryViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.intruder_gallery_view, parent, false);
            horiGalleryViewHolder = new HoriGalleryViewHolder(convertView);
            convertView.setTag(horiGalleryViewHolder);
        } else {
            horiGalleryViewHolder = (HoriGalleryViewHolder) convertView.getTag();
        }

        IntruderDisplaySubBean intruderDisplaySubBean = mIntruderDisplaySubBeens.get(position);
        ImageLoader.getInstance(parent.getContext()).displayImage(intruderDisplaySubBean.getPath(),
                horiGalleryViewHolder.mIntruderMen);

        return convertView;
    }

    private class HoriGalleryViewHolder {
        /**
         * 此处照片展示嫌疑犯
         * ╭( ′• ㉨ •′ )╭☞警察蜀黍！就是这个银！
         * */
        private ImageView mIntruderMen;

        public HoriGalleryViewHolder(View contentView) {
            this.mIntruderMen = (ImageView) contentView.findViewById(R.id.intruder_gallery_view_img);
        }
    }
}
