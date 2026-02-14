package com.allein.freund.authapp;


import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.allein.freund.authapp.remote.APIService;
import com.allein.freund.authapp.remote.APIUtils;
import com.allein.freund.authapp.remote.AuthService;
import com.allein.freund.authapp.remote.Invoice;
import com.allein.freund.authapp.remote.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    public static final String INVOICE_ID = "com.allein.freund.authapp.INVOICE_ID";
    public static final String INVOICE_CUSTOMER = "com.allein.freund.authapp.INVOICE_CUSTOMER";
    public static final String INVOICE_SIZE = "com.allein.freund.authapp.INVOICE_SIZE";
    public static final String INVOICE_MONEY = "com.allein.freund.authapp.INVOICE_MONEY";
    private String TAG = "MAIN";
    private APIService mAPIService;
    private String userCookie;
    private List<Invoice> invoiceList;
    private ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        userCookie = intent.getStringExtra(LoginActivity.USER_COOKIE);
        mAPIService = APIUtils.getApiService(this);
        invoiceList = new ArrayList<>();
        ListView invoiceListView = (ListView) findViewById(R.id.listview);
        adapter = new ListViewAdapter(this, invoiceList);
        invoiceListView.setAdapter(adapter);
        invoiceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Invoice invoice = getInvoice(position);
                Log.d(TAG, "itemSelect: position = " + position + ", id = " + invoice.getNumber());
                passToInvoiceDetailsActivity(invoice);
            }
        });

        getInvoices();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getInvoices();
    }

    private Invoice getInvoice(int position) {
        return invoiceList.get(position);
    }

    private void getInvoices() {
        mAPIService.getInvoices().enqueue(new Callback<List<Invoice>>() {
            @Override
            public void onResponse(Call<List<Invoice>> call, Response<List<Invoice>> response) {
                if (response.isSuccessful()) {
                    List<Invoice> invoices = response.body();
                    populateList(invoices);
                } else {
                    Log.i(TAG, "Something goes wrong:" + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Invoice>> call, Throwable t) {
                Log.e(TAG, "Unable to fetch invoices.");
            }
        });
    }

    public void refreshInvoices(View view) {
        getInvoices();
    }

    public void logout(View view) {
        logout();
    }

    private void logout() {
        finish();
        AuthService mAuthService = APIUtils.getAuthService(this);
        mAuthService.logout().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.i(TAG, "Logout.");
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Unable to logout form server:" + t.getMessage());
            }
        });
    }

    private void populateList(List<Invoice> invoices) {
        invoiceList.clear();
        invoiceList.addAll(invoices);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        logout();
    }

    private void passToInvoiceDetailsActivity(Invoice invoice) {
        Intent intent = new Intent(this, InvoiceDetailsActivity.class);
        intent.putExtra(INVOICE_ID, String.valueOf(invoice.getNumber()));
        intent.putExtra(INVOICE_CUSTOMER, invoice.getCustomer());
        intent.putExtra(INVOICE_SIZE, String.valueOf(invoice.getPositions()));
        intent.putExtra(INVOICE_MONEY, String.valueOf(invoice.getMoney()));
//        intent.putExtra("ExtraObj", invoice);
        startActivity(intent);
    }
}
