package everlinkintl.com.myappwh.http;

import com.alibaba.cloudapi.sdk.constant.SdkConstant;
import com.alibaba.cloudapi.sdk.model.ApiCallback;
import com.alibaba.cloudapi.sdk.model.HttpClientBuilderParams;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class AlBaBaAPI {
    static{
        //HTTP Client init
        HttpClientBuilderParams httpParam = new HttpClientBuilderParams();
        httpParam.setAppKey("203779186 ");
        httpParam.setAppSecret("tsdidl36e1879o0qj12fq30rcw3bcd0j");
        HttpApiClient.getInstance().init(httpParam);


        //HTTPS Client init
        HttpClientBuilderParams httpsParam = new HttpClientBuilderParams();
        httpsParam.setAppKey("203779186 ");
        httpsParam.setAppSecret("tsdidl36e1879o0qj12fq30rcw3bcd0j");

        /**
         * HTTPS request use DO_NOT_VERIFY mode only for demo
         * Suggest verify for security
         */
        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }
        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        httpsParam.setSslSocketFactory(sslContext.getSocketFactory());
        httpsParam.setX509TrustManager(xtm);
        httpsParam.setHostnameVerifier(DO_NOT_VERIFY);

        HttpsApiClient.getInstance().init(httpsParam);


    }

    public static void GPUHttpTest(String data,ApiCallback apiCallback){
        HttpApiClient.getInstance().GPU(data.getBytes(SdkConstant.CLOUDAPI_ENCODING) , apiCallback);
    }



    public static void GPUHttpsTest(String test,ApiCallback apiCallback){

        HttpsApiClient.getInstance().GPU(test.getBytes(SdkConstant.CLOUDAPI_ENCODING) ,apiCallback);

    }





}
