package com.jiubang.commerce.ad.params;

public class ModuleRequestParams {
    private Integer mModuleId;
    private Integer mPageId;

    public ModuleRequestParams(Integer moduleId, Integer pageId) {
        this.mModuleId = moduleId;
        this.mPageId = pageId;
    }

    public Integer getModuleId() {
        return this.mModuleId;
    }

    public void setModuleId(Integer moduleId) {
        this.mModuleId = moduleId;
    }

    public Integer getPageId() {
        return this.mPageId;
    }

    public void setPageId(Integer pageId) {
        this.mPageId = pageId;
    }
}
