package com.allein.freund.authapp.remote;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private Data data;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public Data getData() { return data; }

    public static class Data {
        @SerializedName("id_employee")
        @Expose
        private int idEmployee;

        @SerializedName("skey")
        @Expose
        private String skey;

        public int getIdEmployee() { return idEmployee; }
        public String getSkey() { return skey; }
    }
}
