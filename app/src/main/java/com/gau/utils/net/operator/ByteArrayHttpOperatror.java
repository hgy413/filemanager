package com.gau.utils.net.operator;

import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.BasicResponse;
import com.gau.utils.net.response.IResponse;
import java.io.DataInputStream;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

public class ByteArrayHttpOperatror implements IHttpOperator {
    public IResponse operateHttpResponse(THttpRequest request, HttpResponse response) throws IOException {
        byte[] datas;
        HttpEntity entity = response.getEntity();
        DataInputStream dis = new DataInputStream(entity.getContent());
        if (entity.isChunked()) {
            StringBuffer stringBuffer = new StringBuffer();
            byte[] byteData = new byte[1024];
            while (true) {
                int n = dis.read(byteData);
                if (n == -1) {
                    break;
                }
                stringBuffer.append(new String(byteData, 0, n));
            }
            datas = stringBuffer.toString().getBytes();
        } else {
            long dataLength = entity.getContentLength();
            if (dataLength <= 0) {
                return null;
            }
            datas = new byte[((int) dataLength)];
            dis.readFully(datas);
        }
        return new BasicResponse(1, datas);
    }
}
