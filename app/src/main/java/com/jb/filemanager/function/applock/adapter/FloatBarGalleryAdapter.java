package com.jb.filemanager.function.applock.adapter;

import android.graphics.BitmapFactory;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.model.bean.IntruderDisplayBean;
import com.jb.filemanager.function.applock.model.bean.IntruderDisplaySubBean;
import com.jb.filemanager.function.applock.view.ViewHolder;
import com.jb.filemanager.util.imageloader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nieyh on 2017/1/4.
 * 画廊适配器
 * 描述：<br>
 * 一共分为两层：时间标题和图片列表 <br>
 */

public class FloatBarGalleryAdapter extends AbsAdapter<IntruderDisplayBean> {

    private int mLinePictureNum = 3;

    private int mScreenWidth = 0;

    private int mChildItemHeight;

    private FloatBarGalleryAdapter.onPhotoClickListener IOnPhotoClickListener;

    public FloatBarGalleryAdapter(List<IntruderDisplayBean> groups) {
        super(groups);
        mScreenWidth = TheApplication.getAppContext().getResources().getDisplayMetrics().widthPixels;
        mChildItemHeight = mScreenWidth / mLinePictureNum;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int childrenSize = mGroups.get(groupPosition).getchildrenSize();
        int mod = childrenSize % mLinePictureNum;
        int res = childrenSize / mLinePictureNum;
        if (mod > 0 ) {
            res += 1;
        }
        return res;
    }

