package com.allein.freund.authapp.remote;

import android.content.Context;

/**
 * Created by freund on 1/9/18.
 */

public class APIUtils {

    private APIUtils() {
    }

    public static final String BASE_URL = "http://diluonline.eu/";

    public static AuthService getAuthService(Context context) {
        return RetrofitClient.getClient(BASE_URL, context).create(AuthService.class);
    }

    public static APIService getApiService(Context context) {
        return RetrofitClient.getClient(BASE_URL, context).create(APIService.class);
    }
}
