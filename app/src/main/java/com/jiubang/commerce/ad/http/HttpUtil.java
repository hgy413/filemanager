package com.jiubang.commerce.ad.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpUtil {
    public static String convert2Url(Map<String, String> params) {
        StringBuffer sb = new StringBuffer();
        boolean isAtFirst = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!isAtFirst) {
                sb.append("&");
            } else {
                isAtFirst = false;
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }

    public static JSONObject putInJSONObject(Map<String, Object> params, JSONObject jsonObj) {
        if (jsonObj == null) {
            jsonObj = new JSONObject();
        }
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                try {
                    jsonObj.put(entry.getKey(), entry.getValue());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonObj;
    }

    public static String gzip(JSONObject json) {
        if (json == null) {
            return null;
        }
        return json.toString();
    }

    public static String getContentString(HttpResponse response, boolean isDataGzipped) {
        if (response == null || response.getStatusLine().getStatusCode() != 200) {
            return null;
        }
        try {
            return toString(response.getEntity().getContent(), isDataGzipped);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getJsonObject(HttpResponse response, boolean isDataGzipped) {
        try {
            return new JSONObject(getContentString(response, isDataGzipped));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String toString(InputStream inStream, boolean isDataGzipped) {
        try {
            byte[] bytes = toByteArray(inStream);
            if (isDataGzipped) {
                bytes = ungzip(bytes);
            }
            return new String(bytes, "utf-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0020  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0025  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static byte[] ungzip(byte[] r6) throws java.lang.Exception {
        /*
            r3 = 0
            r0 = 0
            java.io.ByteArrayInputStream r1 = new java.io.ByteArrayInputStream     // Catch:{ Exception -> 0x001b }
            r1.<init>(r6)     // Catch:{ Exception -> 0x001b }
            java.util.zip.GZIPInputStream r4 = new java.util.zip.GZIPInputStream     // Catch:{ Exception -> 0x0030, all -> 0x0029 }
            r4.<init>(r1)     // Catch:{ Exception -> 0x0030, all -> 0x0029 }
            byte[] r5 = toByteArray(r4)     // Catch:{ Exception -> 0x0033, all -> 0x002c }
            if (r1 == 0) goto L_0x0015
            r1.close()
        L_0x0015:
            if (r4 == 0) goto L_0x001a
            r4.close()
        L_0x001a:
            return r5
        L_0x001b:
            r2 = move-exception
        L_0x001c:
            throw r2     // Catch:{ all -> 0x001d }
        L_0x001d:
            r5 = move-exception
        L_0x001e:
            if (r0 == 0) goto L_0x0023
            r0.close()
        L_0x0023:
            if (r3 == 0) goto L_0x0028
            r3.close()
        L_0x0028:
            throw r5
        L_0x0029:
            r5 = move-exception
            r0 = r1
            goto L_0x001e
        L_0x002c:
            r5 = move-exception
            r0 = r1
            r3 = r4
            goto L_0x001e
        L_0x0030:
            r2 = move-exception
            r0 = r1
            goto L_0x001c
        L_0x0033:
            r2 = move-exception
            r0 = r1
            r3 = r4
            goto L_0x001c
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.ad.http.HttpUtil.ungzip(byte[]):byte[]");
    }

    private static int copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[4096];
        int count = 0;
        while (true) {
            int n = input.read(buffer);
            if (-1 == n) {
                return count;
            }
            output.write(buffer, 0, n);
            count += n;
        }
    }

    public static String[] jsonArray2StringArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() <= 0) {
            return null;
        }
        String[] ret = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            ret[i] = jsonArray.optString(i);
        }
        return ret;
    }

    public static JSONArray stringArray2JSONArray(String[] strArray) {
        JSONArray jsonArray = new JSONArray();
        if (strArray != null && strArray.length > 0) {
            for (String attachment : strArray) {
                jsonArray.put(attachment);
            }
        }
        return jsonArray;
    }
}
