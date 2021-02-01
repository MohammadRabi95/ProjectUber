package com.vcrow.rides.Driver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.vcrow.rides.DatabaseCalls;
import com.vcrow.rides.Interfaces.ResponseInterface;
import com.vcrow.rides.Maps.DriverMapsActivity;
import com.vcrow.rides.Models.Car;
import com.vcrow.rides.R;
import com.vcrow.rides.Utils.AppHelper;
import com.vcrow.rides.Utils.CurrentUser;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class DriverCarDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int LICENSE_IMAGE = 111;
    private static final int CAR_IMAGE = 222;
    private TextInputEditText name_et, number_et, manuf_et, model_et, color_et, license_et;
    private ImageView license_iv, car_iv;
    private Uri li_path, car_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_car_details);
        name_et = findViewById(R.id.car_name);
        number_et = findViewById(R.id.reg_num);
        manuf_et = findViewById(R.id.manuf_name);
        model_et = findViewById(R.id.model_year);
        color_et = findViewById(R.id.color);
        license_et = findViewById(R.id.license_num);

        license_iv = findViewById(R.id.driver_iv);
        car_iv = findViewById(R.id.car_iv);

        Button register = findViewById(R.id.reg);

        license_iv.setOnClickListener(this);
        car_iv.setOnClickListener(this);
        register.setOnClickListener(this);

    }

    private void galleryIntent(int code) {
        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, code);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data.getData() != null) {
            switch (requestCode) {
                case LICENSE_IMAGE:
                    li_path = data.getData();
                    Picasso.get().load(li_path).fit().centerCrop().noPlaceholder().into(license_iv);
                    break;
                case CAR_IMAGE:
                    car_path = data.getData();
                    Picasso.get().load(car_path).fit().centerCrop().noPlaceholder().into(car_iv);
                    break;
                default:
                    break;
            }
        }

    }

    private boolean validate() {
        if (Objects.requireNonNull(name_et.getText()).toString().isEmpty()) {
            name_et.setError("Field Cannot be Empty");
            name_et.requestFocus();
            return false;
        } else if (Objects.requireNonNull(number_et.getText()).toString().isEmpty()) {
            number_et.setError("Field Cannot be Empty");
            number_et.requestFocus();
            return false;
        } else if (Objects.requireNonNull(manuf_et.getText()).toString().isEmpty()) {
            manuf_et.setError("Field Cannot be Empty");
            manuf_et.requestFocus();
            return false;
        } else if (Objects.requireNonNull(model_et.getText()).toString().isEmpty()) {
            model_et.setError("Field Cannot be Empty");
            model_et.requestFocus();
            return false;
        } else if (Objects.requireNonNull(color_et.getText()).toString().isEmpty()) {
            color_et.setError("Field Cannot be Empty");
            color_et.requestFocus();
            return false;
        } else if (Objects.requireNonNull(license_et.getText()).toString().isEmpty()) {
            license_et.setError("Field Cannot be Empty");
            license_et.requestFocus();
        } else if (li_path == null) {
            AppHelper.showSnackBar(findViewById(android.R.id.content),
                    "Please Select the Driving License Image to proceed");
            return false;
        } else if (car_path == null) {
            AppHelper.showSnackBar(findViewById(android.R.id.content),
                    "Please Select the your Car Image to proceed");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reg:
                if (validate())
                    registerDriver();
                break;
            case R.id.car_iv:
                galleryIntent(CAR_IMAGE);
                break;
            case R.id.driver_iv:
                galleryIntent(LICENSE_IMAGE);
                break;
        }
    }

    private void registerDriver() {
        Car car = new Car();
        car.setColor(color_et.getText().toString());
        car.setCompany(manuf_et.getText().toString());
        car.setDriverId(CurrentUser.getUserId());
        car.setId(CurrentUser.getUserId());
        car.setModel(model_et.getText().toString());
        car.setName(name_et.getText().toString());
        car.setNumber(number_et.getText().toString());
        car.setLicenseNumber(license_et.getText().toString());

        DatabaseCalls.registerDriverCall(this, car, li_path,
                car_path, new ResponseInterface() {
                    @Override
                    public void onResponse(Object... params) {
                        if ((boolean) params[0]) {
                            startActivity(new Intent(DriverCarDetailsActivity.this, DriverMapsActivity.class));
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