package com.android.ran.network;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class SignUpUtils {

    public static SignUpResponse parseError(Response<?> response) {

        Converter<ResponseBody, SignUpResponse> converter = APIUtils.getAPIErrorService();
        SignUpResponse message;

        try {
            message = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new SignUpResponse();
        }

        return message;
    }
}
