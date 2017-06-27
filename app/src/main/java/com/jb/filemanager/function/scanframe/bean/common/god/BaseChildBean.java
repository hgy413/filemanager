package com.jb.filemanager.function.scanframe.bean.common.god;

/**
 * 上帝类<br>
 * 清理列表 - 所有子项的基础数据类型(二级/三级)
 *
 * @author chenbenbin
 */
public abstract class BaseChildBean {
    private ChildType mType;
    private String mKey;

    public BaseChildBean(ChildType type) {
        mType = type;
    }

    final public ChildType getChildType() {
        return mType;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public boolean isTypeItem() {
        return mType.equals(ChildType.ITEM);
    }

    public boolean isTypeSubItem() {
        return mType.equals(ChildType.SUB_ITEM);
    }

    public abstract long getSize();

    public abstract void setSize(long size);

    public abstract String getTitle();

    public abstract void setTitle(String title);

}