    @Override
    public View onGetGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHodler holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_gallery_title_header, parent, false);
            holder = new GroupViewHodler(convertView);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHodler) convertView.getTag();
        }

        // 二级列表自动展开
        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        mExpandableListView.expandGroup(groupPosition);

        IntruderDisplayBean intruderDisplayBean = mGroups.get(groupPosition);
        holder.mTitle.setText(intruderDisplayBean.getTimeTitle());
        return convertView;
    }

    @Override
    public View onGetChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_gallery_child_item, parent, false);
            holder = new ChildViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        final List<IntruderDisplaySubBean> intruderDisplaySubBeanList = mGroups.get(groupPosition).getChildren();

        final List<IntruderDisplaySubBean> subBeanList = new ArrayList<>();
        final int size = intruderDisplaySubBeanList.size();
        for (int i = childPosition * 3; i < size && (i < childPosition * 3 + 3); i++) {
            subBeanList.add(intruderDisplaySubBeanList.get(i));
        }
        if (isLastChild) {
            ViewGroup.LayoutParams rootViewLayoutParams = holder.mRootView.getLayoutParams();
            rootViewLayoutParams.height = (int) (mChildItemHeight + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, parent.getContext().getResources().getDisplayMetrics()));;
            holder.mRootView.setLayoutParams(rootViewLayoutParams);
            holder.mRootView.setPadding(0, 0, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, parent.getContext().getResources().getDisplayMetrics()));
        } else {
            ViewGroup.LayoutParams rootViewLayoutParams = holder.mRootView.getLayoutParams();
            rootViewLayoutParams.height = mChildItemHeight;
            holder.mRootView.setLayoutParams(rootViewLayoutParams);
            holder.mRootView.setPadding(0, 0, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, parent.getContext().getResources().getDisplayMetrics()));
        }
        holder.updateView(subBeanList);
        return convertView;
    }

    //第一层布局
    private class GroupViewHodler {
        TextView mTitle;

        private GroupViewHodler(View parent) {
            mTitle = (TextView) parent.findViewById(R.id.layout_gallery_title_header_title);
        }
    }

    //第二层布局
    private class ChildViewHolder extends ViewHolder {
        public LinearLayout mRootView;
        private final SubViewHolder[] mItemViews = new SubViewHolder[3];


        public ChildViewHolder(View contentView) {
            setContentView(contentView);
            mRootView = (LinearLayout) findViewById(R.id.fragment_duplicate_child_root);
            int bgSize = mScreenWidth / 3;
            ViewGroup.LayoutParams rootViewLayoutParams = mRootView.getLayoutParams();
            rootViewLayoutParams.height = bgSize;
            mRootView.setLayoutParams(rootViewLayoutParams);

            mItemViews[0] = new SubViewHolder(findViewById(R.id.layout_gallery_child_item_0));
            mItemViews[0].mPhoto.setMaxWidth(bgSize);
            mItemViews[0].mPhoto.setMaxHeight(bgSize);
            mItemViews[1] = new SubViewHolder(findViewById(R.id.layout_gallery_child_item_1));
            mItemViews[1].mPhoto.setMaxWidth(bgSize);
            mItemViews[1].mPhoto.setMaxHeight(bgSize);
            mItemViews[2] = new SubViewHolder(findViewById(R.id.layout_gallery_child_item_2));
            mItemViews[2].mPhoto.setMaxWidth(bgSize);
            mItemViews[2].mPhoto.setMaxHeight(bgSize);
        }

        public void updateView(List<IntruderDisplaySubBean> intruderDisplaySubBeans) {
            for (int i = 0; i < mItemViews.length; i++) {
                if (!intruderDisplaySubBeans.isEmpty() && i < intruderDisplaySubBeans.size()) {
                    final IntruderDisplaySubBean bean = intruderDisplaySubBeans.get(i);
                    mItemViews[i].setVisibility(View.VISIBLE);
                    mItemViews[i].updateView(bean);
                } else {
                    mItemViews[i].setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    //第二层布局中的子布局
    private class SubViewHolder extends ViewHolder {
        ImageView mPhoto;
        ImageView mNewSign;
        IntruderDisplaySubBean mIntruderDisplaySubBean;

        private SubViewHolder(View root) {
            setContentView(root);
            mPhoto = (ImageView) root.findViewById(R.id.layout_gallery_child_content_item_img);
            mNewSign = (ImageView) root.findViewById(R.id.layout_gallery_child_content_item_new_sign);
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = 0;
                    boolean isFind = false;
                    for (IntruderDisplayBean bean : mGroups) {
                        List<IntruderDisplaySubBean> subBeans = bean.getChildren();
                        for (IntruderDisplaySubBean subBean : subBeans) {
                            if (mIntruderDisplaySubBean.equals(subBean)) {
                                isFind = true;
                                break;
                            }
                            index++;
                        }
                        if (isFind) {
                            break;
                        }
                    }

                    ArrayList<String> pathList = new ArrayList<>();
                    for (IntruderDisplayBean bean : mGroups) {
                        for (IntruderDisplaySubBean subBean : bean.getChildren()) {
                            pathList.add(subBean.getPath());
                        }
                    }
                    if (IOnPhotoClickListener != null) {
                        IOnPhotoClickListener.onPhotoClick(pathList, index);
                    }
                }
            });
        }

        private void updateView(IntruderDisplaySubBean intruderDisplaySubBean) {
            mIntruderDisplaySubBean = intruderDisplaySubBean;
            final int imageMaxSize = mScreenWidth / 3;
            ImageLoader.getInstance(TheApplication.getAppContext()).displayImage(mIntruderDisplaySubBean.getPath(),
                    mPhoto, getImgScaleFactor(mIntruderDisplaySubBean, imageMaxSize));
            if (intruderDisplaySubBean.isReaded()) {
                mNewSign.setVisibility(View.GONE);
            } else {
                mNewSign.setVisibility(View.VISIBLE);
            }

        }
    }

    private int getImgScaleFactor(IntruderDisplaySubBean bean, int imageMaxSize) {
        int scaleFactor;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(bean.getPath(), options);
        int width = options.outWidth;
        int height = options.outHeight;

        if (height < width) {
            scaleFactor = height / imageMaxSize;
        } else {
            scaleFactor = width / imageMaxSize;
        }
        if (scaleFactor < 1) {
            scaleFactor = 1;
        }
        return scaleFactor;
    }

    public void setOnPhotoClickListener(onPhotoClickListener onPhotoClickListener) {
        this.IOnPhotoClickListener = onPhotoClickListener;
    }

    /**
     * 监听器
     * */
    public interface onPhotoClickListener {
        void onPhotoClick(ArrayList<String> pathList, int index);
    }

    public void bindData(List<IntruderDisplayBean> groups) {
        mGroups = groups;
        notifyDataSetChanged();
    }
}
