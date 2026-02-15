package com.allein.freund.authapp;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.allein.freund.authapp.remote.APIService;
import com.allein.freund.authapp.remote.APIUtils;
import com.allein.freund.authapp.remote.InvoiceDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoiceDetailsActivity extends AppCompatActivity {
    private APIService mAPIService;
    private int invoiceId;
    private List<InvoiceDetails> invoiceDetails;
    private InvoiceDetailsAdapter adapter;
    public static final String INVOICE_DETAILS = "com.allein.freund.authapp.INVOICE_DETAILS";
    public static final String INVOICE_ID = "com.allein.freund.authapp.INVOICE_ID";
    private String TAG = "INVOICE_DETAILS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_details);

        Intent intent = getIntent();

        String id = intent.getStringExtra(MainActivity.INVOICE_ID);
        String customer = intent.getStringExtra(MainActivity.INVOICE_CUSTOMER);
        String size = intent.getStringExtra(MainActivity.INVOICE_SIZE);
        String money = intent.getStringExtra(MainActivity.INVOICE_MONEY);

        TextView detailsTitle = (TextView) findViewById(R.id.invoiceDetailsTitle);
        detailsTitle.setText("Order No. " + id);
        TextView detailsCustomer = (TextView) findViewById(R.id.detailsCustomer);
        detailsCustomer.setText(customer);
        TextView detailsSize = (TextView) findViewById(R.id.detailsSize);
        detailsSize.setText("Amount of items: " + size);
        TextView detailsMoney = (TextView) findViewById(R.id.detailsMoney);
        detailsMoney.setText("Total cost: " + money + " $");

        mAPIService = APIUtils.getApiService(this);
        invoiceId = Integer.parseInt(id);
        invoiceDetails = new ArrayList<>();

        ListView detailsListView = (ListView) findViewById(R.id.detailsListView);
        adapter = new InvoiceDetailsAdapter(this, invoiceDetails);
        detailsListView.setAdapter(adapter);

        getInvoiceDetails();
    }

    private void getInvoiceDetails() {
        mAPIService.getInvoiceDetails(invoiceId).enqueue(new Callback<List<InvoiceDetails>>() {
            @Override
            public void onResponse(Call<List<InvoiceDetails>> call, Response<List<InvoiceDetails>> response) {
                if (response.isSuccessful()) {
                    List<InvoiceDetails> details = response.body();
                    setInvoiceDetails(details);
                } else {
                    Log.i(TAG, "Something goes wrong:" + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<InvoiceDetails>> call, Throwable t) {
                Log.e(TAG, "Unable to fetch details.");
            }
        });
    }

    private void setInvoiceDetails(List<InvoiceDetails> details) {
        invoiceDetails.clear();
        invoiceDetails.addAll(details);
        adapter.notifyDataSetChanged();

    }

    public void refreshInvoice(View view) {
        getInvoiceDetails();
    }

    public void backToMain(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void scan(View view) {
        passToScanActivity();
    }

    private void passToScanActivity() {

        Gson gson = new Gson();
        Type type = new TypeToken<List<InvoiceDetails>>() {
        }.getType();
        String json = gson.toJson(invoiceDetails, type);
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(INVOICE_DETAILS, json);
        intent.putExtra(INVOICE_ID, String.valueOf(invoiceId));
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1) {
            finish();
        }
    }
}
