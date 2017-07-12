package com.jb.filemanager.function.image.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.function.image.ImageDetailFragment;
import com.jb.filemanager.function.image.app.BaseFragment;
import com.jb.filemanager.function.image.modle.ImageGroupModle;
import com.jb.filemanager.function.image.modle.ImageModle;
import com.jb.filemanager.function.image.presenter.ImageContract;
import com.jb.filemanager.util.QuickClickGuard;
import com.jb.filemanager.util.imageloader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nieyh on 17-7-7.
 */

public class ImageExpandableAdapter extends BaseExpandableListAdapter implements View.OnClickListener {

    private List<ImageGroupModle> mImageGroupModleList;
    private BaseFragment mCurrentFragment;
    private ImageContract.Presenter mPresenter;
    private QuickClickGuard mQuickClickGuard;

    public ImageExpandableAdapter(List<ImageGroupModle> imageGroupModleList, BaseFragment currentFragment, ImageContract.Presenter presenter) {
        this.mImageGroupModleList = imageGroupModleList;
        this.mCurrentFragment = currentFragment;
        this.mPresenter = presenter;
        mQuickClickGuard = new QuickClickGuard();
    }

    @Override
    public int getGroupCount() {
        if (mImageGroupModleList != null) {
            return mImageGroupModleList.size();
        }
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (mImageGroupModleList != null) {
            return mImageGroupModleList.get(groupPosition).mImageModleList.size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mImageGroupModleList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mImageGroupModleList.get(groupPosition).mImageModleList.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mImageGroupModleList.get(groupPosition).mTimeDate.hashCode();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mImageGroupModleList.get(groupPosition).mImageModleList.get(childPosition).hashCode();
    }

    /**
     * 通过 hasStableIds 和 getChildId 与 getGroupId 来 优化 造成局部刷新
     */
    @Override
    public boolean hasStableIds() {
        //此处返回false 可以
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewGroupHolder viewGroupHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_image_result, parent, false);
            viewGroupHolder = new ViewGroupHolder(convertView);
            convertView.setTag(viewGroupHolder);
        } else {
            viewGroupHolder = (ViewGroupHolder) convertView.getTag();
        }

        ImageGroupModle imageGroupModle = mImageGroupModleList.get(groupPosition);

