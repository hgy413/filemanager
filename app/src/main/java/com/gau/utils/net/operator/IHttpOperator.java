package com.gau.utils.net.operator;

import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.IResponse;
import java.io.IOException;
import org.apache.http.HttpResponse;

public interface IHttpOperator {
    IResponse operateHttpResponse(THttpRequest tHttpRequest, HttpResponse httpResponse) throws IllegalStateException, IOException;
}
