package com.allein.freund.authapp.remote;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;
    private static final String AUTH_KEY = "aDJucjNPNExWVHBYXg1TVl6NERrTHZaOGF1N2FOamdFY3g0ZA==";

    public static Retrofit getClient(String baseUrl, Context context) {

        SharedPreferences prefs = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {

                        Request original = chain.request();
                        Request.Builder builder = original.newBuilder()
                                .header("AUTH_KEY", AUTH_KEY);

                        String idEmployee = prefs.getString("ID_EMPLOYEE", null);
                        String skey = prefs.getString("SKEY", null);

                        if (idEmployee != null && skey != null) {
                            builder.header("ID_EMPLOYEE", idEmployee);
                            builder.header("SKEY", skey);
                        }

                        Request request = builder.build();
                        return chain.proceed(request);
                    }
                })
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }
}
