package com.jb.filemanager.util.images;

import android.support.v4.app.FragmentActivity;

import com.jb.filemanager.Const;

/**
 * Created by bill wang on 2017/6/28.
 *
 */

public class ImageUtils {

    public static ImageFetcher createImageFetcher(FragmentActivity activity, int imageSize, int defaultImageId) {
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(activity,
                Const.IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of
        // app memory

        // The ImageFetcher takes care of loading images into our ImageView
        // children asynchronously
        ImageFetcher imageFetcher = new ImageFetcher(activity, imageSize);
        imageFetcher.setLoadingImage(defaultImageId);
        imageFetcher.addImageCache(activity.getSupportFragmentManager(), cacheParams);
        return imageFetcher;
    }
}
