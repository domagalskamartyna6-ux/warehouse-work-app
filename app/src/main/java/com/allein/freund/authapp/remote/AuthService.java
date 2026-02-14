package com.allein.freund.authapp.remote;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthService {

    // PrestaShop login endpoint
    @FormUrlEncoded
    @POST("pickingapi?method=loginEmployee")
    Call<User> sendCredentials(
            @Field("email") String email,
            @Field("pass") String pass
    );

    // opcjonalnie – jeśli masz logout w backendzie
    @GET("pickingapi?method=logoutEmployee")
    Call<User> logout();
}
