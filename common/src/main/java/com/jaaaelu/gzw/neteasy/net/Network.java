package com.jaaaelu.gzw.neteasy.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jaaaelu.gzw.neteasy.common.Common;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by admin on 2017/7/7.
 */

public class Network {
    private static Network sInstance = new Network();
    private Retrofit mRetrofit;

    static {
    }

    private Network() {
    }

    public static Retrofit getRetrofit() {
        if (sInstance.mRetrofit != null) {
            return sInstance.mRetrofit;
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                //  设置时间格式
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .create();

        Retrofit.Builder builder = new Retrofit.Builder();
        sInstance.mRetrofit = builder.baseUrl(Common.Constance.API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return sInstance.mRetrofit;
    }

    static BookService getBookService() {
        return getRetrofit().create(BookService.class);
    }
}
