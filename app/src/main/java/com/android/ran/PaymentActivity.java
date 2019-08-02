package com.android.ran;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.ran.model.Cab;
import com.android.ran.model.User;
import com.android.ran.network.APIUtils;
import com.android.ran.network.EndPointInterface;
import com.android.ran.payment.Checksum;
import com.android.ran.payment.Constants;
import com.android.ran.payment.Paytm;
import com.android.ran.util.NotificationsUtils;
import com.google.gson.Gson;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentActivity extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    private static final String TAG = PaymentActivity.class.getSimpleName();
    private String token;
    private User user;
    private Cab cabBooked;
    private String pickup, drop, startTime;
    private Button payAnywayView, payWithPaytmView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Payment Gateway");
        setContentView(R.layout.activity_payment);
        initViews();

        SharedPreferences pref = getSharedPreferences("AppPref", MODE_PRIVATE);
        token = pref.getString("token", "");
        String json = pref.getString("user", "");
        user = new Gson().fromJson(json, User.class);

        Intent intent = getIntent();
        cabBooked = (Cab) intent.getSerializableExtra("confirmed_ride_details");
        pickup = intent.getStringExtra("pickup");
        drop = intent.getStringExtra("drop");
        startTime = intent.getStringExtra("startTime");

        payAnywayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserCab();
            }
        });

        payWithPaytmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateCheckSum();
            }
        });
    }

    private void updateUserCab() {
        EndPointInterface service = APIUtils.getAPIService();
        service.cabUpdate(cabBooked.get_id(), true, pickup, drop, startTime).enqueue(new Callback<Cab>() {
            @Override
            public void onResponse(@NonNull Call<Cab> call, @NonNull Response<Cab> response) {
                Toast.makeText(getApplicationContext(), "Cab booked successfully", Toast.LENGTH_LONG).show();
                NotificationsUtils.remindUserCarBooked(getApplicationContext());
                startActivity(new Intent(PaymentActivity.this, CabBookedActivity.class));
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<Cab> call, @NonNull Throwable t) {
                Log.e(TAG + "On Failure", t.getMessage());
            }
        });

        service.userBookCab(user.get_id(), user.getEmail(), token, cabBooked.get_id()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                Toast.makeText(getApplicationContext(), "A new trip added in your account", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(TAG + "On Failure", t.getMessage());
            }
        });
    }

    private void generateCheckSum() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUtils.PAYMENT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EndPointInterface apiService = retrofit.create(EndPointInterface.class);

        final Paytm paytm = new Paytm(
                Constants.M_ID,
                Constants.CHANNEL_ID,
                cabBooked.getFare(),
                Constants.WEBSITE,
                Constants.CALLBACK_URL,
                Constants.INDUSTRY_TYPE_ID
        );

        Call<Checksum> call = apiService.getChecksum(
                paytm.getmId(),
                paytm.getOrderId(),
                paytm.getCustId(),
                paytm.getChannelId(),
                paytm.getTxnAmount(),
                paytm.getWebsite(),
                paytm.getCallBackUrl(),
                paytm.getIndustryTypeId()
        );

        call.enqueue(new Callback<Checksum>() {
            @Override
            public void onResponse(@NonNull Call<Checksum> call, @NonNull Response<Checksum> response) {
                assert response.body() != null;
                initializePaytmPayment(response.body().getChecksumHash(), paytm);
            }

            @Override
            public void onFailure(@NonNull Call<Checksum> call, @NonNull Throwable t) {

            }
        });
    }

    private void initializePaytmPayment(String checksumHash, Paytm paytm) {
        PaytmPGService Service = PaytmPGService.getStagingService();
        //PaytmPGService Service = PaytmPGService.getProductionService();

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("MID", Constants.M_ID);
        paramMap.put("ORDER_ID", paytm.getOrderId());
        paramMap.put("CUST_ID", paytm.getCustId());
        paramMap.put("CHANNEL_ID", paytm.getChannelId());
        paramMap.put("TXN_AMOUNT", paytm.getTxnAmount());
        paramMap.put("WEBSITE", paytm.getWebsite());
        paramMap.put("CALLBACK_URL", paytm.getCallBackUrl());
        paramMap.put("CHECKSUMHASH", checksumHash);
        paramMap.put("INDUSTRY_TYPE_ID", paytm.getIndustryTypeId());

        PaytmOrder order = new PaytmOrder(paramMap);
        Service.initialize(order, null);
        Service.startPaymentTransaction(this, true, true, this);
    }

    @Override
    public void onTransactionResponse(Bundle bundle) {
        Toast.makeText(this, bundle.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void networkNotAvailable() {
        Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show();
    }

    @Override
    public void clientAuthenticationFailed(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void someUIErrorOccurred(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Toast.makeText(this, "Back Pressed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Toast.makeText(this, s + bundle.toString(), Toast.LENGTH_LONG).show();
    }

    private void initViews() {
        payAnywayView = findViewById(R.id.payment_pay_anyway);
        payWithPaytmView = findViewById(R.id.payment_pay_with_paytm);
    }
}