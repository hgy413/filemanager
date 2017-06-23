package com.jb.filemanager.ad.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;
import com.facebook.ads.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.ad.event.OnAdClickEvent;
import com.jb.filemanager.os.ZAsyncTask;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.DrawUtils;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.NetworkImageUtil;
import com.jb.filemanager.util.QuickClickGuard;
import com.jb.filemanager.util.StringUtil;
import com.jiubang.commerce.ad.AdSdkApi;
import com.jiubang.commerce.ad.bean.AdInfoBean;
import com.jiubang.commerce.ad.bean.AdModuleInfoBean;
import com.jiubang.commerce.ad.sdk.bean.SdkAdSourceAdWrapper;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

/**
 * 将广告数据绑定到视图上的帮助类
 * @author chenhewen
 *
 */
public class BindAdHelper {

	private static final String TAG = "BindAdHelper";

	private static QuickClickGuard sQuickClickGuard;

	public static void setName(AdViewBean bean, TextView textView) {
		if (textView != null) {
			if (bean.isFbNativeAd()) {
				NativeAd fbAdBean = bean.getFbAdBean();
				textView.setText(fbAdBean.getAdTitle());
			} else if (bean.isPubNativeAd()) {
				final PubNativeAd pubAdBean = bean.getPubAdBean();
				textView.setText(pubAdBean.getAppName());
			} else if (bean.isAppCenterAd()) {
				AdInfoBean acAdBean = bean.getAcAdBean();
				textView.setText(acAdBean.getName());
			} else if (bean.isAdmobNativeInsallAd()) {
				NativeAppInstallAd admobNativeInstallAd = bean.getAdmobNativeInstallAd();
				textView.setText(admobNativeInstallAd.getHeadline());
			} else if (bean.isAdmobNativeContentAd()) {
				NativeContentAd admobNativeContentAd = bean.getAdmobNativeContentAd();
				textView.setText(admobNativeContentAd.getHeadline());
			} else {
				textView.setText("");
			}
		}
	}

	public static void setDesc(AdViewBean bean, TextView textView) {
		if (textView != null) {
			if (bean.isFbNativeAd()) {
				NativeAd fbAdBean = bean.getFbAdBean();
				textView.setText(fbAdBean.getAdBody());
			} else if (bean.isPubNativeAd()) {
				PubNativeAd pubAdBean = bean.getPubAdBean();
				//缩进
				textView.setText(StringUtil.createIndentedText(pubAdBean.getDescription(),
						DrawUtils.dip2px(20), 0));
			} else if (bean.isAppCenterAd()) {
				AdInfoBean acAdBean = bean.getAcAdBean();
				textView.setText(acAdBean.getRemdMsg());
			} else if (bean.isAdmobNativeInsallAd()) {
				NativeAppInstallAd admobNativeInstallAd = bean.getAdmobNativeInstallAd();
				textView.setText(admobNativeInstallAd.getBody());
			} else if (bean.isAdmobNativeContentAd()) {
				NativeContentAd admobNativeContentAd = bean.getAdmobNativeContentAd();
				textView.setText(admobNativeContentAd.getBody());
			} else {
				textView.setText("");
			}
		}
	}
	
	public static boolean setIcon(Context context, AdViewBean bean, ImageView imageView) {
		if (imageView != null) {
			if (bean.isFbNativeAd()) {
				NativeAd fbAdBean = bean.getFbAdBean();
				NativeAd.downloadAndDisplayImage(fbAdBean.getAdIcon(), imageView);
			} else if (bean.isPubNativeAd()) {
				PubNativeAd pubAdBean = bean.getPubAdBean();
				NetworkImageUtil.loadAndSet(context, pubAdBean.getIconUrl(), imageView);
			} else if (bean.isAppCenterAd()) {
				AdInfoBean acAdBean = bean.getAcAdBean();
				NetworkImageUtil.loadAndSet(context, acAdBean.getIcon(), imageView);
			} else if (bean.isAdmobNativeInsallAd()) {
				NativeAppInstallAd admobNativeInstallAd = bean.getAdmobNativeInstallAd();
				com.google.android.gms.ads.formats.NativeAd.Image icon = admobNativeInstallAd.getIcon();
				if (icon != null) {
					imageView.setImageDrawable(icon.getDrawable());
				}
			} else if (bean.isAdmobNativeContentAd()) {
				NativeContentAd admobNativeContentAd = bean.getAdmobNativeContentAd();
				com.google.android.gms.ads.formats.NativeAd.Image logo = admobNativeContentAd.getLogo();
				if (logo != null) {
					imageView.setImageDrawable(logo.getDrawable());
				} else {
					return false;
				}
			}
		}
		return true;
	}
	
