package com.jb.filemanager.home;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.R;
import com.jb.filemanager.home.bean.CategoryBean;

import java.util.ArrayList;

/**
 * Created by bill wang on 2017/6/22.
 *
 */

public class CategoryFragment extends Fragment {

    private GridView mCategoryView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(R.layout.fragment_main_category, container, false);

        // TODO test data
        CategoryBean bean1 = new CategoryBean(R.drawable.ic_main_category_image, "image", 199);
        CategoryBean bean2 = new CategoryBean(R.drawable.ic_main_category_image, "music", 99);
        CategoryBean bean3 = new CategoryBean(R.drawable.ic_main_category_image, "image", 199);
        CategoryBean bean4 = new CategoryBean(R.drawable.ic_main_category_image, "music", 99);
        CategoryBean bean5 = new CategoryBean(R.drawable.ic_main_category_image, "image", 199);
        CategoryBean bean6 = new CategoryBean(R.drawable.ic_main_category_image, "music", 99);
        CategoryBean bean7 = new CategoryBean(R.drawable.ic_main_category_image, "image", 199);
        CategoryBean bean8 = new CategoryBean(R.drawable.ic_main_category_image, "music", 99);
        CategoryBean bean9 = new CategoryBean(R.drawable.ic_main_category_image, "image", 199);
        CategoryBean bean10 = new CategoryBean(R.drawable.ic_main_category_image, "music", 99);
        CategoryBean bean11= new CategoryBean(R.drawable.ic_main_category_image, "image", 199);
        CategoryBean bean12 = new CategoryBean(R.drawable.ic_main_category_image, "music", 99);
        CategoryBean bean13 = new CategoryBean(R.drawable.ic_main_category_image, "image", 199);
        CategoryBean bean14 = new CategoryBean(R.drawable.ic_main_category_image, "music", 99);
        CategoryBean bean15 = new CategoryBean(R.drawable.ic_main_category_image, "image", 199);
        CategoryBean bean16 = new CategoryBean(R.drawable.ic_main_category_image, "music", 99);
        CategoryBean bean17 = new CategoryBean(R.drawable.ic_main_category_image, "image", 199);
        CategoryBean bean18 = new CategoryBean(R.drawable.ic_main_category_image, "music", 99);
        ArrayList<CategoryBean> arrayList = new ArrayList<>();
        arrayList.add(bean1);
        arrayList.add(bean2);
        arrayList.add(bean3);
        arrayList.add(bean4);
        arrayList.add(bean5);
        arrayList.add(bean6);
        arrayList.add(bean7);
        arrayList.add(bean8);
        arrayList.add(bean9);
        arrayList.add(bean10);
        arrayList.add(bean11);
        arrayList.add(bean12);
        arrayList.add(bean13);
        arrayList.add(bean14);
        arrayList.add(bean15);
        arrayList.add(bean16);
        arrayList.add(bean17);
        arrayList.add(bean18);


        CategoryAdapter adapter = new CategoryAdapter();
        adapter.setData(arrayList);

        mCategoryView = (GridView) rootView.findViewById(R.id.gv_main_category);
        mCategoryView.setAdapter(adapter);

        mCategoryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // TODO
                CategoryBean bean = (CategoryBean) mCategoryView.getItemAtPosition(position);
                Toast.makeText(parent.getContext(), bean.getCategoryName(), Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }


    private static class CategoryAdapter extends BaseAdapter {

        private ArrayList<CategoryBean> mData;

        public void setData(ArrayList<CategoryBean> data) {
            mData = data;
        }

        @Override
        public int getCount() {
            int result = 0;
            if (mData != null && mData.size() > 0) {
                result = mData.size();
            }
            return result;
        }

        @Override
        public Object getItem(int position) {
            Object result = null;
            if (mData != null && mData.size() > position) {
                result = mData.get(position);
            }
            return result;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            Context context = parent.getContext();
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_main_category,
                        parent, false);
                holder = new ViewHolder();
                holder.mIvIcon = (ImageView) convertView.findViewById(R.id.iv_main_category_icon);

                holder.mTvName = (TextView) convertView.findViewById(R.id.tv_main_category_name);
                if (holder.mTvName != null) {
                    holder.mTvName.getPaint().setAntiAlias(true);
                }

                holder.mTvNumber = (TextView) convertView.findViewById(R.id.tv_main_category_number);
                if (holder.mTvNumber != null) {
                    holder.mTvNumber.getPaint().setAntiAlias(true);
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (mData != null && mData.size() > position) {
                CategoryBean bean = mData.get(position);

                if (holder.mIvIcon != null) {
                    holder.mIvIcon.setImageResource(bean.getCategoryIconResId());
                }

                if (holder.mTvName != null) {
                    holder.mTvName.setText(bean.getCategoryName());
                }

                if (holder.mTvNumber != null) {
                    String number = context.getString(R.string.main_category_item_number, bean.getCategoryNumber());
                    holder.mTvNumber.setText(number);
                }
            }
            return convertView;
        }
    }

    private static class ViewHolder {
        ImageView mIvIcon;
        TextView mTvName;
        TextView mTvNumber;
    }
}
