package com.example.projectuber.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.projectuber.Maps.MapsActivity;
import com.example.projectuber.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class ReceivedOTPActivity extends AppCompatActivity {

    private static final String KEY_VERIFICATION_ID = "key_verification_id";
    private static final String TAG = "OtpVerificationFragment";
    private TextView textView_title_with_phone, textView_resendOTP;
    private ImageView imageView_back_button;
    private Button button_next;
    private EditText editText_otp_code;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private String mobNum = "";
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                editText_otp_code.setText(code);
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            //AppHelper.showSnakeBar(), e.getMessage());
            Log.e(TAG, "onVerificationFailed: ", e);
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
            //mResendToken = forceResendingToken;
        }
    };

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_VERIFICATION_ID, mVerificationId);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationId = savedInstanceState.getString(KEY_VERIFICATION_ID);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_o_t_p);
        if (mVerificationId == null && savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mobNum = extras.getString("phone");
        } else {
            mobNum = "null";
        }
        mAuth = FirebaseAuth.getInstance();
        textView_title_with_phone = findViewById(R.id.tv_title_input_enter_number_SinginActivty);
        textView_resendOTP = findViewById(R.id.tv_resendCode_OTPActivity);
        imageView_back_button = findViewById(R.id.iv_back_ReceivedOTPActivity);
        button_next = findViewById(R.id.btn_next_OTPActivity);
        editText_otp_code = findViewById(R.id.et_OTP_code_OTPActivity);
        textView_title_with_phone.setText("Enter the 4-digit code sent to you at " + mobNum);
        imageView_back_button.setOnClickListener(view -> finish());
        button_next.setOnClickListener(view1 -> {
            String code = editText_otp_code.getText().toString().trim();
            if (code.isEmpty() || code.length() < 6) {
                editText_otp_code.setError("Enter valid code");
                editText_otp_code.requestFocus();
                return;
            }
            verifyVerificationCode(code);
        });
        sendVerificationCode(mobNum);
    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(mobile)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyVerificationCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(this, MapsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        String message = "Something is wrong, we will fix it soon...";
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            message = "Invalid code entered...";
                        }
                        // AppHelper.showSnakeBar(Objects.requireNonNull(getView()), message);
                    }
                });
    }
}