	public static void setBanner(Context context, AdViewBean bean, final ImageView imageView) {
		if (imageView != null) {
			int width = DrawUtils.dip2px(294);
			int height = DrawUtils.dip2px(154);
			final RetryPolicy retryPolicy = new DefaultRetryPolicy(5000, 3,
					DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
			if (bean.isFbNativeAd()) {
				NativeAd fbAdBean = bean.getFbAdBean();
				NativeAd.downloadAndDisplayImage(fbAdBean.getAdCoverImage(), imageView);
			} else if (bean.isPubNativeAd()) {
				PubNativeAd pubAdBean = bean.getPubAdBean();
				NetworkImageUtil.loadAndSet(context, pubAdBean.getBannerUrl(), imageView);
			} else if (bean.isAppCenterAd()) {
				AdInfoBean acAdBean = bean.getAcAdBean();
				NetworkImageUtil.load(context, acAdBean.getBanner(), width, height, new NetworkImageUtilListener(imageView), retryPolicy);
			/*
			NetworkImageUtil.load(context, acAdBean.getBanner(), width, height, new NetworkImageUtil.Listener() {
				@Override
				public void onResponse(Bitmap arg0) {
					imageView.setImageBitmap(arg0);
				}

				@Override
				public void onErrorResponse(String str) {
                    Logger.d(TAG, "setBanner bean.isAppCenterAd onErrorResponse " + str);
				}
			}, retryPolicy);*/
			} else if (bean.isAdmobNativeInsallAd()) {
				NativeAppInstallAd admobNativeInstallAd = bean.getAdmobNativeInstallAd();
				List<com.google.android.gms.ads.formats.NativeAd.Image> images = admobNativeInstallAd.getImages();
				if (images != null && images.get(0) != null) {
					imageView.setImageDrawable(images.get(0).getDrawable());
				}
			} else if (bean.isAdmobNativeContentAd()) {
				NativeContentAd adMobNativeAd = bean.getAdmobNativeContentAd();
				List<com.google.android.gms.ads.formats.NativeAd.Image> images = adMobNativeAd.getImages();
				if (images != null && images.get(0) != null) {
					imageView.setImageDrawable(images.get(0).getDrawable());
				}
			}
		}
	}

	static class NetworkImageUtilListener implements NetworkImageUtil.Listener {

		private WeakReference<ImageView> ref;

		public NetworkImageUtilListener(ImageView imageView) {
			ref = new WeakReference<>(imageView);
		}

		@Override
		public void onResponse(Bitmap arg0) {
			ImageView imageView = ref.get();
			if (imageView != null) {
				imageView.setImageBitmap(arg0);
			}
		}

		@Override
		public void onErrorResponse(String str) {
			Logger.d(TAG, "setBanner bean.isAppCenterAd onErrorResponse " + str);
		}
	}
	
	public static void setButton(AdViewBean bean, TextView button) {
		if (button != null) {
			if (bean.isFbNativeAd()) {
				NativeAd fbAdBean = bean.getFbAdBean();
				button.setText(fbAdBean.getAdCallToAction());
			} else if (bean.isPubNativeAd()) {
//				PubNativeAd pubAdBean = bean.getPubAdBean();
			} else if (bean.isAppCenterAd()) {
				button.setText(TheApplication.getAppContext().getString(R.string.details));
			} else if (bean.isAdmobNativeInsallAd()) {
				NativeAppInstallAd admobNativeInstallAd = bean.getAdmobNativeInstallAd();
				button.setText(admobNativeInstallAd.getCallToAction());
			} else if (bean.isAdmobNativeContentAd()) {
				NativeContentAd adMobNativeAd = bean.getAdmobNativeContentAd();
				button.setText(adMobNativeAd.getCallToAction());
			}
		}
	}
	
	//TODO 防爆点击
	public static void setClick(final Context context, final AdViewBean bean, final int adPositionId, View container, View...clickViews) {
		
		sQuickClickGuard = new QuickClickGuard();
		sQuickClickGuard.setLimitTime(2000);
		
		if (bean.isFbNativeAd()) {
			Logger.d(TAG, "fb native..");
			NativeAd fbAdBean = bean.getFbAdBean();
			List<View> clickViewsList = Arrays.asList(clickViews);
			fbAdBean.registerViewForInteraction(container, clickViewsList);
		} else if (bean.isPubNativeAd()) {
			Logger.d(TAG, "pub native..");
			final PubNativeAd pubAdBean = bean.getPubAdBean();
			for (View view : clickViews) {
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!sQuickClickGuard.isQuickClick(v.getId())) {
							AppUtils.openLinkSafe(TheApplication.getAppContext(),
									pubAdBean.getClickUrl());
							EventBus.getDefault().post(new OnAdClickEvent(bean.getAdType(), bean.getEntranceId(), bean.getHashCode()));
						}
					}
				});
			}
		} else if (bean.isAppCenterAd()) {
			Logger.d(TAG, "app center..");
			//unfinished code, because there are no appCenter ads here any more!
			for (View view : clickViews) {
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (!sQuickClickGuard.isQuickClick(v.getId())) {
							AdInfoBean acAdBean = bean.getAcAdBean();
							AdSdkApi.clickAdvertWithToast(TheApplication.getAppContext(), acAdBean, "", "", false, false);
							EventBus.getDefault().post(new OnAdClickEvent(bean.getAdType(), bean.getEntranceId(), bean.getHashCode()));
						}
					}
				});
			}
		}
	}

	//TODO 防爆点击
	public static void unbind(final AdViewBean bean) {
		if (bean.isFbNativeAd()) {
			Logger.d(TAG, "fb native.. unbind");
			NativeAd fbAdBean = bean.getFbAdBean();
			fbAdBean.unregisterView();
			fbAdBean.destroy();
		} else if (bean.isAdmobNativeInsallAd()) {
			bean.getAdmobNativeInstallAd().destroy();
		} else if (bean.isAdmobNativeContentAd()) {
			bean.getAdmobNativeContentAd().destroy();
		} else if (bean.isMoPubBannerAd()) {
			bean.getMoPubBannerAd().destroy();
		} else if (bean.isMoPubNative()) {
            bean.getMoPubNativeAd().destroy();
        } else if (bean.isFbInterstitialAd()) {
            bean.getFbInterstitialAd().destroy();
        }
    }



	//================统计======================

	/**
	 * 对于AppCenter来说是不是第三方应用, 统计方式不同
	 * @param bean bean
	 * @return result
	 */
	private static boolean isThirdPartyAdForAppCenter(AdViewBean bean) {
		return bean.isAdmobNativeContentAd()
				|| bean.isAdmobNativeInsallAd()
				|| bean.isAdmobInterstitialAd()
				|| bean.isFbNativeAd()
				|| bean.isFbInterstitialAd()
				|| bean.isMoPubBannerAd()
				|| bean.isMoPubNative();
	}

	/**
	 * 帮助appCenter进行展示统计(包括离线和第三方)
	 * @param context context
	 * @param bean bean
	 */
	public static void helpAppCenterShowStaticstic(Context context, AdViewBean bean) {
		if (bean.isAppCenterAd()) {
			AdSdkApi.showAdvert(context, bean.getAcAdBean(), "", "");
		} else if (isThirdPartyAdForAppCenter(bean)) {
			AdModuleInfoBean adModuleInfoBean = bean.getAdModuleInfoBean();
			if (adModuleInfoBean != null) {
				List<SdkAdSourceAdWrapper> adViewList = adModuleInfoBean.getSdkAdSourceAdInfoBean().getAdViewList();
				if (adViewList != null && adViewList.size() > 0) {
					SdkAdSourceAdWrapper wrapper = adViewList.get(0);
					AdSdkApi.sdkAdShowStatistic(context, bean.getAdModuleInfoBean().getModuleDataItemBean(), wrapper, null);
				}
			}
		}
	}

	/**
	 * 帮助appCenter进行第三方广告的点击统计
	 * @param context context
	 * @param bean bean
	 */
	public static void helpAppCenterClickStatistics(Context context, AdViewBean bean) {
		if (bean.isAppCenterAd()) {
			AdModuleInfoBean adModuleInfoBean = bean.getAdModuleInfoBean();
			if (adModuleInfoBean != null) {
				List<AdInfoBean> adInfoList =  adModuleInfoBean.getAdInfoList();
				if (adInfoList != null && adInfoList.size() > 0) {
					AdSdkApi.clickAdvertWithToast(context, adInfoList.get(0), "", "", false);
				}
			}
		} else if (isThirdPartyAdForAppCenter(bean)) {
			AdModuleInfoBean adModuleInfoBean = bean.getAdModuleInfoBean();
			if (adModuleInfoBean != null) {
				List<SdkAdSourceAdWrapper> adViewList = adModuleInfoBean.getSdkAdSourceAdInfoBean().getAdViewList();
				if (adViewList != null && adViewList.size() > 0) {
					SdkAdSourceAdWrapper wrapper = adViewList.get(0);
					AdSdkApi.sdkAdClickStatistic(context, bean.getAdModuleInfoBean().getModuleDataItemBean(), wrapper, null);
				}
			}
		}
	}

	public static void doSthSpeciallyOnShown(AdViewBean bean) {
		if (bean.isPubNativeAd()) {
			//PubNative广告有一个奇葩的要求, 就是广告展示出来需要和他们服务器确认

			final PubNativeAd pubAdBean = bean.getPubAdBean();
			
			Handler handler = new Handler(Looper.getMainLooper());
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					new ZAsyncTask<String, String, String>() {
						@Override
						protected String doInBackground(String... params) {
							pubAdBean.sendImpressionToServer(pubAdBean.getImpressionBeaconUrl());
							return "success";
						}
						
					}.executeOnExecutor(ZAsyncTask.THREAD_POOL_EXECUTOR);
				}
			}, 3000);
		}
	}
}
