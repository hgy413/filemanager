package com.jiubang.commerce.ad.http.bean;

import android.content.Context;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class BaseModuleInfoBean implements Serializable {
    private static final int DEFAULT_VALUE_INT = 0;
    private static final String DEFAULT_VALUE_STRING = "";
    private static final long serialVersionUID = 1;
    private int mAdvPositionId;
    private String mBanner;
    private String mBgColor;
    private int mFirstScreen;
    private String mIcon;
    private List<BaseModuleDataItemBean> mModuleDataItemList;
    private int mModuleId;
    private String mModuleName;
    private int mPType;
    private String mSerialNum;
    private int mVirtualModuleId;

    public int getVirtualModuleId() {
        return this.mVirtualModuleId;
    }

    public void setVirtualModuleId(int virtualModuleId) {
        this.mVirtualModuleId = virtualModuleId;
    }

    public int getModuleId() {
        return this.mModuleId;
    }

    public void setModuleId(int moduleId) {
        this.mModuleId = moduleId;
    }

    public int getAdvPositionId() {
        return this.mAdvPositionId;
    }

    public void setAdvPositionId(int advPositionId) {
        this.mAdvPositionId = advPositionId;
    }

    public String getModuleName() {
        return this.mModuleName;
    }

    public void setModuleName(String moduleName) {
        this.mModuleName = moduleName;
    }

    public String getBgColor() {
        return this.mBgColor;
    }

    public void setBgColor(String bgColor) {
        this.mBgColor = bgColor;
    }

    public String getBanner() {
        return this.mBanner;
    }

    public void setBanner(String banner) {
        this.mBanner = banner;
    }

    public String getIcon() {
        return this.mIcon;
    }

    public void setIcon(String icon) {
        this.mIcon = icon;
    }

    public int getPType() {
        return this.mPType;
    }

    public void setPType(int pType) {
        this.mPType = pType;
    }

    public int getFirstScreen() {
        return this.mFirstScreen;
    }

    public void setFirstScreen(int firstScreen) {
        this.mFirstScreen = firstScreen;
    }

    public String getSerialNum() {
        return this.mSerialNum;
    }

    public void setSerialNum(String serialNum) {
        this.mSerialNum = serialNum;
    }

    public List<BaseModuleDataItemBean> getModuleDataItemList() {
        return this.mModuleDataItemList;
    }

    public void setModuleDataItemList(List<BaseModuleDataItemBean> moduleDataItemList) {
        this.mModuleDataItemList = moduleDataItemList;
    }

    public static List<BaseModuleInfoBean> parseJsonArray(Context context, JSONArray jsonArray, int virtualModuleId) {
        if (jsonArray == null || jsonArray.length() < 1) {
            return null;
        }
        List<BaseModuleInfoBean> moduleInfoList = new ArrayList<>();
        for (int index = 0; index < jsonArray.length(); index++) {
            try {
                BaseModuleInfoBean moduleInfoBean = parseJsonObject(context, jsonArray.getJSONObject(index), virtualModuleId);
                if (moduleInfoBean != null) {
                    moduleInfoList.add(moduleInfoBean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return moduleInfoList;
    }

    public static BaseModuleInfoBean parseJsonObject(Context context, JSONObject jsonObject, int virtualModuleId) {
        if (jsonObject == null) {
            return null;
        }
        BaseModuleInfoBean moduleInfoBean = new BaseModuleInfoBean();
        moduleInfoBean.mVirtualModuleId = virtualModuleId;
        moduleInfoBean.mModuleId = jsonObject.optInt("moduleId", 0);
        moduleInfoBean.mAdvPositionId = jsonObject.optInt("advpositionid", 0);
        moduleInfoBean.mModuleName = jsonObject.optString("moduleName", "");
        moduleInfoBean.mBgColor = jsonObject.optString("bgColor", "");
        moduleInfoBean.mBanner = jsonObject.optString("banner", "");
        moduleInfoBean.mIcon = jsonObject.optString("icon", "");
        moduleInfoBean.mPType = jsonObject.optInt("ptype", 0);
        moduleInfoBean.mFirstScreen = jsonObject.optInt("firstScreen", 0);
        moduleInfoBean.mSerialNum = jsonObject.optString("serialNum", "");
        return moduleInfoBean;
    }
}
