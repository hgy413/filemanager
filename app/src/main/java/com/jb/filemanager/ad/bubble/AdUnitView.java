package com.jb.filemanager.ad.bubble;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdScrollView;
import com.jb.filemanager.R;

/**
 * Created by bill wang on 15/9/15.
 *
 */
public class AdUnitView implements NativeAdScrollView.AdViewProvider {

    private Context mContext;
    private LayoutInflater mInflater;

    public AdUnitView(Context context) {
        mContext = context;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static void inflateAd(NativeAd nativeAd, View adView, Context context) {
        // Create native UI using the ad metadata.

        ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.ad_company_logo);
        TextView nativeAdTitle = (TextView) adView.findViewById(R.id.ad_name);
        TextView nativeAdBody = (TextView) adView.findViewById(R.id.ad_desc);
        MediaView nativeAdMedia = (MediaView) adView.findViewById(R.id.nativeAdMedia);
        Button nativeAdCallToAction = (Button) adView.findViewById(R.id.ad_action_button);

        NativeAd.Rating ratin = nativeAd.getAdStarRating();

        // Setting the Text
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(View.VISIBLE);
        nativeAdTitle.setText(nativeAd.getAdTitle());
        nativeAdBody.setText(nativeAd.getAdBody());

        // Downloading and setting the ad icon.
        NativeAd.Image adIcon = nativeAd.getAdIcon();
        NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

        // Downloading and setting the cover image.
        NativeAd.Image adCoverImage = nativeAd.getAdCoverImage();
        int bannerWidth = adCoverImage.getWidth();
        int bannerHeight = adCoverImage.getHeight();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        nativeAdMedia.setLayoutParams(new FrameLayout.LayoutParams(
                screenWidth,
                Math.min((int) (((double) screenWidth / (double) bannerWidth) * bannerHeight), screenHeight / 3)
        ));
        nativeAdMedia.setNativeAd(nativeAd);

        // Wire up the View with the native ad, the whole nativeAdContainer will be clickable
        nativeAd.registerViewForInteraction(nativeAdCallToAction);

        // Or you can replace the above call with the following function to specify the clickable areas.
        // nativeAd.registerViewForInteraction(adView,
        //     Arrays.asList(nativeAdCallToAction, nativeAdMedia));
    }

    @Override
    public View createView(NativeAd nativeAd, int i) {
        View adView = mInflater.inflate(R.layout.item_ad_unit, null);
        inflateAd(nativeAd, adView, mContext);
        return adView;
    }

    @Override
    public void destroyView(NativeAd nativeAd, View view) {
        nativeAd.unregisterView();
    }
}