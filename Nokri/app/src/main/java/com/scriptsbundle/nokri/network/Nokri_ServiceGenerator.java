package com.scriptsbundle.nokri.network;

import android.content.Context;
import android.text.TextUtils;

import com.scriptsbundle.nokri.utils.Nokri_Config;

import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Glixen Technologies on 02/01/2018.
 */

public class Nokri_ServiceGenerator {

    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(Nokri_Config.BASE_URL).addConverterFactory(GsonConverterFactory.create());
    private static Retrofit retrofit = builder.build();
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    public static <S> S createService(Class<S> serviceClass){


        return retrofit.create(serviceClass);
    }
  /*  public static <S> S createService(Class<S> serviceClass, String username,String password){
        if (!TextUtils.isEmpty(username)
                && !TextUtils.isEmpty(password)) {
            String authToken = Credentials.basic(username, password);
            return createService(serviceClass, authToken);
        }
        return createService(serviceClass, null,null); }*/

    public static <S> S createService(Class<S> serviceClass,String authToken,Context context){
        if (!TextUtils.isEmpty(authToken)) {
            Nokri_AuthenticationInterceptor interceptor = new Nokri_AuthenticationInterceptor(authToken,context);
            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);
              /*  httpClient.connectTimeout(0,TimeUnit.MINUTES);
                httpClient.readTimeout(0, TimeUnit.SECONDS);
               httpClient.writeTimeout(0,TimeUnit.SECONDS);*/
                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }
        return retrofit.create(serviceClass);
    }
    public static <S> S createService(
            Class<S> serviceClass, String username, String password, Context context) {
        if (!TextUtils.isEmpty(username)
                && !TextUtils.isEmpty(password)) {
            String authToken = Credentials.basic(username, password);
            return createService(serviceClass, authToken, context);
        }

        return createService(serviceClass, null, null, context);
    }


    public static <S> S createServiceNoTimeout(Class<S> serviceClass,String authToken,Context context){
        if (!TextUtils.isEmpty(authToken)) {
            Nokri_AuthenticationInterceptor interceptor = new Nokri_AuthenticationInterceptor(authToken,context);
            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);
                httpClient.connectTimeout(0,TimeUnit.MINUTES);
                httpClient.readTimeout(0, TimeUnit.SECONDS);
                httpClient.writeTimeout(0,TimeUnit.SECONDS);
                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }
        return retrofit.create(serviceClass);
    }
    public static <S> S createServiceNoTimeout(
            Class<S> serviceClass, String username, String password, Context context) {
        if (!TextUtils.isEmpty(username)
                && !TextUtils.isEmpty(password)) {
            String authToken = Credentials.basic(username, password);
            return createServiceNoTimeout(serviceClass, authToken, context);
        }

        return createServiceNoTimeout(serviceClass, null, null, context);
    }
}
