package com.android.ran.network;

import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class APIUtils {

    public static String PAYMENT_URL = "http://ec2-54-184-16-106.us-west-2.compute.amazonaws.com/paytm/";
    private static String BASE_URL = "http://ec2-54-184-16-106.us-west-2.compute.amazonaws.com:8080";

    private APIUtils() {
    }

    public static EndPointInterface getAPIService() {
        return RetrofitClientInstance.getRetrofitInstance(BASE_URL).create(EndPointInterface.class);
    }

    static Converter<ResponseBody, SignUpResponse> getAPIErrorService() {
        return RetrofitClientInstance.getRetrofitInstance(BASE_URL).responseBodyConverter(SignUpResponse.class, new Annotation[0]);
    }
}