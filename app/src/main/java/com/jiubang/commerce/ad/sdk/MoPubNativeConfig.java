package com.jiubang.commerce.ad.sdk;

import android.location.Location;
import com.mopub.nativeads.MoPubAdRenderer;
import com.mopub.nativeads.RequestParameters;
import java.util.EnumSet;

public class MoPubNativeConfig {
    public EnumSet<RequestParameters.NativeAdAsset> mAssetsSet;
    public Location mLocation = null;
    public MoPubAdRenderer mMoPubAdRenderer;

    public MoPubNativeConfig(MoPubAdRenderer renderer, EnumSet<RequestParameters.NativeAdAsset> assetsSet) {
        this.mMoPubAdRenderer = renderer;
        this.mAssetsSet = assetsSet;
    }

    public MoPubNativeConfig location(Location location) {
        this.mLocation = location;
        return this;
    }
}
