package com.jiubang.commerce.ad.window.activation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.jiubang.commerce.ad.AdSdkApi;
import com.jiubang.commerce.ad.ResourcesProvider;
import com.jiubang.commerce.ad.bean.AdInfoBean;
import com.jiubang.commerce.ad.manager.AdImageManager;
import com.jiubang.commerce.ad.window.ActivationGuideWindowManager;
import com.jiubang.commerce.thread.AdSdkThreadExecutorProxy;
import java.util.ArrayList;
import java.util.List;

public class ActivationRecommendAdapter extends BaseAdapter implements View.OnClickListener, View.OnTouchListener {
    private static final int SHADOW_COLOR = 2130706432;
    private ActivationGuideWindowManager mActivationGuideWindowManager;
    private List<AdInfoBean> mAdInfoList;
    private Context mContext;
    private LayoutInflater mInflater = LayoutInflater.from(this.mContext);

    public ActivationRecommendAdapter(Context context, ActivationGuideWindowManager windowManager, List<AdInfoBean> adInfoList) {
        this.mContext = context;
        this.mActivationGuideWindowManager = windowManager;
        if (adInfoList != null && adInfoList.size() > 0) {
            updateData(adInfoList);
        }
    }

    public void updateData(List<AdInfoBean> adInfoList) {
        if (this.mAdInfoList == null) {
            this.mAdInfoList = new ArrayList();
        } else {
            this.mAdInfoList.clear();
        }
        if (adInfoList != null) {
            this.mAdInfoList.addAll(adInfoList);
            if (this.mAdInfoList.size() > 0) {
                for (AdInfoBean adInfoBean : this.mAdInfoList) {
                    AdSdkApi.showAdvert(this.mContext, adInfoBean, "", "");
                }
            }
        }
        notifyDataSetChanged();
    }

    public int getCount() {
        if (this.mAdInfoList != null) {
            return this.mAdInfoList.size();
        }
        return 0;
    }

    public AdInfoBean getItem(int position) {
        if (this.mAdInfoList != null) {
            return this.mAdInfoList.get(position);
        }
        return null;
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public boolean onTouch(View view, MotionEvent event) {
        ImageView iconImageView;
        if (!(view == null || !(view.getTag() instanceof ViewHolder) || (iconImageView = ((ViewHolder) view.getTag()).mIconImageView) == null || iconImageView.getDrawable() == null)) {
            switch (event.getAction()) {
                case 0:
                    try {
                        iconImageView.getDrawable().setColorFilter(SHADOW_COLOR, PorterDuff.Mode.SRC_ATOP);
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                case 1:
                case 3:
                    try {
                        iconImageView.getDrawable().clearColorFilter();
                        break;
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        break;
                    }
            }
        }
        return false;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            viewHolder = new ViewHolder();
            convertView = this.mInflater.inflate(ResourcesProvider.getInstance(this.mContext).getLayoutId("ad_activation_recommend_item"), (ViewGroup) null);
            viewHolder.mIconImageView = (ImageView) convertView.findViewById(ResourcesProvider.getInstance(this.mContext).getId("dialog_item_icon"));
            viewHolder.mAppNameTextView = (TextView) convertView.findViewById(ResourcesProvider.getInstance(this.mContext).getId("dialog_item_name"));
            convertView.setTag(viewHolder);
            convertView.setOnClickListener(this);
            convertView.setOnTouchListener(this);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        AdInfoBean adInfoBean = getItem(position);
        if (adInfoBean != null) {
            viewHolder.mAppNameTextView.setText(adInfoBean.getName());
            viewHolder.mAppNameTextView.setTag(adInfoBean);
            setIconAsync(viewHolder.mIconImageView, adInfoBean.getIcon());
        }
        return convertView;
    }

    private void setIconAsync(final ImageView imageView, String imageUrl) {
        if (imageView != null && !TextUtils.isEmpty(imageUrl)) {
            imageView.setTag(imageUrl);
            imageView.setImageResource(ResourcesProvider.getInstance(this.mContext).getDrawableId("default_icon"));
            AdSdkApi.loadAdImage(this.mContext, imageUrl, new AdImageManager.ILoadSingleAdImageListener() {
                public void onLoadFinish(String imageUrl, final Bitmap bitmap) {
                    String imageUrlTag = imageView != null ? (String) imageView.getTag() : null;
                    if (bitmap != null && imageUrlTag != null && imageUrlTag.equals(imageUrl)) {
                        AdSdkThreadExecutorProxy.runOnMainThread(new Runnable() {
                            public void run() {
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    }
                }

                public void onLoadFail(String imageUrl) {
                }
            });
        }
    }

    public void onClick(View view) {
        TextView appNameTextView;
        if ((view.getTag() instanceof ViewHolder) && (appNameTextView = ((ViewHolder) view.getTag()).mAppNameTextView) != null && (appNameTextView.getTag() instanceof AdInfoBean)) {
            AdSdkApi.clickAdvertWithToast(this.mContext, (AdInfoBean) appNameTextView.getTag(), "", "", true);
            if (this.mActivationGuideWindowManager != null) {
                this.mActivationGuideWindowManager.hideActivationGuideWindow();
            }
        }
    }

    public class ViewHolder {
        public TextView mAppNameTextView;
        public ImageView mIconImageView;

        public ViewHolder() {
        }
    }
}
