package com.jiubang.commerce.ad.http;

import com.gau.utils.net.operator.IHttpOperator;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.BasicResponse;
import com.gau.utils.net.response.IResponse;
import com.jiubang.commerce.utils.ZipUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

public class AdvertJsonOperator implements IHttpOperator {
    private boolean mIsZipData = true;

    public AdvertJsonOperator() {
    }

    public AdvertJsonOperator(boolean isZipData) {
        this.mIsZipData = isZipData;
    }

    public IResponse operateHttpResponse(THttpRequest request, HttpResponse response) throws IllegalStateException, IOException {
        return new BasicResponse(4, parseData(response.getEntity().getContent(), this.mIsZipData));
    }

    public static JSONObject parseData(InputStream in, boolean isZipData) {
        String jsonString;
        if (in == null) {
            return null;
        }
        if (isZipData) {
            try {
                jsonString = ZipUtils.unzip(in);
            } catch (JSONException e) {
                e.printStackTrace();
                if (in == null) {
                    return null;
                }
                try {
                    in.close();
                    return null;
                } catch (IOException e2) {
                    e2.printStackTrace();
                    return null;
                }
            } catch (Throwable th) {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                throw th;
            }
        } else {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            while (true) {
                try {
                    int len = in.read(buff);
                    if (len == -1) {
                        break;
                    }
                    buffer.write(buff, 0, len);
                } catch (IOException e4) {
                    e4.printStackTrace();
                    if (in == null) {
                        return null;
                    }
                    try {
                        in.close();
                        return null;
                    } catch (IOException e5) {
                        e5.printStackTrace();
                        return null;
                    }
                }
            }
            jsonString = new String(buffer.toByteArray());
        }
        if (jsonString != null) {
            JSONObject jSONObject = new JSONObject(jsonString);
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e6) {
                    e6.printStackTrace();
                }
            }
            return jSONObject;
        } else if (in == null) {
            return null;
        } else {
            try {
                in.close();
                return null;
            } catch (IOException e7) {
                e7.printStackTrace();
                return null;
            }
        }
    }
}
