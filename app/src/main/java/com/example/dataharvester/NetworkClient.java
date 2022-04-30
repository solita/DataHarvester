package com.example.dataharvester;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {
    private  static final String BASE_URL = "https://racekernel.com/dataharvester/";
    private static NetworkClient mInstance;
    private Retrofit retrofit;

    private NetworkClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized NetworkClient getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkClient();
        }
        return mInstance;
    }

    public UploadApis getAPI() {
        return retrofit.create(UploadApis.class);
    }
}
