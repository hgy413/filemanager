package com.jb.filemanager.ad;

import android.content.Context;
import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mszaro on 12/10/15.
 */
public class AdNetworkUtil {

    public static final String TAG = "AdNetworkUtil";

    private static HttpURLConnection createUrlConnection(final String endpoint) {
        try {
            final URL url = new URL(endpoint);

            final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setDefaultUseCaches(false);
            urlConnection.setAllowUserInteraction(false);
            urlConnection.setUseCaches(false);
            urlConnection.setInstanceFollowRedirects(false);
            urlConnection.setDoInput(true);

            return urlConnection;
        } catch (Exception ex) {
            return null;
        }
    }

    public static void httpGetAndLaunch(final Context context, final String endpoint, final Callback callback) {
        HttpURLConnection urlConnection = null;
        int responseCode = -1;

        try {
            urlConnection = createUrlConnection(endpoint);
            responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                // This is a redirect, we need to follow it.

                final String redirectLocation = urlConnection.getHeaderField("Location");
                if (redirectLocation.startsWith("http") || redirectLocation.startsWith("https")) {
                    // This is another HTTP link to follow
                    final URL baseURL = new URL(endpoint);

                    // Resolve relative URLs
                    final URL redirectURL = new URL(baseURL, redirectLocation);

                    // Follow the redirect
                    httpGetAndLaunch(context, redirectURL.toExternalForm(), callback);
                } else {
                    // This is a non HTTP or HTTPS link, which means it must be a deep link.

                    callback.onHttpGetSuccess(redirectLocation, 200);
                }
            } else if (responseCode >= 200 && responseCode <= 300) {
                // This is a 2XX code. We're done.

                callback.onHttpGetSuccess(endpoint, responseCode);
            } else {
                // This is an HTTP error code.

                callback.onHttpGetFailure(endpoint, responseCode);
            }

        } catch (Throwable th) {
            // Update with response code with the exception code only if response code
            // has not been fetched from the server

            callback.onHttpGetFailure(endpoint, -1);
        } finally {
            try {
                urlConnection.disconnect();
            } catch (Exception ex) {

            }
        }
    }

    /**
     *
     */
    public interface Callback {
        void onHttpGetSuccess(final String endpoint, final int responseCode);

        void onHttpGetFailure(final String endpoint, final int responseCode);
    }

    /**
     *
     */
    public static class AsyncHttpGetOperation extends AsyncTask<Void, Void, Void> {
        private Context mContext;
        private Callback mCallback;
        private String mEndpoint;

        public AsyncHttpGetOperation(final Context context, final String endpoint, Callback callback) {
            this.mCallback = callback;
            this.mEndpoint = endpoint;
            this.mContext = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            AdNetworkUtil.httpGetAndLaunch(mContext, mEndpoint, mCallback);
            return null;
        }
    }

}