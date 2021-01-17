package com.example.projectuber.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.example.projectuber.R;

public class SignInActivity extends AppCompatActivity {

    private ImageView imageView_back;
    private EditText editText_phone;
    private Button button_next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        imageView_back=findViewById(R.id.iv_back_SinginActivty);
        editText_phone=findViewById(R.id.ed_phone_signInActivtity);
        button_next=findViewById(R.id.btn_next_SinginActivty);


        button_next.setOnClickListener(view -> {
            String num = editText_phone.getText().toString();
            Intent intent=new Intent(SignInActivity.this, ReceivedOTPActivity.class);
            intent.putExtra("phone",num);
            startActivity(intent);
        });


        imageView_back.setOnClickListener(view -> finish());
    }
}