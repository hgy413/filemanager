package com.jiubang.commerce.ad.http.bean;

import android.content.Context;
import com.jiubang.commerce.ad.http.bean.flash.FlashBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class BaseContentResourceInfoBean implements Serializable {
    private static final int DEFAULT_VALUE_INT = 0;
    private static final String DEFAULT_VALUE_STRING = "";
    public static final int TYPE_APP_OR_GAME = 3;
    public static final int TYPE_BRAND_ADV = 6;
    public static final int TYPE_REAL_MODULE_TOPICS = 1;
    public static final int TYPE_VIRTUAL_MODULE_TOPICS = 2;
    public static final int TYPE_WALLPAPER = 4;
    private static final long serialVersionUID = 1;
    private BaseAppInfoBean mAppInfoBean;
    private String mBanner;
    private FlashBean mFlashBean;
    private BaseModuleInfoBean mModuleInfoBean;
    private String mSuperScriptUrl;
    private int mType;

    public int getType() {
        return this.mType;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public String getBanner() {
        return this.mBanner;
    }

    public void setBanner(String banner) {
        this.mBanner = banner;
    }

    public String getSuperScriptUrl() {
        return this.mSuperScriptUrl;
    }

    public void setSuperScriptUrl(String superScriptUrl) {
        this.mSuperScriptUrl = superScriptUrl;
    }

    public BaseModuleInfoBean getModuleInfoBean() {
        return this.mModuleInfoBean;
    }

    public void setModuleInfoBean(BaseModuleInfoBean moduleInfoBean) {
        this.mModuleInfoBean = moduleInfoBean;
    }

    public BaseAppInfoBean getAppInfoBean() {
        return this.mAppInfoBean;
    }

    public void setAppInfoBean(BaseAppInfoBean appInfoBean) {
        this.mAppInfoBean = appInfoBean;
    }

    public FlashBean getFlashBean() {
        return this.mFlashBean;
    }

    public static List<BaseContentResourceInfoBean> parseJsonArray(Context context, JSONArray jsonArray, int virtualModuleId, int moduleId, int adId, int advDataSource) {
        if (jsonArray == null || jsonArray.length() < 1) {
            return null;
        }
        List<BaseContentResourceInfoBean> contentResourceInfoList = new ArrayList<>();
        for (int index = 0; index < jsonArray.length(); index++) {
            try {
                BaseContentResourceInfoBean contentResourceInfoBean = parseJsonObject(context, jsonArray.getJSONObject(index), virtualModuleId, moduleId, adId, advDataSource);
                if (contentResourceInfoBean != null) {
                    contentResourceInfoList.add(contentResourceInfoBean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return contentResourceInfoList;
    }

    public static BaseContentResourceInfoBean parseJsonObject(Context context, JSONObject jsonObject, int virtualModuleId, int moduleId, int adId, int advDataSource) {
        boolean isBrandAdv = true;
        if (jsonObject == null) {
            return null;
        }
        BaseContentResourceInfoBean contentResourceInfoBean = new BaseContentResourceInfoBean();
        contentResourceInfoBean.mType = jsonObject.optInt("type", 0);
        contentResourceInfoBean.mBanner = jsonObject.optString("banner", "");
        contentResourceInfoBean.mSuperScriptUrl = jsonObject.optString("superscriptUrl", "");
        if (jsonObject.has("contentInfo")) {
            try {
                if (contentResourceInfoBean.mType == 1 || contentResourceInfoBean.mType == 2) {
                    contentResourceInfoBean.mModuleInfoBean = BaseModuleInfoBean.parseJsonObject(context, jsonObject.getJSONObject("contentInfo"), virtualModuleId);
                } else {
                    if (6 != contentResourceInfoBean.mType) {
                        isBrandAdv = false;
                    }
                    contentResourceInfoBean.mAppInfoBean = BaseAppInfoBean.parseJsonObject(context, jsonObject.getJSONObject("contentInfo"), virtualModuleId, moduleId, adId, advDataSource, isBrandAdv);
                    if (contentResourceInfoBean.mAppInfoBean == null) {
                        contentResourceInfoBean = null;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (5 != contentResourceInfoBean.mType) {
            return contentResourceInfoBean;
        }
        contentResourceInfoBean.mFlashBean = FlashBean.parseJson(jsonObject);
        return contentResourceInfoBean;
    }
}
