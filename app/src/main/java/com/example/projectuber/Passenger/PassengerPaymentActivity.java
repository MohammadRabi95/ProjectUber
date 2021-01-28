package com.example.projectuber.Passenger;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectuber.Config.Config;
import com.example.projectuber.DatabaseCalls;
import com.example.projectuber.Driver.DriverPaymentActivity;
import com.example.projectuber.Interfaces.ResponseInterface;
import com.example.projectuber.Maps.PassengerMapsActivity;
import com.example.projectuber.Models.CompletedRide;
import com.example.projectuber.R;
import com.example.projectuber.SelectModeActivity;
import com.example.projectuber.Utils.AppHelper;
import com.example.projectuber.Utils.Constants;
import com.example.projectuber.Utils.RideSession;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import static com.example.projectuber.Utils.Constants.completedRideKey;

public class PassengerPaymentActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PAYPAL_REQUEST_CODE = 7777;
    private static final String TAG = "PassengerPaymentActivit";
    private static final PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);

    private TextView id_tv;
    private TextView status_tv;
    private Button btn_cash, btn_pypl;
    private CompletedRide completedRide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_payment_activty);

        completedRide = (CompletedRide) getIntent().getSerializableExtra(completedRideKey);
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        TextView amount_tv = findViewById(R.id.amount);
        id_tv = findViewById(R.id.id);
        status_tv = findViewById(R.id.status);

        btn_cash = findViewById(R.id.btn_cash);
        btn_pypl = findViewById(R.id.btn_pypl);

        btn_cash.setOnClickListener(this);
        btn_pypl.setOnClickListener(this);

        amount_tv.setText(completedRide.getPrice() + " $");

    }

    private void processPayment() {
        String amount = completedRide.getPrice().replace(" $", "");
        String txt = "Payment For Ride with " + completedRide.getDriverName() + "\nDriver Id: "
                + completedRide.getDriverId();
        Log.d(TAG, "processPayment: " + amount);
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(amount),
                "USD", txt, PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    setPaymentType(Constants.PAID_VIA_PAYPAL);
                    JSONObject paymentDetails = confirmation.toJSONObject();
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = paymentDetails.getJSONObject("response");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        setDataForReceipts(jsonObject);
                    }
                    Log.d(TAG, "onActivityResult: " + paymentDetails);
                }
            } else if (resultCode == Activity.RESULT_CANCELED)
                AppHelper.showSnackBar(findViewById(android.R.id.content), "Canceled");
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
            AppHelper.showSnackBar(findViewById(android.R.id.content), "Invalid");

    }

    private void setDataForReceipts(JSONObject jsonObject) {
        id_tv.setVisibility(View.VISIBLE);
        btn_pypl.setVisibility(View.GONE);
        btn_cash.setVisibility(View.GONE);
        findViewById(R.id.or).setVisibility(View.GONE);
        try {
            id_tv.setText("Id: " + jsonObject.getString("id"));
            status_tv.setText("Status: " + jsonObject.getString("state"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cash:
                setPaymentType(Constants.PAID_VIA_CASH);
                break;

            case R.id.btn_pypl:
                processPayment();
                break;
        }
    }

    private void setPaymentType(String method) {
        DatabaseCalls.setPaymentMethod(this, completedRide.getId(), method, new ResponseInterface() {
            @Override
            public void onResponse(Object... params) {
                if ((boolean) params[0]) {
                    if (method.equals(Constants.PAID_VIA_PAYPAL)) {
                        DatabaseCalls.setPaymentPaid(PassengerPaymentActivity.this, completedRide.getId(),
                                true, new ResponseInterface() {
                                    @Override
                                    public void onResponse(Object... params) {
                                        startActivity(new Intent(PassengerPaymentActivity.this, SelectModeActivity.class));
                                        finish();
                                        RideSession.resetRideSession(PassengerPaymentActivity.this);
                                    }
                                    @Override
                                    public void onError(String error) {

                                    }
                                });
                    } else if (method.equals(Constants.PAID_VIA_CASH)){
                        isAmountPaid();
                    }
                }
            }

            @Override
            public void onError(String error) {
                AppHelper.showSnackBar(findViewById(android.R.id.content), error);
            }
        });
    }

    private void isAmountPaid() {
        DatabaseCalls.isAmountPaid(this, completedRide.getId(), new ResponseInterface() {
            @Override
            public void onResponse(Object... params) {
                if ((boolean) params[0]) {
                    RideSession.resetRideSession(PassengerPaymentActivity.this);
                    startActivity(new Intent(PassengerPaymentActivity.this, SelectModeActivity.class));
                    finish();
                }
            }

            @Override
            public void onError(String error) {
                AppHelper.showSnackBar(findViewById(android.R.id.content), error);
            }
        });
    }
}