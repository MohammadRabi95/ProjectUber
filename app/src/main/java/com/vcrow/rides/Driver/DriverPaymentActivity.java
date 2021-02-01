package com.vcrow.rides.Driver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.vcrow.rides.DatabaseCalls;
import com.vcrow.rides.Interfaces.ResponseInterface;
import com.vcrow.rides.Models.RideProgress;
import com.vcrow.rides.R;
import com.vcrow.rides.SelectModeActivity;
import com.vcrow.rides.Utils.AppHelper;
import com.vcrow.rides.Utils.Constants;
import com.vcrow.rides.Utils.RideSession;

public class DriverPaymentActivity extends AppCompatActivity {

    private TextView status_tv, id_tv, amount_tv;
    private Button receive_btn;
    private boolean isPaymentChecked = false;
    private RideProgress rideProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_payment_activty);

        rideProgress = (RideProgress) getIntent().getSerializableExtra("model");

        status_tv = findViewById(R.id.status);
        id_tv = findViewById(R.id.id);
        amount_tv = findViewById(R.id.amount);

        receive_btn = findViewById(R.id.btn_rec);

        amount_tv.setText(rideProgress.getPrice() + " $");

        checkPayment();

        receive_btn.setOnClickListener(view -> {
            if (isPaymentChecked) {
                DatabaseCalls.setPaymentPaid(this, rideProgress.getId(),
                        true, new ResponseInterface() {
                            @Override
                            public void onResponse(Object... params) {
                                if ((boolean) params[0]) {
                                    RideSession.resetRideSession(DriverPaymentActivity.this);
                                    startActivity(new Intent(DriverPaymentActivity.this, SelectModeActivity.class));
                                    finish();
                                }
                            }

                            @Override
                            public void onError(String error) {
                                AppHelper.showSnackBar(findViewById(android.R.id.content), error);
                            }
                        });
            }
        });


    }

    private void checkPayment() {
        DatabaseCalls.isPaying(this, rideProgress.getId(), new ResponseInterface() {
            @Override
            public void onResponse(Object... params) {
                String paidVia = (String) params[0];
                if (!"".equals(paidVia)) {
                    if (Constants.PAID_VIA_CASH.equals(paidVia)) {
                        receive_btn.setClickable(true);
                        isPaymentChecked = true;
                    } else if (Constants.PAID_VIA_PAYPAL.equals(paidVia)) {
                        receive_btn.setClickable(false);
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
        DatabaseCalls.isAmountPaid(this, rideProgress.getId(),
                new ResponseInterface() {
                    @Override
                    public void onResponse(Object... params) {
                        if ((boolean) params[0]) {
                            RideSession.resetRideSession(DriverPaymentActivity.this);
                            startActivity(new Intent(DriverPaymentActivity.this, SelectModeActivity.class));
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