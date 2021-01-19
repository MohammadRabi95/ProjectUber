package com.example.projectuber.Auth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.projectuber.Models.Driver;
import com.example.projectuber.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class GetingCredentialsActivity extends AppCompatActivity {

    private TextInputEditText editText_Fname,editText_email,editText_Lname,editText_password,editText_confirm_password;
    private Button button_register;
    private TextView textView_goto_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geting_credentials);

        editText_Fname=findViewById(R.id.et_first_name_register_getingCredentials);
        editText_Lname=findViewById(R.id.et_second_name_register_getingCredentials);
        editText_email=findViewById(R.id.et_email_register_getingCredentials);
        editText_password=findViewById(R.id.et_password_register_getingCredentials);
        editText_confirm_password=findViewById(R.id.et_confirm_password_register_getingCredentials);
        textView_goto_login=findViewById(R.id.tv_login_title_register);
        button_register=findViewById(R.id.btn_register_getingCredentials);

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ValidFields();
            }
        });

    }

    private void ValidFields() {
        if(editText_Fname.getText().toString().equalsIgnoreCase(""))
        {
            editText_Fname.setError("Required");
        }
        else if(editText_Lname.getText().toString().equalsIgnoreCase(""))
        {
            editText_Lname.setError("Required");
        }
        else if (editText_email.getText().toString().equalsIgnoreCase(""))
        {
            editText_email.setError("Required");
        }
        else if(editText_password.getText().toString().equalsIgnoreCase(""))
        {
            editText_password.setError("Required");
        }
        else if(editText_confirm_password.getText().toString().equalsIgnoreCase(""))
        {
            editText_confirm_password.setError("Required");
        }
        else if(!editText_password.getText().toString().equals(editText_confirm_password.getText().toString()))
        {
            AlertDialog.Builder builder =new AlertDialog.Builder(this);
            builder.setTitle("Alert");
            builder.setMessage("Passwords doesn't match");
            builder.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.show();
            editText_password.setText("");
            editText_confirm_password.setText("");
            editText_password.requestFocus();
        }
        else if(editText_password.getText().toString().length()<6)
        {
            AlertDialog.Builder builder =new AlertDialog.Builder(this);
            builder.setTitle("Alert");
            builder.setMessage("Passwords must contain at least 6 char.");
            builder.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.show();
            editText_password.requestFocus();
        }
        else if(editText_email.getText().toString().contains(".") || editText_email.getText().toString().contains("@") || editText_email.getText().toString().contains("com") )
        {
            AlertDialog.Builder builder =new AlertDialog.Builder(this);
            builder.setTitle("Alert");
            builder.setMessage("Email formatting is incorrect.\nEmail must contain '.' , '@' , 'com'.");
            builder.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.show();
            editText_email.requestFocus();
        }
        else {

            Driver driver=new Driver();
            driver.setEmail(editText_email.getText().toString());
            //driver.setPhone();
            driver.setFname(editText_Fname.getText().toString());
            driver.setLname(editText_Lname.getText().toString());
            driver.setPassword(editText_password.getText().toString());

            // Here you can put method
        }
    }
}