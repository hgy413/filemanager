package com.jiubang.commerce.service;

import com.jiubang.commerce.ad.intelligent.api.IntelligentApi;
import com.jiubang.commerce.dyload.core.proxy.service.BaseProxyService;

public class IntelligentPreloadService extends BaseProxyService {
    public String getTargetPackageName() {
        return IntelligentApi.PACKAGE_NAME;
    }

    public String getTargetClassName() {
        return "com.jiubang.commerce.ad.intelligent.service.IntelligentPreloadService";
    }
}
