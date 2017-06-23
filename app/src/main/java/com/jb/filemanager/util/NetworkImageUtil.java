package com.jb.filemanager.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.jb.filemanager.TheApplication;

/**
 *
 * @author chenhewen
 *
 */
public class NetworkImageUtil {

    private static RequestQueue sQueue;

    static {
        sQueue = Volley.newRequestQueue(TheApplication.getAppContext());
    }

    /**
     *
     * @author chenhewen
     *
     */
    public interface Listener {
        void onResponse(Bitmap arg0);
        void onErrorResponse(String str);
    }

    public static void load(Context context, String url, int maxWidth, int maxHeight, final Listener listener, RetryPolicy retryPolicy) {

        if (sQueue == null) {
            sQueue = Volley.newRequestQueue(context.getApplicationContext());
        }

        ImageRequest imgRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap arg0) {
                if (listener != null) {
                    listener.onResponse(arg0);
                }
            }
        }, maxWidth, maxHeight, Bitmap.Config.RGB_565, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
                if (listener != null) {
                    listener.onErrorResponse(arg0.getMessage());
                }
            }
        });
        if (retryPolicy != null) {
            imgRequest.setRetryPolicy(retryPolicy);
        }
        sQueue.add(imgRequest);
    }

    public static void load(Context context, String url, int maxWidth,
                            int maxHeight, final Listener listener) {
        load(context, url, maxWidth, maxHeight, listener, null);
    }

    public static void load(Context context, String url, final Listener listener) {
        //TODO 定义常量
        load(context, url, DrawUtils.dip2px(62), DrawUtils.dip2px(62), listener);
    }

    public static void loadAndSet(Context context, String url, final ImageView imageView) {

        if (imageView == null) {
            return;
        }

        RequestQueue mQueue = Volley.newRequestQueue(context);

        ImageLoader imageLoader = new ImageLoader(mQueue, new ImageCache() {

            @Override
            public Bitmap getBitmap(String arg0) {
                return null;
            }

            @Override
            public void putBitmap(String arg0, Bitmap arg1) {

            }

        });

        ImageListener listener = ImageLoader.getImageListener(imageView, 0, 0);
        imageLoader.get(url, listener);
    }

}