        viewGroupHolder.mDate.setText(imageGroupModle.mTimeDate);
        if (imageGroupModle.isCheck) {
            viewGroupHolder.mGroupSelectBox.setState(GroupSelectBox.SelectState.ALL_SELECTED);
        } else {
            viewGroupHolder.mGroupSelectBox.setState(GroupSelectBox.SelectState.NONE_SELECTED);
        }
        viewGroupHolder.mGroupSelectBox.setTag(imageGroupModle);
        viewGroupHolder.mGroupSelectBox.setOnClickListener(this);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewItemHolder viewItemHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_result, parent, false);
            viewItemHolder = new ViewItemHolder(convertView);
            convertView.setTag(viewItemHolder);
        } else {
            viewItemHolder = (ViewItemHolder) convertView.getTag();
        }

        List<ImageModle> imageModleList = mImageGroupModleList.get(groupPosition).mImageModleList.get(childPosition);
        int size = imageModleList.size();
        for (int i = 0; i < size; i++) {
            ImageModle imageModle = imageModleList.get(i);
            viewItemHolder.showVisiable(i, groupPosition, childPosition, imageModle);
        }
        int end = size;
        while (end++ < 3) {
            viewItemHolder.gone(end);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onClick(View v) {
        if (mQuickClickGuard.isQuickClick(v.getId())) {
            return;
        }
        switch (v.getId()) {
            case R.id.item_sub_image_photo:
                Binder binder = (Binder) v.getTag();
                startDetailPager(binder);
                break;
            case R.id.item_sub_image_gsb:
                ImageModle tagImageModle = (ImageModle) v.getTag();
                tagImageModle.isChecked = !tagImageModle.isChecked;
                notifyDataSetChanged();
                if (mPresenter != null) {
                    mPresenter.handleSelected(mImageGroupModleList);
                }
                break;
            case R.id.group_image_result_gsb:
                ImageGroupModle imageGroupModle = (ImageGroupModle) v.getTag();
                boolean result = imageGroupModle.isCheck = !imageGroupModle.isCheck;
                for (int i = 0; i < imageGroupModle.mImageModleList.size(); i++) {
                    List<ImageModle> imageModleList = imageGroupModle.mImageModleList.get(i);
                    for (int i1 = 0; i1 < imageModleList.size(); i1++) {
                        imageModleList.get(i1).isChecked = result;
                    }
                }
                notifyDataSetChanged();
                if (mPresenter != null) {
                    mPresenter.handleSelected(mImageGroupModleList);
                }
                break;
        }
    }

    /**
     * 打开详情页
     */
    private void startDetailPager(Binder binder) {
        int group = binder.groupPos;
        int child = binder.childPos;
        ImageModle imageModle = binder.mImageModle;
        boolean isSet = false;
        int currentPos = 0;
        int size = 0;
        ArrayList<ImageModle> imageModleList = new ArrayList<>();
        for (int i = 0; i < mImageGroupModleList.size(); i++) {
            ImageGroupModle imageGroupModle = mImageGroupModleList.get(i);
            for (int i1 = 0; i1 < imageGroupModle.mImageModleList.size(); i1++) {
                List<ImageModle> imageModles = imageGroupModle.mImageModleList.get(i1);
                imageModleList.addAll(imageModles);

                if (group == i && child == i1 && !isSet) {
                    for (int i2 = 0; i2 < imageModles.size(); i2++) {
                        ImageModle imageModle1 = imageModles.get(i2);
                        if (imageModle1.mImagePath.equals(imageModle.mImagePath)) {
                            isSet = true;
                            currentPos = size + i2;
                        }
                    }
                }
                size += imageModles.size();
            }
        }

        ImageDetailFragment imageDetailFragment = new ImageDetailFragment();
        imageDetailFragment.setExtras(imageModleList, currentPos);
        mCurrentFragment.addFragment(imageDetailFragment);
    }

    /**
     * 时间组视图
     */
    private class ViewGroupHolder {
        //时间
        private TextView mDate;
        //check按钮
        private GroupSelectBox mGroupSelectBox;

        public ViewGroupHolder(View itemView) {
            mDate = (TextView) itemView.findViewById(R.id.group_image_result_date);
            mGroupSelectBox = (GroupSelectBox) itemView.findViewById(R.id.group_image_result_gsb);
        }
    }

    /**
     * 图片分组视图
     */
    private class ViewItemHolder {

        SubViewItemHolder[] mSubViewItem = new SubViewItemHolder[3];

        public ViewItemHolder(View itemView) {
            mSubViewItem[0] = new SubViewItemHolder(itemView.findViewById(R.id.item_image_result_1));
            mSubViewItem[1] = new SubViewItemHolder(itemView.findViewById(R.id.item_image_result_2));
            mSubViewItem[2] = new SubViewItemHolder(itemView.findViewById(R.id.item_image_result_3));
        }

        void showVisiable(int pos, int group, int child, ImageModle imageModle) {
            mSubViewItem[pos].showVisiable();
            mSubViewItem[pos].updateView(group, child, imageModle);
        }

        void gone(int pos) {
            mSubViewItem[pos].gone();
        }
    }

    private class SubViewItemHolder {
        //图片
        private ImageView mPhoto;
        //图片选项
        private GroupSelectBox mGroupSelectBox;
        //掩码
        private View mMask;

        private View root;

        private ImageModle mImageModle;

        public SubViewItemHolder(View itemView) {
            root = itemView;
            mPhoto = (ImageView) itemView.findViewById(R.id.item_sub_image_photo);
            mGroupSelectBox = (GroupSelectBox) itemView.findViewById(R.id.item_sub_image_gsb);
            mGroupSelectBox.setImageSource(R.drawable.choose_none, R.drawable.choose_all, R.drawable.choose_all);
            mMask = itemView.findViewById(R.id.item_sub_image_photo_mask);
        }

        void updateView(int group, int child, ImageModle imageModle) {
            if (imageModle != null) {
                //更新数据 用于直接修改
                mImageModle = imageModle;
                ImageLoader.getInstance(TheApplication.getAppContext()).displayImage(imageModle.mImagePath,
                        mPhoto, R.drawable.common_default_app_icon);
                if (imageModle.isChecked) {
                    mMask.setVisibility(View.VISIBLE);
                    mGroupSelectBox.setState(GroupSelectBox.SelectState.ALL_SELECTED);
                } else {
                    mMask.setVisibility(View.GONE);
                    mGroupSelectBox.setState(GroupSelectBox.SelectState.NONE_SELECTED);
                }
                mPhoto.setTag(new Binder(group, child, mImageModle));
                mGroupSelectBox.setTag(mImageModle);
                mPhoto.setOnClickListener(ImageExpandableAdapter.this);
                mGroupSelectBox.setOnClickListener(ImageExpandableAdapter.this);
            }
        }

        void showVisiable() {
            root.setVisibility(View.VISIBLE);
        }

        void gone() {
            root.setVisibility(View.GONE);
        }
    }

    /**
     * 借用下Binder机制的名字 最近正在研究
     */
    private class Binder {
        public int groupPos;
        public int childPos;
        public ImageModle mImageModle;

        public Binder(int groupPos, int childPos, ImageModle imageModle) {
            this.groupPos = groupPos;
            this.childPos = childPos;
            this.mImageModle = imageModle;
        }
    }
}