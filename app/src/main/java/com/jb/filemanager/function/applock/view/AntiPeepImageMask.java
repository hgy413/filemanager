package com.jb.filemanager.function.applock.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.text.Html;
import android.text.Spanned;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.TimeUtil;

/**
 * 防偷窥相机水印
 *
 * @author chenbenbin
 */
public class AntiPeepImageMask {
    private Context mContext;
    private String mPackageName;
    private ImageView mIcon;
    private ImageView mImage;
    private TextView mInfo;
    private TextView mTime;
    private View mLayout;

    public AntiPeepImageMask(Context context) {
        mContext = context.getApplicationContext();
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

    /**
     * 绘制水印
     * @param dst 最终绘制的Bitmap
     */
    public void drawWatermask(Bitmap dst) {
        Canvas canvas = new Canvas(dst);
        initScaleFactor(dst);
        // 设置内容
        mIcon.setImageBitmap(AppUtils.loadAppIcon(mContext.getApplicationContext(), mPackageName));
        mImage.setImageBitmap(dst);
        Spanned spanned = Html.fromHtml(
                mContext.getString(R.string.app_lock_anti_peep_img_app_info,
                        AppUtils.getAppName(mContext, mPackageName)));
        mInfo.setText(spanned);
        mTime.setText(TimeUtil.getCurrentTimeInString());
        // 绘制到画板上
        int withSpec = View.MeasureSpec.makeMeasureSpec(dst.getWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(dst.getHeight(), View.MeasureSpec.EXACTLY);
        mLayout.measure(withSpec, heightSpec);
        mLayout.layout(0, 0, mLayout.getMeasuredWidth(), mLayout.getMeasuredHeight());
        mLayout.draw(canvas);
    }

    /**
     * 根据照片尺寸初始化视图
     *
     * @param dst 图片
     */
    private void initScaleFactor(Bitmap dst) {
        if (mLayout != null) {
            return;
        }
        // 获取图片尺寸，换算出水印的比例，通过修改Density的方式进行适配
        float scaleFactor = dst.getWidth() / 1080.0f;
        float srcDensity = mContext.getResources().getDisplayMetrics().density;
        mContext.getResources().getDisplayMetrics().density *= scaleFactor;
        mLayout = LayoutInflater.from(mContext).inflate(R.layout.app_lock_ant_peep_img_layout, null);
        mContext.getResources().getDisplayMetrics().density = srcDensity;

        mIcon = (ImageView) mLayout.findViewById(R.id.app_lock_anti_peep_app_icon);
        mImage = (ImageView) mLayout.findViewById(R.id.app_lock_anti_peep_img);
        mInfo = (TextView) mLayout.findViewById(R.id.app_lock_anti_peep_app_info);
        mTime = (TextView) mLayout.findViewById(R.id.app_lock_anti_peep_time);
    }

}