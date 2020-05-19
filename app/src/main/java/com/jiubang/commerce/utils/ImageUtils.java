package com.jiubang.commerce.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import javax.net.ssl.SSLHandshakeException;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

public class ImageUtils {
    private static final int CONNECT_TIME_OUT = 10000;
    private static final int READ_TIME_OUT = 30000;

    public static boolean saveBitmapToSDFile(Bitmap bitmap, String filePathName, Bitmap.CompressFormat iconFormat) {
        boolean result = false;
        try {
            FileUtils.createNewFile(filePathName, false);
            OutputStream outputStream = new FileOutputStream(filePathName);
            result = bitmap.compress(iconFormat, 100, outputStream);
            outputStream.close();
            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return result;
        } catch (Exception e2) {
            e2.printStackTrace();
            return result;
        }
    }

    public static Bitmap getBitmapFromSDCard(String filepath) {
        if (filepath == null) {
            return null;
        }
        File file = new File(filepath);
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        try {
            return BitmapFactory.decodeFile(filepath);
        } catch (OutOfMemoryError er) {
            er.printStackTrace();
            System.gc();
            try {
                return BitmapFactory.decodeFile(filepath);
            } catch (OutOfMemoryError ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }

    public static Bitmap loadImage(Context context, String imageUrl, String imagePath) {
        Bitmap bitmap = null;
        if (TextUtils.isEmpty(imageUrl)) {
            return null;
        }
        if (!TextUtils.isEmpty(imagePath)) {
            bitmap = getBitmapFromSDCard(imagePath);
        }
        if (bitmap == null) {
            return downloadNetworkImageToSdcard(context, imageUrl, imagePath);
        }
        return bitmap;
    }

    public static Bitmap downloadNetworkImageToSdcard(Context context, String imageUrl, String imagePath) {
        if (context == null || TextUtils.isEmpty(imageUrl) || !NetworkUtils.isNetworkOK(context)) {
            return null;
        }
        Bitmap result = null;
        InputStream inputStream = null;
        if (Build.VERSION.SDK_INT <= 8) {
            result = loadImagFromHttpClient(imageUrl);
        } else {
            HttpURLConnection urlCon = null;
            try {
                HttpURLConnection urlCon2 = (HttpURLConnection) new URL(imageUrl).openConnection();
                urlCon2.setConnectTimeout(CONNECT_TIME_OUT);
                urlCon2.setReadTimeout(READ_TIME_OUT);
                InputStream inputStream2 = (InputStream) urlCon2.getContent();
                if (inputStream2 != null) {
                    result = BitmapFactory.decodeStream(inputStream2);
                }
                if (inputStream2 != null) {
                    try {
                        inputStream2.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (urlCon2 != null) {
                    urlCon2.disconnect();
                }
            } catch (OutOfMemoryError e2) {
                e2.printStackTrace();
                LogUtils.e("Ad_SDK", "downloadNetworkImage(" + imageUrl + ")====" + e2.toString(), e2);
                System.gc();
                result = null;
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                }
                if (urlCon != null) {
                    urlCon.disconnect();
                }
            } catch (SocketTimeoutException e4) {
                e4.printStackTrace();
                LogUtils.e("Ad_SDK", "downloadNetworkImage(" + imageUrl + ")====" + e4.toString(), e4);
                result = null;
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e5) {
                        e5.printStackTrace();
                    }
                }
                if (urlCon != null) {
                    urlCon.disconnect();
                }
            } catch (SSLHandshakeException e6) {
                LogUtils.e("Ad_SDK", "downloadNetworkImage(" + imageUrl + ")====" + e6.toString(), e6);
                result = loadImagFromHttpClient(imageUrl);
                if (result == null) {
                    e6.printStackTrace();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e7) {
                        e7.printStackTrace();
                    }
                }
                if (urlCon != null) {
                    urlCon.disconnect();
                }
            } catch (Exception e8) {
                e8.printStackTrace();
                LogUtils.e("Ad_SDK", "downloadNetworkImage(" + imageUrl + ")====" + e8.toString(), e8);
                result = null;
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e9) {
                        e9.printStackTrace();
                    }
                }
                if (urlCon != null) {
                    urlCon.disconnect();
                }
            } catch (Throwable th) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e10) {
                        e10.printStackTrace();
                    }
                }
                if (urlCon != null) {
                    urlCon.disconnect();
                }
                throw th;
            }
        }
        if (result == null || TextUtils.isEmpty(imagePath) || !SDCardUtils.isSDCardAvaiable()) {
            return result;
        }
        saveBitmapToSDFile(result, imagePath, Bitmap.CompressFormat.PNG);
        return result;
    }

    private static Bitmap loadImagFromHttpClient(String imgUrl) {
        StatusLine statusLine;
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            HttpResponse httpResponse = HttpRequestUtils.executeHttpRequest(imgUrl);
            if (httpResponse != null) {
                statusLine = httpResponse.getStatusLine();
            } else {
                statusLine = null;
            }
            if (statusLine == null || (!(statusLine.getStatusCode() == 200 || statusLine.getStatusCode() == 203) || httpResponse.getEntity() == null || (inputStream = httpResponse.getEntity().getContent()) == null)) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return bitmap;
            }
            bitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            return bitmap;
        } catch (Exception e3) {
            e3.printStackTrace();
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e4) {
                    e4.printStackTrace();
                }
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e5) {
                    e5.printStackTrace();
                }
            }
            throw th;
        }
    }
}
