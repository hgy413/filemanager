package com.gau.utils.net.operator;

import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.BasicResponse;
import com.gau.utils.net.response.IResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpResponse;

public class StringOperator implements IHttpOperator {
    public IResponse operateHttpResponse(THttpRequest request, HttpResponse response) throws IllegalStateException, IOException {
        InputStream is = response.getEntity().getContent();
        ByteArrayOutputStream baopt = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        while (true) {
            int len = is.read(buff);
            if (len == -1) {
                return new BasicResponse(3, new String(baopt.toString()));
            }
            baopt.write(buff, 0, len);
        }
    }
}
