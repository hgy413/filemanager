package com.gau.utils.net.operator;

import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.BasicResponse;
import com.gau.utils.net.response.IResponse;
import java.io.IOException;
import org.apache.http.HttpResponse;

public class StreamHttpOperator implements IHttpOperator {
    public IResponse operateHttpResponse(THttpRequest request, HttpResponse response) throws IllegalStateException, IOException {
        return new BasicResponse(2, response.getEntity().getContent());
    }
}
