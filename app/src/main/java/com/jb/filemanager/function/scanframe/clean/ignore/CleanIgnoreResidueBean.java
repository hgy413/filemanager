package com.jb.filemanager.function.scanframe.clean.ignore;


import com.jb.filemanager.function.scanframe.bean.residuebean.ResidueBean;

import java.util.Collections;
import java.util.HashSet;

/**
 * 清理白名单残留数据Bean
 *
 * @author chenbenbin
 */
public class CleanIgnoreResidueBean extends CleanIgnoreBean {
    private static final String PACKAGE_NAME_DIVIDER = "#";
    private String mTitle;
    private String mPackageName;
    private HashSet<String> mPkgNameSet = new HashSet<String>();

    public CleanIgnoreResidueBean() {
        super(TYPE_RESIDUE);
    }

    public CleanIgnoreResidueBean(ResidueBean bean) {
        this();
        mTitle = bean.getTitle();
        mPkgNameSet.addAll(bean.getPkgNameSet());
        mPackageName = bean.getPackageName();
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public void setTitle(String title) {
        mTitle = title;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

    public HashSet<String> getPkgNameSet() {
        return mPkgNameSet;
    }

    public String getPkgNameString() {
        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        for (String pkgName : mPkgNameSet) {
            if (isFirst) {
                isFirst = false;
            } else {
                builder.append(PACKAGE_NAME_DIVIDER);
            }
            builder.append(pkgName);
        }
        return builder.toString();
    }

    public void setPkgNameString(String string) {
        mPkgNameSet.clear();
        Collections.addAll(mPkgNameSet, string.split(PACKAGE_NAME_DIVIDER));
    }

    public void setPkgNameSet(HashSet<String> set) {
        if (set != null) {
            mPkgNameSet = set;
        }
    }

    @Override
    public String toString() {
        return "CleanIgnoreResidueBean{" +
                "mTitle='" + mTitle + '\'' +
                ", mPackageName='" + mPackageName + '\'' +
                ", mPkgNameSet=" + mPkgNameSet +
                '}';
    }
}
