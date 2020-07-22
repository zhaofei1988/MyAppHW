package everlinkintl.com.myappwh.http;

import com.alibaba.cloudapi.sdk.enums.HttpMethod;
import com.alibaba.cloudapi.sdk.enums.Scheme;
import com.alibaba.cloudapi.sdk.model.ApiCallback;
import com.alibaba.cloudapi.sdk.model.ApiRequest;
import com.alibaba.cloudapi.sdk.model.ApiResponse;
import com.alibaba.cloudapi.sdk.model.HttpClientBuilderParams;

public class HttpApiClient extends com.alibaba.cloudapi.sdk.client.HttpApiClient {
    public final static String HOST = "tysbgpu.market.alicloudapi.com";
    static HttpApiClient instance = new HttpApiClient();
    public static HttpApiClient getInstance(){return instance;}

    public void init(HttpClientBuilderParams httpClientBuilderParams){
        httpClientBuilderParams.setScheme(Scheme.HTTP);
        httpClientBuilderParams.setHost(HOST);
        super.init(httpClientBuilderParams);
    }




    public void GPU(byte[] body , ApiCallback callback) {
        String path = "/api/predict/ocr_general";
        ApiRequest request = new ApiRequest(HttpMethod.POST_BODY , path, body);

        sendAsyncRequest(request , callback);
    }

    public ApiResponse GPU_syncMode(byte[] body) {
        String path = "/api/predict/ocr_general";
        ApiRequest request = new ApiRequest(HttpMethod.POST_BODY , path, body);



        return sendSyncRequest(request);
    }
}
