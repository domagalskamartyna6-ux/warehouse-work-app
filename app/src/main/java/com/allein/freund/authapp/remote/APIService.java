package com.allein.freund.authapp.remote;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by freund on 1/10/18.
 */

public interface APIService {
    @GET("pickingapi?method=getPicking")
    Call<List<Invoice>> getInvoices();

    @GET("pickingapi?method=getCartList")
    Call<List<InvoiceDetails>> getInvoiceDetails(@Query("id_order") int invoiceId);

    @POST("pickingapi?method=updateStatus")
    Call<String> sendInvoiceComplected(@Query("id_order") int invoiceId);
}
