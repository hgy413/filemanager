package com.jiubang.commerce.ad;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.jb.ga0.commerce.util.LogUtils;

public class ResourcesProvider {
    private static ResourcesProvider mInstance;
    private LayoutInflater mInflater;
    private String mPkgName;
    private Resources mResources;

    public static synchronized void freeInstance() {
        synchronized (ResourcesProvider.class) {
            if (mInstance != null) {
                mInstance = null;
            }
        }
    }

    public static synchronized ResourcesProvider getInstance(Context context) {
        ResourcesProvider resourcesProvider;
        synchronized (ResourcesProvider.class) {
            if (mInstance == null) {
                mInstance = new ResourcesProvider(context);
            }
            resourcesProvider = mInstance;
        }
        return resourcesProvider;
    }

    private ResourcesProvider(Context context) {
        this.mPkgName = context.getPackageName();
        this.mResources = context.getResources();
        this.mInflater = LayoutInflater.from(context);
    }

    public int getId(String res) {
        int id = 0;
        if (0 == 0 && (id = this.mResources.getIdentifier(res, "id", this.mPkgName)) == 0) {
            LogUtils.e("ResourcesProvider", "id:" + res + " is not found");
        }
        return id;
    }

    public int getLayoutId(String res) {
        int id = 0;
        if (0 == 0 && (id = this.mResources.getIdentifier(res, "layout", this.mPkgName)) == 0) {
            LogUtils.e("ResourcesProvider", "layout:" + res + " is not found");
        }
        return id;
    }

    public int getDrawableId(String res) {
        int id = 0;
        if (0 == 0 && (id = this.mResources.getIdentifier(res, "drawable", this.mPkgName)) == 0) {
            LogUtils.e("ResourcesProvider", "drawable:" + res + " is not found");
        }
        return id;
    }

    public int getColor(String res) {
        int id = this.mResources.getIdentifier(res, "color", this.mPkgName);
        if (id == 0) {
            LogUtils.e("ResourcesProvider", "color:" + res + " is not found");
        }
        return this.mResources.getColor(id);
    }

    public int getInteger(String res) {
        int id = this.mResources.getIdentifier(res, "integer", this.mPkgName);
        if (id == 0) {
            LogUtils.e("ResourcesProvider", "integer:" + res + " is not found");
        }
        return this.mResources.getInteger(id);
    }

    public float getDimension(String res) {
        int id = this.mResources.getIdentifier(res, "dimen", this.mPkgName);
        if (id == 0) {
            LogUtils.e("ResourcesProvider", "dimen:" + res + " is not found");
        }
        return this.mResources.getDimension(id);
    }

    public int getDimensionPixelSize(String res) {
        int id = this.mResources.getIdentifier(res, "dimen", this.mPkgName);
        if (id == 0) {
            LogUtils.e("ResourcesProvider", "dimen:" + res + " is not found");
        }
        return this.mResources.getDimensionPixelOffset(id);
    }

    public CharSequence getText(String res) {
        int id = this.mResources.getIdentifier(res, "string", this.mPkgName);
        if (id == 0) {
            LogUtils.e("ResourcesProvider", "string:" + res + " is not found");
        }
        return this.mResources.getText(id);
    }

    public String getString(String res) {
        int id = this.mResources.getIdentifier(res, "string", this.mPkgName);
        if (id == 0) {
            LogUtils.e("ResourcesProvider", "string:" + res + " is not found");
        }
        return this.mResources.getString(id);
    }

    public Drawable getDrawable(String res) {
        int id = this.mResources.getIdentifier(res, "drawable", this.mPkgName);
        if (id == 0) {
            LogUtils.e("ResourcesProvider", "drawable:" + res + " is not found");
        }
        return this.mResources.getDrawable(id);
    }

    public View getView(String res, ViewGroup root) {
        int id = this.mResources.getIdentifier(res, "layout", this.mPkgName);
        if (id == 0) {
            LogUtils.e("ResourcesProvider", "layout:" + res + " is not found");
        }
        return this.mInflater.inflate(id, root);
    }

    public View getView(String res, ViewGroup root, boolean attachToRoot) {
        int id = this.mResources.getIdentifier(res, "layout", this.mPkgName);
        if (id == 0) {
            LogUtils.e("ResourcesProvider", "layout:" + res + " is not found");
        }
        return this.mInflater.inflate(id, root, attachToRoot);
    }

    public Animation getAnimation(Application context, String res) {
        int id = this.mResources.getIdentifier(res, "anim", this.mPkgName);
        if (id == 0) {
            LogUtils.e("ResourcesProvider", "anim:" + res + " is not found");
        }
        return AnimationUtils.loadAnimation(context, id);
    }

    public XmlResourceParser getXml(String res) {
        int id = this.mResources.getIdentifier(res, "xml", this.mPkgName);
        if (id == 0) {
            LogUtils.e("ResourcesProvider", "xml:" + res + " is not found");
        }
        return this.mResources.getXml(id);
    }
}
