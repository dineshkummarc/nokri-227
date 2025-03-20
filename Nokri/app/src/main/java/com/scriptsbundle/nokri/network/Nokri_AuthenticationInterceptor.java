package com.scriptsbundle.nokri.network;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Glixen Technologies on 02/01/2018.
 */

public class Nokri_AuthenticationInterceptor implements Interceptor {


    private String authToken;
    Context context;

    public Nokri_AuthenticationInterceptor(String token, Context context) {
        this.authToken = token;
        this.context=context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder()
                .header("Authorization", authToken);
        Request request = builder.build();
        return chain.proceed(request);
    }

}
